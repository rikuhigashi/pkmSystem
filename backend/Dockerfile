# ===== 构建阶段 =====
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .

# 清理Gradle缓存并构建
RUN rm -rf ~/.gradle && \
    gradle build -x test --no-daemon \
    -Dorg.gradle.jvmargs="-Xmx512m -XX:MaxMetaspaceSize=256m" \
    -Dorg.gradle.parallel=true \
    -Dorg.gradle.caching=true

# ===== 运行阶段 =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 10000
CMD ["java", "-Xmx384m", "-XX:MaxRAM=512m", "-jar", "app.jar"]