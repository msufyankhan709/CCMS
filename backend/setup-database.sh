#!/bin/bash

echo "===================================="
echo "PostgreSQL Setup for Campus Complaints"
echo "===================================="
echo ""

# Option 1: Create database with a new user
echo "Creating database and user..."

sudo -u postgres psql << EOF
-- Create database if not exists
SELECT 'CREATE DATABASE campus_complaints' 
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'campus_complaints')\gexec

-- Create user with password
DROP USER IF EXISTS campusapp;
CREATE USER campusapp WITH PASSWORD 'campus123';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE campus_complaints TO campusapp;

-- Connect to the database and grant schema privileges
\c campus_complaints
GRANT ALL ON SCHEMA public TO campusapp;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO campusapp;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO campusapp;

\q
EOF

echo ""
echo "✅ Database setup complete!"
echo ""
echo "Database: campus_complaints"
echo "Username: campusapp"
echo "Password: campus123"
echo ""
echo "Now updating application.properties..."

# Update application.properties
cat > /home/sufyan/.gemini/antigravity/scratch/campus-complaint-system/backend/src/main/resources/application.properties << 'PROPS'
spring.application.name=ComplaintSystem

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/campus_complaints
spring.datasource.username=campusapp
spring.datasource.password=campus123
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=campusComplaintSystemSecretKeyForJWTTokenGenerationAndValidation2024
jwt.expiration=86400000

# Server Configuration
server.port=8080

# Logging
logging.level.com.campus.complaint=DEBUG
logging.level.org.springframework.security=DEBUG
PROPS

echo "✅ Updated application.properties with new credentials"
echo ""
echo "You can now run: ./mvnw spring-boot:run"
