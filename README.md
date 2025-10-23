# DoveNet 🕊️

**DoveNet** is a backend and future frontend application for pigeon breeders to manage their pigeons. Users can register, manage their pigeons, track competitions, update statuses, and generate PDF pedigree trees.

---

## Features

* **User Management**

  * Register new users
  * Secure password storage (hashed)
* **Pigeon Management**

  * Add, delete, and update pigeons
  * Track pigeon details (age, status, color, etc.)
  * View stats and history
* **Competition Tracking**

  * Record pigeon finishes in competitions
* **Pedigree Tree**

  * Generate PDF of pigeon ancestry
* **Filtering & Searching**

  * Filter pigeons by status, age, or other fields

---

## Tech Stack

* **Backend:** Java 17+, Spring Boot, Spring Data JPA
* **Database:** PostgreSQL
* **ORM:** Hibernate
* **Frontend (future):** React.js
* **PDF Generation:** TBD (iText or Apache PDFBox)

---

## Getting Started

### Prerequisites

* Java 17+
* Maven
* PostgreSQL database
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

### Run Backend

```bash
mvn spring-boot:run
```

The backend API will run on `http://localhost:8080/api`.

---

## API Endpoints

### Users

| Method | Endpoint     | Description           |
| ------ | ------------ | --------------------- |
| POST   | `/api/users` | Register new user     |
| GET    | `/api/users` | Get all users (admin) |

### Pigeons

| Method | Endpoint            | Description                    |
| ------ | ------------------- | ------------------------------ |
| GET    | `/api/pigeons`      | List all pigeons               |
| POST   | `/api/pigeons`      | Add a new pigeon               |
| DELETE | `/api/pigeons/{id}` | Delete a pigeon by ID          |
| PUT    | `/api/pigeons/{id}` | Update pigeon details (future) |

---

## Future Features

* User authentication & JWT
* Frontend React UI
* PDF pedigree generation
* Competition tracking module
* Advanced filtering and sorting

---

## Contributing

1. Fork the project
2. Create a new branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a pull request

---

## License

This project is open-source. Feel free to use, modify, and share.
