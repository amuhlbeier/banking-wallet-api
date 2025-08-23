# Banking Wallet API System
**(Fintech Portfolio Project)**

A full-stack Banking & Wallet API system simulating real-world fintech operations.  
Built to showcase backend engineering practices such as layered architecture, DTO separation, authentication, monitoring, and containerization.

## Tech Stack

**Backend**: Java 17, Spring Boot 3, Spring Security, Spring Data JPA, PostgreSQL  
**Frontend**: React, Tailwind CSS, Axios, React Router  
**Infrastructure**: Docker & Docker Compose  
**Docs**: Swagger / OpenAPI  
**Monitoring**: Spring Boot Actuator (/actuator/health, /actuator/metrics)  

## Key Features

### Core Banking Functionality
- Create, retrieve, and delete bank accounts (seed users provided)
- Deposit / Withdraw funds (with $50 minimum balance rule and overdraft protection)
- Transfer funds between accounts with ledger tracking
- Freeze / Unfreeze accounts (security safeguard)
- Immutable transaction history with full CREDIT/DEBIT audit trail

### Transactions & Filtering
- Paginated transaction history
- Filter transactions by:
    - Date range
    - Amount range
    - Transaction ID
    - Account ID

### Export & Reporting
- CSV export of all transactions
- PDF export of all transactions or monthly statements

### Authentication & Security
- JWT-based login with admin credentials (internal dashboard)
- Backend secured with Spring Security
- CORS configured for frontend integration

### API & Documentation
- Swagger UI auto-generates OpenAPI docs
- DTOs & schema annotations for clean contracts
- Centralized exception handling with `@ControllerAdvice`

### Monitoring
- Spring Boot Actuator endpoints (`/actuator/health`, `/actuator/metrics`)
- Docker health checks use `/actuator/health` for container readiness

### Frontend (React Dashboard)
- Account actions: deposit, withdraw, transfer, freeze/unfreeze, delete
- Paginated transaction viewer
- CSV / PDF export
- Responsive dashboard styled with Tailwind CSS  

## Architecture Overview

- Monolithic backend structured into services: Auth, Account, Transaction
- DTO layer separates API contract from domain entities
- PostgreSQL persistence with Spring Data JPA
- Docker Compose orchestrates backend, frontend, and database  

## How to Run Locally:

```
# Clone the repo
git clone https://github.com/amuhlbeier/banking-wallet-api.git
cd banking-wallet-api

# Build and run all containers
docker compose up --build
```

### Frontend
- [http://localhost](http://localhost)
  (username = `admin` / password = `adminpassword`)

### Swagger Docs
- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Health Check
- [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## Notable Design Decisions
- **Java & Spring Boot** → production-grade backend stack, common in fintech
- **DTO Layer** → prevents leaking internal DB models into API
- **Swagger** → self-documenting REST APIs for easy frontend integration
- **Docker Compose** → simple orchestration of multi-service app (backend + DB + frontend)
- **React + Tailwind** → clean, responsive frontend dashboard


## Challenges & Reflections
- **DTO Refactoring**: Learned to decouple entities from API responses for clarity and safety
- **JWT Auth**: Deepened knowledge of Spring Security filters and stateless authentication
- **Docker Networking**: Configured containers to communicate seamlessly (backend ↔ Postgres ↔ frontend)
- **React**: First hands-on React app; learned routing, async calls, and reusable components


## Possible Next Steps
- Add user registration & multi-role access (admin vs. user)
- Introduce caching with Redis
- Enhance monitoring with custom Actuator metrics dashboards
- Add OAuth2 for richer frontend authentication
- Deploy to a managed platform (Render, Railway, Fly.io) with HTTPS  