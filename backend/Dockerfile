# ===== 构建阶段 =====
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# 分步复制文件
COPY build.gradle settings.gradle gradle.properties ./
COPY src src

# 分步构建
RUN gradle dependencies --no-daemon --refresh-dependencies \
    -Dorg.gradle.jvmargs="-Xmx512m -XX:MaxMetaspaceSize=256m" \
    -Dorg.gradle.parallel=false

RUN gradle build -x test --no-daemon \
    -Dorg.gradle.jvmargs="-Xmx512m -XX:MaxMetaspaceSize=256m" \
    -Dorg.gradle.parallel=false

# ===== 运行阶段 =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 10000
CMD ["java", "-Xmx384m", "-XX:MaxRAM=512m", "-jar", "app.jar"]