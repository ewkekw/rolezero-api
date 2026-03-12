# Role-Zero Backend Core

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-PostGIS-blue.svg)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet.svg)
![Security](https://img.shields.io/badge/Security-Stateless_JWT-red.svg)

> **"True connections happen offline."**

Role-Zero is a B2C platform focused on hyper-locality and authentic spontaneous connections. The application enables users to create or request access to ephemeral private events (expiring within 24 hours) via a real-time geographical radar driven by PostGIS spatial capabilities.

This repository holds the **Core API (Backend)**, modeled entirely upon **Domain-Driven Design (DDD)** and **Hexagonal Architecture (Ports and Adapters)** to ensure extreme maintainability, testability, and technological agnosticism regarding business rules.

---

## Client Application Integration Guide

To build the final mobile frontend (React Native, Flutter, Swift, or Kotlin), the application must orchestrate four main pillars of technology. The Role-Zero Backend acts as the centralized source of truth, spatial calculator, and cryptographic validator.

### 1. Maps Integration (External UI API)
The mobile client must implement an interactive map interface (e.g., Google Maps SDK or Mapbox SDK).
- **Responsibility**: Render UI map layers, handle user gestures (pan/zoom), and plot event markers based on the exact spatial coordinates (latitude/longitude) returned by the Role-Zero API.

### 2. Location Services (External OS SDK)
Precise geolocation is critical for the application's core functionality, especially structural rules such as the Check-In proximity constraints.
- **Responsibility**: Utilize `CoreLocation` (iOS) or `FusedLocationProvider` (Android) to retrieve the raw latitude, longitude and accuracy of the device in real-time. This raw data is transmitted as the payload to backend endpoints (`GET /api/v1/events/nearby` and `POST /api/v1/events/{id}/check-in`).

### 3. Identity Providers (External OAuth)
Authentication is strictly delegated to native SSO providers, adhering to a Zero-Knowledge paradigm where the platform does not store or process raw passwords.
- **Responsibility**: The mobile client implements native SDKs for "Sign in with Google" or "Sign in with Apple". It retrieves the identity token directly from the provider and forwards it to the Role-Zero API via `POST /api/v1/auth/sso`. The backend then validates the signature cryptographically, issues an internal stateless session (JWT), and returns it to the client layer.

### 4. Role-Zero Core API (This Repository)
The definitive RESTful interface orchestrating the backend logic.
- **Contract Reference**: The exact and synchronized specification is located in `docs/openapi.yaml`. It details HTTP methods, payloads, expected status codes, and security requirements. Use this contract with Swagger UI, Postman, or strongly-typed client generation tools (e.g., OpenAPI Generator for Dart/Typescript).

---

## Architecture Overview

The backend was engineered to achieve total decoupling between the core business logic and external infrastructure (frameworks, databases, web servers).

### The Hexagonal Layers

- **`core/domain`**: The heart of the system. Contains rich entities (`Evento`, `Usuario`, `CoordenadaGeografica`), value objects, and business rules. Completely isolated from framework annotations.
- **`core/application`**: Defines the use cases (`CriarEventoUseCase`, `ProcessarSolicitacaoUseCase`) and interface ports (Inbound/Outbound). Acts as the orchestrator of business flows and transactions.
- **`adapter/in`**: Inbound adapters, primarily REST Web Controllers. They receive HTTP requests, validate basic payloads, mapping them into domain commands, and delegate to the application core.
- **`adapter/out`**: Outbound adapters that implement the outgoing ports. Includes JPA/Hibernate implementations for database communication, WebSockets for realtime interactions, and integrations for third-party systems.
- **`config`**: Infrastructure wiring. Configures Spring Security workflows, JWT stateless mechanisms, and explicit Bean definitions for dependency injection to maintain core purity.

---

## Development Setup

### System Requirements
- JDK 21+
- Apache Maven 3.8+
- Docker and Docker Compose (For local ecosystem simulation)

### Environment Variables
For local execution, configure the following variables in your application environment or within IDE run configurations:
- `DB_HOST`: Target PostgreSQL instance host.
- `DB_PORT`: Database port (Default: 5432).
- `DB_NAME`: Database schema name.
- `DB_USER`: Authenticated user role.
- `DB_PASS`: User password.
- `JWT_SECRET`: A long, cryptographically secure string (Base64 recommended) used for signing stateless JSON Web Tokens.
- `SPRING_PROFILES_ACTIVE`: Set to `dev` for local development profiles or `prod` for cloud deployments.

### Running the Application

1. **Start Local Infrastructure**
   Deploy the PostGIS-enabled database container (for local offline development):
   ```bash
   docker-compose up -d
   ```

2. **Compile and Execute**
   Leverage the Maven wrapper to build the application, execute tests, and start the embedded Tomcat server:
   ```bash
   mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
   ```

The Flyway migration component will automatically bootstrap the database schemas, spatial indexes, and required relations defined in `src/main/resources/db/migration`.

---

## Cloud Deployment (CI/CD)

The repository is equipped with a multi-stage `Dockerfile` and a Blueprint specification (`render.yaml`), optimized for lightweight PaaS continuous deployments.

1. **Dockerized Runtime**: Compiles via Eclipse Temurin 21 JDK Alpine and executes on an optimized JRE 21 layer to respect strict memory constraints.
2. **Infrastructure-as-Code (IaC)**: The `render.yaml` file natively declares environment rules, regions, scaling factors and instance sizing, allowing automated zero-downtime deployments triggered by push events to the `main` branch.

---

## Technical Documentation

In-depth technical decisions and architecture definitions are maintained within the `docs/` directory:
- **Architecture Decision Records (ADRs)**: Detailed logs justifying technical choices (Hexagonal Architecture constraints, PostGIS geographic queries, Stateless Security).
- **OpenAPI 3.0 Contract (`openapi.yaml`)**: The precise REST layout intended for client and gateway consumption.
