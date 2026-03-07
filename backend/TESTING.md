# Backend Testing Guide

## Prerequisites

### 1. Create Database
```bash
# Connect to PostgreSQL
sudo -u postgres psql

# Create database and exit
CREATE DATABASE campus_complaints;
\q
```

### 2. Start the Backend
```bash
cd /home/sufyan/.gemini/antigravity/scratch/campus-complaint-system/backend
./mvnw spring-boot:run
```

The server will start on **http://localhost:8080**

---

## API Testing with cURL

### Authentication Tests

#### 1. Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "fullName": "System Administrator",
  "role": "ADMIN",
  "userId": 1
}
```

**Save the token** - you'll need it for authenticated requests!

#### 2. Signup as Student
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student1",
    "password": "student123",
    "fullName": "John Student",
    "email": "john@campus.edu",
    "role": "STUDENT"
  }'
```

### Complaint Tests

#### 3. Create a Complaint (as Student)
**Replace `YOUR_TOKEN` with the token from login/signup**
```bash
curl -X POST http://localhost:8080/api/complaints \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "AC not working in Lab 101",
    "description": "The air conditioning unit in Computer Lab 101 has been broken for 2 days",
    "priority": "HIGH"
  }'
```

#### 4. Get All Recent Complaints (Public)
```bash
curl -X GET http://localhost:8080/api/complaints/recent
```

#### 5. Get My Complaints (as Student)
```bash
curl -X GET http://localhost:8080/api/complaints/my \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Admin Tests

#### 6. Get All Users (as Admin)
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### 7. Get Technicians (as Admin)
```bash
curl -X GET http://localhost:8080/api/users/role/TECHNICIAN \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### 8. Assign Complaint to Technician (as Admin)
**Replace complaint ID and technician ID**
```bash
curl -X PUT http://localhost:8080/api/complaints/1/assign \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -d '{
    "technicianId": 2
  }'
```

### Technician Tests

#### 9. Update Complaint Status (as Technician or Admin)
```bash
curl -X PUT http://localhost:8080/api/complaints/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TECHNICIAN_TOKEN" \
  -d '{
    "status": "RESOLVED"
  }'
```

### Comment Tests

#### 10. Add Comment to Complaint
```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "complaintId": 1,
    "content": "I have checked the AC unit. Will fix it tomorrow."
  }'
```

#### 11. Get Comments for Complaint
```bash
curl -X GET http://localhost:8080/api/comments/complaint/1
```

---

## Alternative: Testing with Postman

1. Download and install [Postman](https://www.postman.com/downloads/)
2. Import the endpoints above
3. Use the "Authorization" tab to set Bearer Token
4. Save tokens in Postman environment variables for easy reuse

---

## Alternative: Testing with HTTPie

Install HTTPie (prettier than curl):
```bash
sudo apt install httpie
```

Example usage:
```bash
# Login
http POST localhost:8080/api/auth/login username=admin password=admin123

# Create complaint (with token)
http POST localhost:8080/api/complaints \
  Authorization:"Bearer YOUR_TOKEN" \
  title="Network Issue" \
  description="No internet in Building A" \
  priority=HIGH
```

---

## Quick Test Script

Save this as `test-api.sh` and run it:

```bash
#!/bin/bash

echo "1. Testing Login..."
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.token')

echo "Token: $TOKEN"
echo ""

echo "2. Getting all complaints..."
curl -s http://localhost:8080/api/complaints/recent | jq
echo ""

echo "3. Getting all users (as admin)..."
curl -s -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" | jq
```

Make it executable:
```bash
chmod +x test-api.sh
./test-api.sh
```
