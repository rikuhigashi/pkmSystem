# 构建阶段
FROM amazoncorretto:23 as builder

WORKDIR /workspace/app

COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew && \
    ./gradlew --no-daemon --stacktrace --info -x test build && \
    mkdir -p build/dependency && \
    (cd build/dependency; jar -xf ../libs/*.jar)

# 运行阶段
FROM amazoncorretto:23

# 安装用户管理工具
RUN yum install -y shadow-utils && \
    yum clean all

# 时区配置
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建用户和目录
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

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC"

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.example.backend.BackendApplication"]