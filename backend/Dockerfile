# 构建阶段
# 构建阶段
FROM eclipse-temurin:22-jdk-jammy as builder

WORKDIR /workspace/app

# 优先复制构建必需的最小文件集
COPY gradle/wrapper gradle/wrapper
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 增加构建调试信息
RUN chmod +x gradlew && \
    ./gradlew --stacktrace --info --no-daemon -x test build && \
    mkdir -p build/dependency && \
    (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)


# 运行阶段
FROM eclipse-temurin:21-jre-jammy

# 设置容器时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 使用非root用户增强安全性
RUN useradd -m appuser
USER appuser

WORKDIR /app

# 从构建阶段复制依赖
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

# 暴露端口（Render默认使用10000，可根据需要修改）
EXPOSE 10000

# 优化JVM参数
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

# 启动命令（包含Flyway迁移）
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.example.backend.BackendApplication"]