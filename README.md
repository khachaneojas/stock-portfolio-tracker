# 📈 Real-Time Stock Portfolio Tracker

A production-inspired backend application built using **Java 21** and **Spring Boot 4**, allowing users to manage investment portfolios, monitor live stock prices, receive price alerts, and analyse portfolio performance.

The project demonstrates enterprise backend development practices including JWT authentication, RESTful APIs, Redis caching, WebSockets, Docker, PostgreSQL, and scheduled background jobs.

---

## Features

### Authentication & Security

- JWT Authentication
- Role Based Access Control (Admin/User)
- BCrypt Password Encryption
- Stateless Security
- Spring Security
- Global Exception Handling

---

### Stock Management

- Create Stocks
- Update Stocks
- Activate / Deactivate Stocks
- Search Stocks
- Stock Price History

---

### Portfolio Management

- Add Holdings
- Update Holdings
- Remove Holdings
- Portfolio Valuation
- Profit / Loss Calculation
- Current Portfolio Value

---

### Live Market Prices

- Real-time Market Data Integration (Finnhub API)
- Automatic Price Refresh
- Scheduled Background Updates
- Historical Price Storage
- Redis Price Cache

---

### Alerts

- Price Above Alert
- Price Below Alert
- Portfolio Value Above Alert
- Portfolio Value Below Alert
- WebSocket Alert Notifications

---

### Analytics

- Portfolio Summary
- Profit & Loss Analysis
- Best Performing Stock
- Worst Performing Stock
- Stock Allocation
- Sector Allocation
- Historical Portfolio Snapshots

---

### Real-Time Communication

- Spring WebSockets
- Live Stock Price Updates
- Live Alert Notifications

---

### Infrastructure

- PostgreSQL
- Redis
- Docker
- Docker Compose
- Scheduled Jobs

---

## Tech Stack

### Backend

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring WebSocket
- Spring Scheduling

### Database

- PostgreSQL

### Cache

- Redis

### API Documentation

- Swagger / OpenAPI

### Authentication

- JWT

### Build Tool

- Maven

### Containerisation

- Docker
- Docker Compose

### Testing

- JUnit 5
- Mockito
- Testcontainers

---

## Architecture

```
                 Client

                    │

      REST API / WebSocket

                    │

          Spring Boot Backend

 ┌─────────────────────────────────┐
 │ Authentication                  │
 │ Portfolio Module                │
 │ Stock Module                    │
 │ Analytics Module                │
 │ Alert Module                    │
 │ Price Module                    │
 └─────────────────────────────────┘

          │              │

      PostgreSQL      Redis

          │              │

      Historical      Latest Prices

                    │

             Finnhub Market API
```

---

## Modules

```
Authentication

User Management

Stock Management

Price Management

Portfolio Management

Analytics

Alerts

WebSocket

Security

Common

Configuration
```

---

## Project Structure

```
src
├── main
│   ├── java
│   │   └── com.stocktracker.stock_portfolio_tracker
│   │
│   └── resources
│
└── test
```

---

## Database Design

### Users

- User Information
- Roles
- Status

### Stocks

- Symbol
- Company Name
- Exchange
- Sector

### Stock Prices

- Historical Prices
- Timestamp

### Portfolio Holdings

- Quantity
- Average Buy Price

### Alerts

- Price Alerts
- Portfolio Alerts

### Portfolio Snapshots

- Historical Portfolio Value

---

## REST APIs

### Authentication

```
POST   /api/v1/auth/register

POST   /api/v1/auth/login
```

---

### Stocks

```
POST   /api/v1/stocks

GET    /api/v1/stocks

GET    /api/v1/stocks/{id}

PUT    /api/v1/stocks/{id}

DELETE /api/v1/stocks/{id}
```

---

### Portfolio

```
POST   /api/v1/portfolio

PUT    /api/v1/portfolio/{id}

DELETE /api/v1/portfolio/{id}

GET    /api/v1/portfolio

GET    /api/v1/portfolio/valuation

GET    /api/v1/portfolio/snapshots
```

---

### Alerts

```
POST   /api/v1/alerts

GET    /api/v1/alerts

PATCH  /api/v1/alerts/{id}/deactivate

DELETE /api/v1/alerts/{id}
```

---

### Analytics

```
GET /api/v1/analytics/summary

GET /api/v1/analytics/allocation/stocks

GET /api/v1/analytics/allocation/sectors
```

---

## Real-Time Features

### WebSocket Endpoint

```
/ws
```

### Topics

```
/topic/prices

/topic/prices/{symbol}

/topic/alerts/{userId}
```

---

## Security

- JWT Authentication
- BCrypt Password Encoding
- Stateless Sessions
- Role-Based Authorization
- Method Security

---

## Running Locally

### Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop

### Configure Environment Variables

Create a `.env` file in the project root and add:

```env
JWT_SECRET=your-base64-jwt-secret
FINNHUB_API_KEY=your-finnhub-api-key
```

### Start Infrastructure

Start PostgreSQL and Redis using Docker Compose:

```bash
docker compose up -d
```

### Run the Application

```bash
mvn spring-boot:run
```

Alternatively, use the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

---

## Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

## Testing

Run all tests

```bash
mvn test
```

---

## Future Improvements

- Refresh Tokens
- Email Verification
- Multi-Currency Portfolio Support
- Dividend Tracking
- Transaction History
- OAuth2 Login
- Kubernetes Deployment
- CI/CD Pipeline
- Prometheus & Grafana Monitoring
- Flyway Database Migration
- RabbitMQ Event Processing

---

## Screenshots

- Swagger UI

- ER Diagram

- Architecture Diagram

- Docker Containers

- Postman Collection

- WebSocket Demo

---

## Author

**Ojas Khachane**

MSc Advanced Computer Science

Backend Java Developer

Java • Spring Boot • PostgreSQL • Redis • Docker • WebSockets • JWT