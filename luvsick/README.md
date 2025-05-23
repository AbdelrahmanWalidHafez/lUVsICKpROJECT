# 🛍️ Luvsick E-Commerce Backend

Welcome to the backend of **Luvsick**, a modern streetwear e-commerce platform targeting young fashion enthusiasts in Egypt. This application provides RESTful APIs for managing products, categories, orders, customers, and users with secure authentication and authorization.

---

## 📦 Tech Stack

- **Java 17**
- **Spring Boot 3**
    - Spring Web
    - Spring Data JPA
    - Spring Security
- **Hibernate**
- **Lombok**
- **PostgreSQL** (or H2 for testing)
- **Maven**
- **JWT (JSON Web Tokens)** for authentication
- **Swagger/OpenAPI** (optional: for API documentation)

---

## 🧩 Features

- 🔐 **User Authentication**
    - Register & login users using email and password
    - Secure JWT token-based authentication

- 👕 **Product Management**
    - Add, edit, delete, and list products with category and size
    - Upload product images using `MultipartFile`

- 🗂️ **Category Management**
    - Add and delete categories
    - Fetch all categories

- 🛒 **Order Management**
    - Create orders
    - Update order status
    - Get paginated and filtered orders

- 👤 **Customer Management**
    - Save and retrieve customer details

- 📧 **Email Notifications**
    - New arrivals
    - Order confirmation
    - Order status updates

---

## 📁 Project Structure

├── config # Spring Security configuration\
├── controller # REST controllers\
├── dto # Data Transfer Objects\
├── mapper # DTO ↔ Entity mappers\
├── model # JPA entities\
├── repo # Spring Data JPA repositories\
├── service # Interfaces and implementations\

## 🛠️ Setup Instructions
spring.application.name=luvsick/
spring.datasource.url=jdbc:postgresql://localhost:5432/yourDB_name\
spring.datasource.username=your_username\
spring.datasource.password=your_password\
spring.jpa.hibernate.ddl-auto=update\
spring.jpa.properties.hibernate.hbm2ddl.auto=update\
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect\
spring.mail.host=smtp.gmail.com\
spring.mail.port=587\
spring.mail.username=your_email\
spring.mail.password=your app_password\
spring.mail.properties.mail.smtp.auth=true\
spring.mail.properties.mail.smtp.starttls.enable=true\
spring.mvc.contentnegotiation.favor-parameter=true\
spring.mvc.contentnegotiation.media-types.json=application/json\
spring.mvc.contentnegotiation.media-types.xml=application/xml\
JWT_SECRET=your_secret\
server.port=8080\
server.error.include-message=always\
server.error.include-binding-errors=always\
server.error.include-stacktrace=never\
server.error.include-exception=false\
server.error.whitelabel.enabled=false\
Configurationspring.mvc.throw-exception-if-no-handler-found=true\
spring.web.resources.add-mappings=false\
springdoc.swagger-ui.path=/swagger-ui.html\
springdoc.swagger-ui.operationsSorter=method\
springdoc.swagger-ui.tagsSorter=alpha\
springdoc.swagger-ui.tryItOutEnabled=true\
springdoc.swagger-ui.filter=true\
springdoc.swagger-ui.syntaxHighlight.activated=true\
springdoc.swagger-ui.syntaxHighlight.theme=monokai\
springdoc.swagger-ui.disable-swagger-default-url=true\
---
### Prerequisites

- Java 17
- Maven
- PostgreSQL (or use embedded H2)
---
### 🚀 API Endpoints
| Method | Endpoint                 | Description                          |
|--------|--------------------------|--------------------------------------|
| POST   | `/auth/register`         | Register a new user                  |
| POST   | `/auth/login`            | Authenticate a user                  |
| GET    | `/auth/getAllUsers`      | Get current user info                |
| POST   | `/category`              | Add a new category                   |
| DELETE | `/category/{id}`         | Delete a category by ID              |
| GET    | `/category`              | Get all categories                   |
| POST   | `/products`              | Add a new product                    |
| DELETE | `/products/{id}`         | Delete a product by ID               |
| PUT    | `/products/{id}`         | Edit a product by ID                 |
| GET    | `/products`              | Get all products (with filters)      |
| GET    | `/products/new-arrivals` | Get new arrivals                     |
| GET    | `/products/image/{id}`   | Get new arrivals                     |
| POST   | `/orders`                | Create a new order                   |
| GET    | `/orders`                | Get orders by status with pagination |
| PUT    | `/orders/{id}/status`    | Update the status of an order        |
- API details are available via Swagger if integrated at /swagger-ui.html
---
### 🧪 Testing
- You can use Postman or Swagger UI to test the endpoints. Unit and integration tests can be added using JUnit and Mockito.
### 💬 Contributing
-  If you're interested in contributing, feel free to fork the project and submit a pull request.
### Clone the Repository

```bash
git clone https://github.com/AbbdelrahmanWalidHafez/luvsick.git
cd luvsick