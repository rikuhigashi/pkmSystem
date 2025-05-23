# 构建阶段
FROM eclipse-temurin:22.0.1_12-jdk-jammy as builder

WORKDIR /workspace/app

# 复制最小文件集（保留构建缓存优化）
COPY gradle/wrapper gradle/wrapper
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 提升构建稳定性
RUN chmod +x gradlew && \
    ./gradlew --no-daemon --stacktrace --info -x test build && \
    mkdir -p build/dependency && \
    (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

# 运行阶段（必须与构建阶段版本一致）
FROM eclipse-temurin:22.0.1_12-jre-jammy

# 时区配置（与代码中的UTC时区冲突，建议统一）
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

# 暴露端口
EXPOSE 10000

# JVM优化参数
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC"

# 启动命令
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.example.backend.BackendApplication"]