# Trading Signal Tracking Application

## Overview

The Trading Signal Tracking Application is a Spring Boot REST API developed as part of the Zuvomo Backend Skill Evaluation. The application allows authenticated users to create, manage, and track cryptocurrency trading signals. It integrates with the Binance API to fetch live market prices, evaluates trading signal status, and calculates Return on Investment (ROI).

---

## Features

* JWT-based Authentication
* User Registration and Login
* Create, Update, Delete and View Trading Signals
* Signal Validation
* Live Binance API Integration
* Automatic Signal Status Evaluation
* ROI Calculation
* PostgreSQL Database
* Flyway Database Migration
* Swagger/OpenAPI Documentation
* Unit Testing with JUnit and Mockito

---

## Tech Stack

* Java 17
* Spring Boot 4.1.0
* Spring Security
* Spring Data JPA
* PostgreSQL
* Flyway
* JWT Authentication
* Spring WebFlux (WebClient)
* Swagger / OpenAPI
* Maven
* JUnit 5
* Mockito

---

## Project Structure

```
src
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 ├── validation
 ├── security
 ├── config
 ├── scheduler
 ├── exception
 └── resources
```

---

## Prerequisites

* Java 17
* Maven
* PostgreSQL

---

## Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE zuvomo;
```

Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/zuvomo
    username: YOUR_USERNAME
    password: YOUR_PASSWORD
```

---

## Running the Application

Clone the repository:

```bash
git clone https://github.com/Sumitr0843/trading-signal-tracking-application.git
```

Navigate to the project:

```bash
cd trading-signal-tracking-application
```

Run the application:

```bash
mvn spring-boot:run
```

---

## Swagger Documentation

After starting the application:

```
http://localhost:8080/swagger-ui.html
```

---

## Authentication

1. Register a new user.
2. Login to receive a JWT token.
3. Add the token in the Authorization header:

```
Bearer <JWT_TOKEN>
```

---

## Binance Integration

The application fetches live cryptocurrency prices using the Binance REST API through Spring WebClient. These prices are used for signal evaluation and ROI calculation.

---

## Running Tests

```
mvn test
```

---

## Author

**Sumit Kumar**

Developed as part of the Zuvomo Backend Skill Evaluation.
