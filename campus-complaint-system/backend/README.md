# Campus Complaint System - Backend

## Overview
Spring Boot 3.5.0 REST API for the Campus Complaint Management System.

## Technologies
- Java 21
- Spring Boot 3.5.0
- Spring Security (JWT Authentication)
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## Prerequisites
- Java 21+
- PostgreSQL 13+
- Maven (or use included Maven wrapper)

## Database Setup
1. Install and start PostgreSQL
2. Create database:
```sql
CREATE DATABASE campus_complaints;
```
3. Update credentials in `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/campus_complaints
spring.datasource.username=postgres
spring.datasource.password=password
```

## Running the Application
```bash
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`

## Default Users
- **Admin**: username=`admin`, password=`admin123`, email=`admin@campus.edu`
- **Technician**: username=`tech1`, password=`tech123`, email=`tech1@campus.edu`

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user (STUDENT/FACULTY only)
- `POST /api/auth/login` - Login and get JWT token

### Complaints
- `POST /api/complaints` - Create complaint (STUDENT/FACULTY)
- `GET /api/complaints/recent` - Get all complaints (Public)
- `GET /api/complaints/my` - Get my complaints (STUDENT/FACULTY)
- `GET /api/complaints/technician` - Get assigned complaints (TECHNICIAN)
- `GET /api/complaints/{id}` - Get complaint by ID
- `PUT /api/complaints/{id}/assign` - Assign technician (ADMIN)
- `PUT /api/complaints/{id}/status` - Update status (ADMIN/TECHNICIAN)

### Comments
- `POST /api/comments` - Add comment
- `GET /api/comments/complaint/{complaintId}` - Get comments for complaint

### Users (Admin Only)
- `GET /api/users` - Get all users
- `GET /api/users/role/{role}` - Get users by role
- `PUT /api/users/{id}/role` - Update user role

## Roles & Permissions
- **STUDENT/FACULTY**: Submit and track complaints
- **TECHNICIAN**: View assigned complaints, update status, add comments
- **ADMIN**: Full access, assign complaints, manage users

## Testing
Build and package:
```bash
./mvnw clean package
```
