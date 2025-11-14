# DoveNet 🕊️

![Java](https://img.shields.io/badge/Java-17+-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue)
![Maven](https://img.shields.io/badge/Maven-3.9-red)
![License](https://img.shields.io/badge/License-MIT-yellow)
![Build](https://img.shields.io/badge/build-passing-brightgreen) 
![JWT](https://img.shields.io/badge/JWT-implemented-orange)

**DoveNet** is a backend application (with frontend separately developed - https://github.com/kkotorov/dovenet-ui) for pigeon breeders to manage their pigeons. Users can register, verify accounts, manage pigeons, track competitions, reset passwords, and generate PDF pedigree trees.  

This project is designed with maintainability and security in mind and serves as a showcase for backend development skills, API design, and Java/Spring Boot expertise.

---

- **Live WebClient:** [DoveNet UI](https://dovenet.eu)  
- **Live Video:** [DoveNet UI](https://youtu.be/ezMFsSjKeSA)  

## Features

### User Management
* Register new users with email verification
* Secure password storage (hashed)
* Password reset via token
* Update email and password
* JWT-based authentication for secure login

### Pigeon Management
* Add, delete, update pigeons
* Track pigeon details (age, color, status, gender, ring number)
* View pigeon stats and history
* View pigeon parents and ancestry
* Generate PDF pedigree tree

### Filtering & Searching
* Filter pigeons by name, color, gender, status, birthdate, or competition participation
* Advanced search by partial or exact match
* Sort pigeons by name, color, status, or birthdate

---

## Tech Stack

* **Backend:** Java 17+, Spring Boot, Spring Data JPA  
* **Database:** PostgreSQL  
* **ORM:** Hibernate  
* **Frontend https://github.com/kkotorov/dovenet-ui :** React.js  
* **PDF Generation:** iText  

---

## Getting Started

### Prerequisites

* Java 17+
* Maven
* PostgreSQL
* IDE (IntelliJ, VSCode, Eclipse, etc.)

### Configuration

1. Create a PostgreSQL database (e.g., `dovenet`).
2. Configure `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/dovenet
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

server.port=8080
```

Run Backend
```
mvn spring-boot:run
```
The backend API will run on ```http://localhost:8080/api```

# API Endpoints

## Users

| Method | Endpoint                          | Description                       |
| ------ | --------------------------------- | --------------------------------- |
| POST   | /api/users                        | Register a new user               |
| GET    | /api/users                        | Get all users (admin only)        |
| POST   | /api/users/verify                 | Verify email with token           |
| POST   | /api/users/password/reset         | Initiate password reset           |
| POST   | /api/users/password/reset/confirm | Confirm password reset with token |
| PUT    | /api/users/email                  | Change user email                 |
| PUT    | /api/users/password               | Change user password              |
| DELETE | /api/users/{username}             | Delete user account               |

## Pigeons

| Method | Endpoint                       | Description                            |
| ------ | ------------------------------ | -------------------------------------- |
| GET    | /api/pigeons                   | Get all pigeons for authenticated user |
| GET    | /api/pigeons/{id}              | Get pigeon by ID                       |
| POST   | /api/pigeons                   | Create a new pigeon                    |
| PATCH  | /api/pigeons/{id}              | Update pigeon details                  |
| DELETE | /api/pigeons/{id}              | Delete pigeon by ID                    |
| GET    | /api/pigeons/{id}/parents      | Get parents of a pigeon                |
| GET    | /api/pigeons/{id}/pedigree     | Get pigeon pedigree                    |
| GET    | /api/pigeons/{id}/pedigree/pdf | Download PDF pedigree                  |

## Demo

You can explore **DoveNet** either through the frontend interface or by interacting directly with the backend API.

### Frontend Demo
Experience the full user interface for managing pigeons, registering users, and generating PDF pedigrees:

- **Live Demo:** [DoveNet UI](https://youtu.be/ezMFsSjKeSA)  
- Features shown:
  - User registration and login (JWT authentication)
  - Adding, updating, and deleting pigeons
  - Viewing pigeon ancestry and stats
  - Generating PDF pedigree trees

### Backend Demo
Interact directly with the API endpoints to test functionality and see the backend in action:

- Example `curl` requests:

```bash
# Register a new user
curl -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{"username":"demo","email":"demo@example.com","password":"Demo123!"}'
```
# Authenticate and get JWT token
```
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"demo","password":"Demo123!"}'
```
# Get all pigeons (requires JWT token)
```
curl -X GET http://localhost:8080/api/pigeons \
-H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Author

**Krasen Kotorov** – Java Backend Developer
