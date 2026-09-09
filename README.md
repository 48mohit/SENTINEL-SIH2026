# 🛂 SENTINEL — AI-Based Identity & Document Screening System

**Smart India Hackathon 2026 | PS: SIH26188**
**Organization: Ministry of Home Affairs | SSB, Police II Division**
**Theme: Blockchain & Cybersecurity**

---

## 🎯 Problem Statement

Border checkpoints process thousands of identity documents daily. Manual verification is slow, error-prone, and unable to detect sophisticated forgeries. SENTINEL automates this process using AI-powered risk intelligence.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 21, Spring Boot 4.1, Spring Security |
| **Database** | MySQL 8.0, Spring Data JPA, Hibernate |
| **Security** | JWT Authentication, BCrypt, RBAC (4 roles) |
| **AI/Risk** | Custom Risk Scoring Engine, SHA-256 Integrity |
| **Frontend** | React.js (in progress) |
| **DevOps** | Docker, GitHub Actions (planned) |

---

## ✅ Features Implemented

### Module 1 — Secure Authentication
- JWT-based stateless authentication
- 4 roles: ADMIN, OFFICER, SUPERVISOR, AUDITOR
- BCrypt password hashing
- Role-based API access control

### Module 2 — Document Upload
- Secure multi-part file upload
- File type validation (JPG, PNG, PDF)
- SHA-256 integrity hash generation
- Duplicate document detection
- Secure server-side storage

### Module 3 — Risk Scoring Engine
- Explainable 0-100 risk score
- 4 risk signals: Document Type, File Metadata, Integrity, Validation
- Risk levels: LOW (0-29) | MEDIUM (30-59) | HIGH (60-79) | CRITICAL (80-100)
- Full explanation of every risk signal
- Dashboard statistics API

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/login | Officer login |
| POST | /api/documents/upload | Upload document |
| GET | /api/documents | List documents |
| GET | /api/documents/{id} | Get document |
| POST | /api/risk/assess/{id} | Run risk assessment |
| GET | /api/risk/document/{id} | Get risk result |
| GET | /api/risk/stats | Dashboard statistics |
| GET | /api/risk/high-risk | High risk documents |

---

## 🚀 How to Run

### Prerequisites
- Java 21+
- MySQL 8.0
- Maven 3.9+

### Setup
```bash
# Create database
mysql -u root -p
CREATE DATABASE sentinel_db;
exit

# Run application
cd backend/sentinel
mvn spring-boot:run
```

### Default Users
| Username | Password | Role |
|---|---|---|
| admin | Admin@123 | ADMIN |
| officer1 | Officer@123 | OFFICER |
| supervisor1 | Super@123 | SUPERVISOR |
| auditor1 | Audit@123 | AUDITOR |

---

## 🗄️ Database Schema
