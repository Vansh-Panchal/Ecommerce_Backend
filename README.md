# 🛒 Shopyverse — Backend (Spring Boot)

> A robust, scalable, and secure RESTful backend for the **Shopyverse** e-commerce platform, built with Java & Spring Boot.

---

## 📌 Table of Contents

- [About the Project](#-about-the-project)
- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Project Architecture](#-project-architecture)
- [Database Schema](#-database-schema)
- [API Endpoints](#-api-endpoints)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Environment Variables](#environment-variables)
  - [Running the Application](#running-the-application)
- [Payment Integration](#-payment-integration--razorpay)
- [Authentication & Security](#-authentication--security)
- [Folder Structure](#-folder-structure)
- [Contributing](#-contributing)
- [License](#-license)
- [Connect with Me](#-connect-with-me)

---

## 📖 About the Project

**Shopyverse** is a full-stack e-commerce web application. This repository contains the **backend** built with **Java & Spring Boot**, exposing secure RESTful APIs consumed by the React.js frontend.

The backend handles:
- User registration, login & authentication via **JWT**
- Product, category & inventory management
- Shopping cart & wishlist operations
- Order placement & management
- Secure payment processing via **Razorpay**
- Role-based access for **Admin** and **Customer**

> 🔗 Frontend Repository: [shopyverse-frontend](https://github.com/Vansh-Panchal/shopyverse-frontend)

---

## 🛠️ Tech Stack

| Layer              | Technology                          |
|--------------------|--------------------------------------|
| Language           | Java 17+                             |
| Framework          | Spring Boot 3.x                      |
| Security           | Spring Security + JWT                |
| Database           | MySQL                                |
| ORM                | Spring Data JPA / Hibernate          |
| Payment Gateway    | Razorpay                             |
| Build Tool         | Maven                                |
| API Style          | RESTful APIs                         |
| Dev Tools          | Lombok, ModelMapper                  |
| Testing            | Postman (API Testing)                |

---

## ✨ Features

- ✅ User Registration & Login with **JWT Authentication**
- ✅ **Role-Based Access Control** — Admin & Customer roles
- ✅ Product CRUD (Create, Read, Update, Delete) — Admin only
- ✅ Category Management
- ✅ Product Search, Filter & Sort
- ✅ Shopping Cart — Add, Update, Remove items
- ✅ Wishlist Management
- ✅ Order Placement & Order History
- ✅ Order Status Management — Admin can update order status
- ✅ **Razorpay Payment Gateway** Integration
- ✅ Secure Password Hashing with **BCrypt**
- ✅ Global Exception Handling
- ✅ Input Validation & Error Responses
- ✅ CORS Configuration for Frontend Integration

---

## 🏗️ Project Architecture

The backend follows a clean **Layered Architecture**:

```
Client (React Frontend)
        │
        ▼
  [ Controller Layer ]   ← Handles HTTP Requests & Responses
        │
        ▼
  [ Service Layer ]      ← Business Logic
        │
        ▼
  [ Repository Layer ]   ← Data Access (JPA Repositories)
        │
        ▼
  [ MySQL Database ]     ← Persistent Storage
```

**Design Pattern:** MVC (Model-View-Controller)  
**Security Layer:** JWT Filter intercepts every request before reaching the Controller.

---

## 🗄️ Database Schema

> Key Entities & Relationships:

```
USER ──────────────── ORDER
 │                      │
 │                   ORDER_ITEM
 │                      │
CART ──────────────── PRODUCT
 │                      │
WISHLIST            CATEGORY
```

**Main Tables:**

| Table          | Description                          |
|----------------|--------------------------------------|
| `users`        | Stores user info & roles             |
| `products`     | Product details & inventory          |
| `categories`   | Product categories                   |
| `cart`         | User's active cart                   |
| `cart_items`   | Individual items in cart             |
| `wishlist`     | User's wishlist                      |
| `orders`       | Placed orders                        |
| `order_items`  | Products within an order             |
| `payments`     | Payment records from Razorpay        |

---

## 📡 API Endpoints

### 🔐 Auth APIs
| Method | Endpoint               | Description            | Access  |
|--------|------------------------|------------------------|---------|
| POST   | `/api/auth/register`   | Register a new user    | Public  |
| POST   | `/api/auth/login`      | Login & get JWT token  | Public  |

### 👤 User APIs
| Method | Endpoint               | Description            | Access  |
|--------|------------------------|------------------------|---------|
| GET    | `/api/users/profile`   | Get user profile       | User    |
| PUT    | `/api/users/profile`   | Update user profile    | User    |

### 📦 Product APIs
| Method | Endpoint                    | Description              | Access  |
|--------|-----------------------------|--------------------------|---------|
| GET    | `/api/products`             | Get all products         | Public  |
| GET    | `/api/products/{id}`        | Get product by ID        | Public  |
| GET    | `/api/products/search`      | Search & filter products | Public  |
| POST   | `/api/products`             | Add new product          | Admin   |
| PUT    | `/api/products/{id}`        | Update product           | Admin   |
| DELETE | `/api/products/{id}`        | Delete product           | Admin   |

### 🗂️ Category APIs
| Method | Endpoint                    | Description              | Access  |
|--------|-----------------------------|--------------------------|---------|
| GET    | `/api/categories`           | Get all categories       | Public  |
| POST   | `/api/categories`           | Add new category         | Admin   |
| PUT    | `/api/categories/{id}`      | Update category          | Admin   |
| DELETE | `/api/categories/{id}`      | Delete category          | Admin   |

### 🛒 Cart APIs
| Method | Endpoint                    | Description              | Access  |
|--------|-----------------------------|--------------------------|---------|
| GET    | `/api/cart`                 | Get user's cart          | User    |
| POST   | `/api/cart/add`             | Add item to cart         | User    |
| PUT    | `/api/cart/update/{itemId}` | Update cart item qty     | User    |
| DELETE | `/api/cart/remove/{itemId}` | Remove item from cart    | User    |
| DELETE | `/api/cart/clear`           | Clear entire cart        | User    |

### ❤️ Wishlist APIs
| Method | Endpoint                        | Description              | Access  |
|--------|---------------------------------|--------------------------|---------|
| GET    | `/api/wishlist`                 | Get user's wishlist      | User    |
| POST   | `/api/wishlist/add/{productId}` | Add to wishlist          | User    |
| DELETE | `/api/wishlist/{productId}`     | Remove from wishlist     | User    |

### 📋 Order APIs
| Method | Endpoint                    | Description              | Access  |
|--------|-----------------------------|--------------------------|---------|
| POST   | `/api/orders/place`         | Place a new order        | User    |
| GET    | `/api/orders/my-orders`     | Get all user orders      | User    |
| GET    | `/api/orders/{id}`          | Get order details        | User    |
| GET    | `/api/orders/all`           | Get all orders           | Admin   |
| PUT    | `/api/orders/{id}/status`   | Update order status      | Admin   |

### 💳 Payment APIs
| Method | Endpoint                     | Description                 | Access  |
|--------|------------------------------|-----------------------------|---------|
| POST   | `/api/payment/create-order`  | Create Razorpay order       | User    |
| POST   | `/api/payment/verify`        | Verify payment signature    | User    |

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- ☕ **Java 17+** — [Download](https://www.oracle.com/java/technologies/downloads/)
- 🐬 **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/)
- 🔧 **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- 💻 **IDE** — IntelliJ IDEA (Recommended) or Eclipse
- 🧪 **Postman** — For API testing

---

### Installation

1. **Clone the repository:**
```bash
git clone https://github.com/Vansh-Panchal/shopyverse-backend.git
cd shopyverse-backend
```

2. **Create the MySQL database:**
```sql
CREATE DATABASE shopyverse_db;
```

3. **Configure environment variables** (see below)

4. **Install dependencies:**
```bash
mvn clean install
```

---

### Environment Variables

Create an `application.properties` file inside `src/main/resources/` and configure:

```properties
# Server
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/shopyverse_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration
jwt.secret=YOUR_JWT_SECRET_KEY
jwt.expiration=86400000

# Razorpay Configuration
razorpay.key.id=YOUR_RAZORPAY_KEY_ID
razorpay.key.secret=YOUR_RAZORPAY_KEY_SECRET

# CORS - Frontend URL
frontend.url=http://localhost:5173
```

> ⚠️ **Never commit your secrets to GitHub!** Add `application.properties` to `.gitignore` and use environment variables in production.

---

### Running the Application

```bash
# Using Maven
mvn spring-boot:run
```

The server will start at:
```
http://localhost:8080
```

---

## 💳 Payment Integration — Razorpay

Shopyverse uses **Razorpay** for secure and seamless payment processing.

**Payment Flow:**

```
1. User clicks "Place Order"
        │
        ▼
2. Backend creates a Razorpay Order  →  POST /api/payment/create-order
        │
        ▼
3. Frontend opens Razorpay Payment Modal
        │
        ▼
4. User completes payment (Card / UPI / Net Banking)
        │
        ▼
5. Backend verifies payment signature  →  POST /api/payment/verify
        │
        ▼
6. Order is confirmed & saved in DB ✅
```

**Setup:**
1. Create a free account at [Razorpay Dashboard](https://dashboard.razorpay.com/)
2. Get your **Key ID** and **Key Secret** from the API Keys section
3. Add them to your `application.properties`

---

## 🔐 Authentication & Security

- **JWT (JSON Web Tokens)** used for stateless authentication
- Tokens are sent in the `Authorization` header as `Bearer <token>`
- **BCrypt** used for secure password hashing
- **Spring Security** filter chain protects all private endpoints
- Role-based access: `ROLE_USER` and `ROLE_ADMIN`

**Request Header Example:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📁 Folder Structure

```
shopyverse-backend/
│
├── src/
│   └── main/
│       ├── java/com/shopyverse/
│       │   ├── controller/        # REST Controllers
│       │   ├── service/           # Business Logic
│       │   ├── repository/        # JPA Repositories
│       │   ├── model/             # Entity Classes
│       │   ├── dto/               # Data Transfer Objects
│       │   ├── security/          # JWT Filter, Security Config
│       │   ├── exception/         # Custom Exceptions & Handler
│       │   ├── config/            # App Configuration (CORS, Beans)
│       │   └── ShopyverseApplication.java
│       │
│       └── resources/
│           └── application.properties
│
├── pom.xml
└── README.md
```

---

## 🤝 Contributing

Contributions are welcome and appreciated! 🙌

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add: your feature description'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — feel free to use, modify, and distribute.

---

## 🌐 Connect with Me

Built with ❤️ by **Vansh Panchal**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Vansh%20Panchal-blue?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/vansh-panchal-77a647237/)
[![GitHub](https://img.shields.io/badge/GitHub-Vansh--Panchal-black?style=for-the-badge&logo=github)](https://github.com/Vansh-Panchal)

> ⭐ If you found this project helpful, please give it a **star** on GitHub — it truly means a lot! 🙏

---

> 🔗 **Frontend Repository:** [shopyverse-frontend](https://github.com/Vansh-Panchal/shopyverse-frontend)
