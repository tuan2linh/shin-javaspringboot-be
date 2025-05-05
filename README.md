# 🛍️ Spring Boot Mini E-commerce Backend

> A stateless RESTful API backend system for e-commerce built with Spring Boot.

## 🏷️ Introduction

### About Spring Boot
Spring Boot is an opinionated framework that simplifies Java application development by providing:
- **Auto-configuration**: Smart defaults based on dependencies
- **Standalone**: Full-stack Spring applications with embedded servers
- **Production-ready**: Built-in security, health checks & metrics

### Tech Stack
- ☕ Java 17 
- 🚀 Spring Boot
- 🔒 Spring Security
- 💾 Spring Data JPA
- 🗄️ MySQL
- 📚 Swagger (springdoc-openapi)
- 🔑 JWT Authentication

### Key Features
<details>
<summary>🔐 Authentication</summary>

- Register/Login functionality
- Logout with token blacklisting
- JWT-based authentication
- Automatic refresh token rotation
- Access token blacklist mechanism
</details>

<details>
<summary>📦 Product Management</summary>

- CRUD operations for products
- Product search by name
- Pagination support
</details>

<details>
<summary>🧩 Category Management</summary>

- CRUD operations for categories  
- Product-category relationship management
</details>

<details>
<summary>👤 Customer Management</summary>

- Customer information management
- One-to-many user-customer relationship
- Customer listing by user
</details>

<details>
<summary>🛒 Shopping Cart</summary>

- Add/remove/update cart items
- View current cart
</details>

<details>
<summary>🧾 Order Management</summary>

- Create orders from cart
- Order management by user/customer
- View, update and delete orders
</details>

## 📂 Project Structure
```
src/main/java/com/example/demojpa/
├── entity/       # JPA entity classes
├── repository/   # Data access interfaces
├── controller/   # REST endpoints
├── service/      # Business logic
├── dto/          # Data transfer objects
├── config/       # Configurations
└── security/     # Security components
```

## 🚀 Running the Application

### Option 1: Using Docker

1. Build and run with Docker Compose:
```bash
# Build the project first
mvn clean package -DskipTests

# Start containers
docker-compose up -d --build
```

### Option 2: Local Development

1. Configure MySQL database:
- Create database named `shin_shop`
- Import initial data from `db/init.sql`
- Update `application.properties` if needed:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shin_shop
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
```

2. Build and run:
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 📑 API Documentation
- Access Swagger UI: http://localhost:8080/swagger-ui/index.html
- Default admin credentials:
  - Username: `admin`
  - Password: `123456`

## 📌 Future Improvements
- Cloud storage integration (S3, Cloudinary) for product images
- Shipping and payment integration
- Order confirmation emails
- CI/CD pipeline
- Cloud deployment (VPS/Azure)

## 🧪 Testing APIs
- Use Postman or Swagger UI
- Authentication header format: `Authorization: Bearer {access_token}`
