# ===== 构建阶段 =====
FROM gradle:8.7-jdk23 AS build
WORKDIR /app
COPY . .
# 使用阿里云镜像加速 + 内存限制
RUN gradle build -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx512m"

# ===== 运行阶段 =====
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 10000
CMD ["java", "-Xmx384m", "-jar", "app.jar"]