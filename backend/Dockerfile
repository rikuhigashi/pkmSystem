# ===== 构建阶段 =====
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .

# 配置阿里云镜像加速 + 内存限制
RUN sed -i 's/mavenCentral()/maven { url "https:\/\/maven.aliyun.com\/repository\/public" }\n    mavenCentral()/g' build.gradle
RUN gradle build -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx512m"

# ===== 运行阶段 =====
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 10000
CMD ["java", "-Xmx384m", "-jar", "app.jar"]