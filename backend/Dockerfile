# ===== 构建应用 =====
# 使用 Gradle + JDK 23 镜像作为构建环境
FROM gradle:8.7-jdk23 AS build

# 设置工作目录为 /app
WORKDIR /app

# 将当前目录所有文件复制到镜像的 /app 目录
COPY . .

# 执行构建命令：跳过测试，减少构建时间
RUN gradle build -x test


# ===== 运行应用 =====
# 使用轻量级 JRE 23 镜像作为运行环境
FROM eclipse-temurin:23-jre-jammy

# 设置工作目录为 /app
WORKDIR /app

# 从第一阶段（build）复制生成的 JAR 文件到当前镜像
COPY --from=build /app/build/libs/*.jar app.jar

# 暴露端口（Render 要求固定为 10000）
EXPOSE 10000

# 启动命令：运行 JAR 文件
CMD ["java", "-jar", "app.jar"]