# 构建阶段
FROM amazoncorretto:23 as builder

WORKDIR /workspace/app

COPY gradle/wrapper gradle/wrapper
COPY gradlew build.gradle settings.gradle ./
COPY src src

# 安装必要工具
RUN yum install -y file unzip findutils && \
    yum clean all

# 配置镜像源
RUN sed -i '/repositories {/a \    maven { url "https://maven.aliyun.com/repository/public" }' build.gradle

# 构建项目
RUN chmod +x gradlew && \
    ./gradlew --no-daemon --stacktrace --info -x test bootJar

# 验证 JAR 文件
RUN ls -l build/libs/ && \
    [ "$(ls -A build/libs/*.jar 2>/dev/null)" ] || exit 1

# 解压主 JAR
RUN mkdir -p build/dependency && \
    (cd build/dependency; jar -xf ../libs/backend-0.0.1-SNAPSHOT.jar)

# 运行阶段：优化后的生产镜像
FROM amazoncorretto:23

# 1. 安装必要工具（兼容 Amazon Linux 2023）
RUN yum install -y shadow-utils tzdata && \
    yum clean all && \
    rm -rf /var/cache/yum

# 2. 时区与安全配置
ENV TZ=UTC
RUN ln -snf "/usr/share/zoneinfo/$TZ" /etc/localtime && \
    echo "$TZ" > /etc/timezone && \
    useradd --system --user-group --create-home --shell /sbin/nologin appuser && \
    mkdir -p /app/{lib,classes} && \
    chown -R appuser:appuser /app

USER appuser
WORKDIR /app

# 3. 分层次复制依赖（优化镜像层缓存）
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=builder --chown=appuser:appuser /workspace/app/build/dependency/BOOT-INF/lib ./lib/
COPY --from=builder --chown=appuser:appuser /workspace/app/build/dependency/META-INF ./
COPY --from=builder --chown=appuser:appuser /workspace/app/build/dependency/BOOT-INF/classes .

# 4. 现代化 JVM 参数（适配容器编排）
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport \
                      -XX:MaxRAMPercentage=75 \
                      -XX:+ExitOnOutOfMemoryError \
                      -Djava.security.egd=file:/dev/./urandom \
                      -Duser.timezone=UTC"

# 5. 健康检查与优雅停机
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:10000/actuator/health || exit 1

EXPOSE 10000

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.example.backend.BackendApplication"]