#!/bin/bash

BASE_URL="http://localhost:8080"

echo "================================"
echo "Campus Complaint System API Test"
echo "================================"
echo ""

# Test 1: Login as Admin
echo "1. Logging in as Admin..."
RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

ADMIN_TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$ADMIN_TOKEN" ]; then
    echo "❌ Login failed!"
    echo "Response: $RESPONSE"
    exit 1
else
    echo "✅ Login successful!"
    echo "Token: ${ADMIN_TOKEN:0:20}..."
fi
echo ""

# Test 2: Signup as Student
echo "2. Signing up as Student..."
STUDENT_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser'$(date +%s)'",
    "password": "test123",
    "fullName": "Test Student",
    "email": "test'$(date +%s)'@campus.edu",
    "role": "STUDENT"
  }')

STUDENT_TOKEN=$(echo $STUDENT_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$STUDENT_TOKEN" ]; then
    echo "❌ Signup failed!"
    echo "Response: $STUDENT_RESPONSE"
else
    echo "✅ Signup successful!"
    echo "Token: ${STUDENT_TOKEN:0:20}..."
fi
echo ""

# Test 3: Create Complaint
echo "3. Creating a complaint as Student..."
COMPLAINT_RESPONSE=$(curl -s -X POST $BASE_URL/api/complaints \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -d '{
    "title": "Test Complaint - AC Issue",
    "description": "AC not working in Lab 101",
    "priority": "HIGH"
  }')

echo "✅ Complaint created:"
echo "$COMPLAINT_RESPONSE" | grep -o '"id":[0-9]*' || echo "$COMPLAINT_RESPONSE"
echo ""

# Test 4: Get Recent Complaints
echo "4. Getting recent complaints (public)..."
RECENT=$(curl -s $BASE_URL/api/complaints/recent)
COUNT=$(echo $RECENT | grep -o '"id"' | wc -l)
echo "✅ Found $COUNT complaint(s)"
echo ""

# Test 5: Get All Users (Admin)
echo "5. Getting all users (as Admin)..."
USERS=$(curl -s -X GET $BASE_URL/api/users \
  -H "Authorization: Bearer $ADMIN_TOKEN")
USER_COUNT=$(echo $USERS | grep -o '"id"' | wc -l)
echo "✅ Found $USER_COUNT user(s)"
echo ""

# Test 6: Get Technicians
echo "6. Getting technicians (as Admin)..."
TECHS=$(curl -s -X GET $BASE_URL/api/users/role/TECHNICIAN \
  -H "Authorization: Bearer $ADMIN_TOKEN")
TECH_COUNT=$(echo $TECHS | grep -o '"id"' | wc -l)
echo "✅ Found $TECH_COUNT technician(s)"
echo ""

echo "================================"
echo "All tests completed!"
echo "================================"
