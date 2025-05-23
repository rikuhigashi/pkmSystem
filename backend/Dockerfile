# ===== 构建阶段 =====
FROM gradle:8.7-jdk23 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# ===== 生成精简 JRE =====
FROM eclipse-temurin:23-jdk-jammy AS jre-builder
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
RUN jlink --add-modules ALL-MODULE-PATH \
          --strip-debug \
          --no-man-pages \
          --no-header-files \
          --compress=2 \
          --output /jre

# ===== 最终运行阶段 =====
FROM debian:stable-slim
WORKDIR /app
COPY --from=jre-builder /jre /opt/jre
ENV PATH="/opt/jre/bin:${PATH}"
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 10000
CMD ["java", "-jar", "app.jar"]