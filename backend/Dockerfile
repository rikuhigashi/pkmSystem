# 构建阶段：使用最新的 Java 23 镜像
FROM eclipse-temurin:23-jdk-jammy as builder

WORKDIR /workspace/app

# 复制构建必需文件
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 构建命令（显式指定Java版本）
RUN chmod +x gradlew && \
    ./gradlew --no-daemon --stacktrace --info -x test build && \
    mkdir -p build/dependency && \
    (cd build/dependency; jar -xf ../libs/*.jar)

# 运行阶段：与构建阶段一致
FROM eclipse-temurin:23-jre-jammy

# 时区配置（与代码中的UTC一致）
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安全配置
RUN useradd -m appuser && \
    mkdir -p /app/uploads && \
    chown -R appuser:appuser /app
USER appuser

WORKDIR /app

# 复制依赖
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=builder --chown=appuser:appuser ${DEPENDENCY}/BOOT-INF/lib ./lib
COPY --from=builder --chown=appuser:appuser ${DEPENDENCY}/META-INF ./META-INF
COPY --from=builder --chown=appuser:appuser ${DEPENDENCY}/BOOT-INF/classes ./

EXPOSE 10000

# JVM优化参数
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC"

# 启动命令
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.example.backend.BackendApplication"]