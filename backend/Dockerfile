# ===== 构建阶段 =====
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .

# 显式配置阿里云镜像
ENV MAVEN_REPO_URL="https://maven.aliyun.com/repository/public"
RUN gradle build -x test --no-daemon \
    -Dorg.gradle.jvmargs="-Xmx512m" \
    -Dorg.gradle.daemon=false \
    -Dorg.gradle.internal.repository.mirror.override=true \
    -Dorg.gradle.internal.repository.mirror.mavenCentral="$MAVEN_REPO_URL"

# ===== 运行阶段 =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 10000
CMD ["java", "-Xmx384m", "-jar", "app.jar"]