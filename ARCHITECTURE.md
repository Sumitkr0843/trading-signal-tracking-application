# Trading Signal Tracking Application - Architecture Overview

## Project Architecture

The application follows a layered architecture to ensure separation of concerns, maintainability, and scalability.

```text
Client
   │
   ▼
REST Controllers
   │
   ▼
Service Layer
   │
   ▼
Repository Layer (Spring Data JPA)
   │
   ▼
PostgreSQL Database
```

Additional components:

* **Spring Security + JWT** – Authentication and authorization.
* **Validation Layer** – Validates trading signal requests.
* **Flyway** – Database versioning and migrations.
* **WebClient** – Fetches live cryptocurrency prices from the Binance API.
* **Scheduler** – Periodically evaluates trading signal status.

---

## Project Structure

```text
src/main/java
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── scheduler
├── security
├── service
├── validation
└── TradingSignalTrackingApplication.java
```

---

## Business Logic Flow

### User Authentication

1. User registers with email and password.
2. Password is securely stored.
3. User logs in.
4. JWT token is generated.
5. Protected APIs require the JWT token.

---

### Trading Signal Flow

1. Authenticated user submits a trading signal.
2. Request validation checks:

    * BUY/SELL type
    * Entry price
    * Target price
    * Stop-loss price
    * Signal timing
3. Valid signal is stored in PostgreSQL.
4. Scheduler periodically fetches the latest market price from Binance.
5. Signal status is evaluated:

    * OPEN
    * TARGET_HIT
    * STOP_LOSS_HIT
    * EXPIRED
6. ROI is calculated based on trade outcome.
7. Updated information is returned through REST APIs.

---

## Binance API Integration

The application integrates with the Binance REST API using Spring WebClient.

Workflow:

```text
Scheduler
      │
      ▼
Binance REST API
      │
Current Market Price
      │
      ▼
Status Evaluation Service
      │
      ▼
Update Database
```

This enables real-time trading signal evaluation without requiring manual intervention.

---

## Technologies Used

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* PostgreSQL
* Flyway
* Spring WebClient
* Swagger/OpenAPI
* Maven
* JUnit 5
* Mockito
