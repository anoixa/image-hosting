# 使用多阶段构建
# 第一阶段：构建阶段
FROM gradle:7.6-jdk17 AS builder

# 设置工作目录
WORKDIR /app

# 复制Gradle文件
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY image-hosting-common/build.gradle /app/image-hosting-common/
COPY image-hosting-images/build.gradle /app/image-hosting-images/
COPY image-hosting-system/build.gradle /app/image-hosting-system/
COPY image-hosting-starter/build.gradle /app/image-hosting-starter/
COPY image-hosting-framework/build.gradle /app/image-hosting-framework/

# 尝试解析依赖项
RUN ./gradlew dependencies || true

# 复制源代码
COPY . /app/

# 执行构建
RUN ./gradlew clean :image-hosting-starter:bootJar

# 第二阶段：运行阶段
FROM openjdk:17-jdk-slim

WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai

# 复制JAR包
COPY --from=builder /app/image-hosting-starter/build/libs/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]