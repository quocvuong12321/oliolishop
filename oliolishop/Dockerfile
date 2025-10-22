# Stage 1: build app
# Start with a Maven image that includes JDK 22
# <Pull about the operating system have already Maven and JDK>
FROM maven:3.9.8-sapmachine-21 AS build

# Copy file pom.xml to /app floder
# Copy source code in floder src to /app/src
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with maven
RUN mvn package -DskipTests

#Stage 2: create image
# Start with Amazon Correto JDK 21
FROM openjdk:21-jdk-slim

# Thêm cấu hình Múi giờ cho Việt Nam (Asia/Ho_Chi_Minh)
# Cài đặt tzdata và thiết lập biến môi trường
RUN apt-get update && \
    apt-get install -y tzdata && \
    rm -rf /var/lib/apt/lists/*

ENV TZ Asia/Ho_Chi_Minh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set working folder to App and copy complied file from above step
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Tạo thư mục cho images
RUN mkdir -p /images

# Command to run the application
ENTRYPOINT ["java","-jar","app.jar"]
