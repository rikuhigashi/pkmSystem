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

# 构建项目（明确生成可执行 JAR）
RUN chmod +x gradlew && \
    ./gradlew --no-daemon --stacktrace --info -x test bootJar

# 验证 JAR 文件结构
RUN jar tf build/libs/backend-0.0.1-SNAPSHOT.jar | grep BackendApplication.class

# 解压主 JAR（严格匹配 Spring Boot 结构）
RUN mkdir -p build/dependency && \
    (cd build/dependency; jar -xf ../libs/backend-0.0.1-SNAPSHOT.jar)

# 运行阶段
FROM amazoncorretto:23

# 安装必要工具
RUN yum install -y shadow-utils tzdata && \
    yum clean all

# 时区与安全配置
ENV TZ=UTC
RUN ln -snf "/usr/share/zoneinfo/$TZ" /etc/localtime && \
    echo "$TZ" > /etc/timezone && \
    useradd --system --user-group --create-home --shell /sbin/nologin appuser && \
    mkdir -p /app && \
    chown -R appuser:appuser /app

USER appuser
WORKDIR /app

# 精确复制文件结构
COPY --from=builder --chown=appuser:appuser /workspace/app/build/dependency/BOOT-INF/lib ./lib/
COPY --from=builder --chown=appuser:appuser /workspace/app/build/dependency/META-INF ./META-INF/
COPY --from=builder --chown=appuser:appuser /workspace/app/build/dependency/BOOT-INF/classes ./BOOT-INF/classes/

# 启动命令
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC"
ENTRYPOINT ["java", "-cp", "BOOT-INF/classes:BOOT-INF/lib/*", "com.example.backend.BackendApplication"]

EXPOSE 10000