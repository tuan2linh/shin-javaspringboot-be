# Sử dụng Java 17
FROM eclipse-temurin:17-jdk-alpine

# Tạo thư mục làm việc trong container
WORKDIR /app

# Copy file jar vào container
COPY target/*.jar app.jar

# Chạy ứng dụng khi container start
ENTRYPOINT ["java", "-jar", "app.jar"]
