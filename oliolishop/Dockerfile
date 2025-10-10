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
FROM amazoncorretto:21

# Set working folder to App and copy complied file from above step
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Tạo thư mục cho images
RUN mkdir -p /images

# Command to run the application
ENTRYPOINT ["java","-jar","app.jar"]
