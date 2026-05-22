# 🏦 Enterprise Banking System - Microservices Architecture

![Java](https://img.shields.io/badge/Java-17-orange.svg) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg) ![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-blue.svg) ![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg) ![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event--Driven-black.svg)

A production-ready, highly scalable Banking Management System built entirely from scratch using **Java Microservices Architecture**. 

This project demonstrates enterprise-grade system design, featuring 9 independent microservices, an event-driven transaction pipeline, central routing, and a strict database-per-service pattern. It is fully containerized and Kubernetes-ready.

## 🚀 Key Architectural Patterns
* **Database-per-Service:** Each core service maintains its own isolated PostgreSQL database, ensuring zero data coupling.
* **Event-Driven Architecture (EDA):** Utilizes Apache Kafka for asynchronous processing (e.g., Fraud Detection and Notifications) without blocking the main transaction thread.
* **Centralized Edge Routing:** All external traffic flows through a Spring Cloud API Gateway equipped with stateless JWT authentication and role-based access control (RBAC).
* **Service Discovery & Configuration:** Dynamic scaling and routing managed via Eureka Naming Server, with centralized environment variables served by a Spring Cloud Config Server.

---

## 🛠️ Technology Stack

**Backend System:**
* Java 17
* Spring Boot & Spring Cloud
* Spring Security & JWT (JSON Web Tokens)
* Spring Data JPA / Hibernate
* OpenFeign (Synchronous internal communication)
* Apache Kafka (Asynchronous event streaming)

**Databases:**
* PostgreSQL (Isolated instances per service)
* Flyway / Liquibase (Database migrations)

**DevOps & Deployment:**
* Docker & Docker Compose
* Kubernetes (Deployment manifests included)
* Maven

---

## 🏗️ Microservices Overview

The ecosystem consists of the following 9 independent nodes:

| Service | Role | Port |
| :--- | :--- | :--- |
| **API Gateway** | Central entry point, JWT validation, rate limiting, and routing. | `8080` |
| **Eureka Server** | Service registry and discovery for dynamic load balancing. | `8761` |
| **Config Server** | Centralized configuration management for all environments. | `8888` |
| **Auth Service** | User registration, login, BCrypt password hashing, and JWT generation. | `8081` |
| **Account Service** | Manages account creation, balances, and user profiles. | `8082` |
| **Transaction Service** | Core ledger handling transfers, deposits, and publishing Kafka events. | `8083` |
| **Fraud Detection** | Consumes transaction events to detect suspicious/rapid withdrawals. | `8084` |
| **Notification** | Consumes Kafka events to trigger email/SMS alerts for users. | `8085` |
| **Loan Service** | Manages loan applications, eligibility logic, and EMI calculations. | `8086` |

---

## ⚙️ Local Installation & Setup

### Prerequisites
* Java 17+
* Maven 3.8+
* Docker & Docker Compose

### 1. Clone the Repository
```bash
git clone [https://github.com/your-username/banking-system-microservices.git](https://github.com/your-username/banking-system-microservices.git)
cd banking-system-microservices
