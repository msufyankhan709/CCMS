# Campus Complaint Management System

A full-stack web application for managing campus complaints with role-based access control.

## 🎯 Overview
This system streamlines the complaint lifecycle from submission by students/faculty to resolution by technicians, with admins overseeing the entire process.

## 🛠️ Tech Stack

### Backend
- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL 13+**
- **Maven**

### Frontend
- **Vue 3** (Composition API)
- **Vuetify 3.9** (Material Design)
- **Pinia 3.0** (State Management)
- **Vue Router 4**
- **Axios 1.7**
- **Vite 6**

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Node.js 18+
- PostgreSQL 13+

### 1. Setup Database
```bash
sudo -u postgres psql -c "CREATE DATABASE campus_complaints;"
```

### 2. Start Backend
```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on: **http://localhost:8080**

### 3. Start Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on: **http://localhost:5174**

### 4. Access Application
Navigate to **http://localhost:5174** in your browser.

## 👥 Default Accounts
- **Admin**: username=`admin`, password=`admin123`
- **Technician**: username=`tech1`, password=`tech123`
- **Student/Faculty**: Create via signup page

## 📁 Project Structure
```
campus-complaint-system/
├── backend/                 # Spring Boot Application
│   ├── src/
│   │   ├── main/java/com/campus/complaint/
│   │   │   ├── model/      # JPA Entities
│   │   │   ├── repository/ # Data Access Layer
│   │   │   ├── service/    # Business Logic
│   │   │   ├── controller/ # REST API
│   │   │   ├── security/   # JWT & Security Config
│   │   │   ├── dto/        # Data Transfer Objects
│   │   │   └── config/     # Configuration
│   │   └── resources/
│   │       └── application.properties
│   ├── pom.xml
│   └── README.md
│
└── frontend/               # Vue.js Application
    ├── src/
    │   ├── views/         # Page Components
    │   ├── components/    # Reusable Components
    │   ├── stores/        # Pinia State Management
    │   ├── services/      # API Integration
    │   ├── router/        # Vue Router
    │   ├── plugins/       # Vue Plugins
    │   ├── App.vue
    │   └── main.js
    ├── package.json
    └── README.md
```

## ✨ Features

### 🔐 Authentication & Authorization
- JWT-based stateless authentication
- BCrypt password hashing
- Role-based access control (RBAC)
- Automatic token refresh

### 👤 User Roles

#### Student/Faculty
- Submit complaints with priority levels
- Track complaint status
- View all recent complaints

#### Technician
- View assigned complaints
- Update complaint status
- Mark complaints as resolved

#### Admin
- View all complaints
- Assign complaints to technicians
- Manage user roles
- Close resolved complaints

### 📊 Complaint Management
- **Status Workflow**: OPEN → IN_PROGRESS → RESOLVED → CLOSED
- **Priority Levels**: LOW, MEDIUM, HIGH, CRITICAL
- **Real-time Updates**: Instant UI feedback
- **Color-coded Status**: Visual indicators

### 🎨 UI/UX
- Material Design components
- Responsive layout
- Beautiful gradient backgrounds
- Interactive data tables
- Loading states
- Error handling

## 📡 API Endpoints

### Authentication
- `POST /api/auth/signup` - Register (Student/Faculty)
- `POST /api/auth/login` - Login

### Complaints
- `POST /api/complaints` - Create complaint
- `GET /api/complaints/recent` - Get all complaints
- `GET /api/complaints/my` - Get my complaints
- `GET /api/complaints/technician` - Get assigned (Technician)
- `PUT /api/complaints/{id}/assign` - Assign technician (Admin)
- `PUT /api/complaints/{id}/status` - Update status

### Users
- `GET /api/users` - Get all users (Admin)
- `GET /api/users/role/{role}` - Get users by role (Admin)
- `PUT /api/users/{id}/role` - Update user role (Admin)

## 🧪 Testing

### Backend Testing
```bash
cd backend
./test-api.sh
```

See [backend/TESTING.md](backend/TESTING.md) for detailed API testing guide.

### Frontend Testing
1. Ensure backend is running
2. Navigate to http://localhost:5174
3. Test login with default accounts
4. Test complete workflow

## 📚 Documentation
- [Backend README](backend/README.md)
- [Frontend README](frontend/README.md)
- [API Testing Guide](backend/TESTING.md)
- [Implementation Walkthrough](brain/walkthrough.md)

## 🎯 Workflow Example
1. **Student** submits a complaint (Status: OPEN)
2. **Admin** reviews and assigns to technician (Status: IN_PROGRESS)
3. **Technician** works on issue and marks resolved (Status: RESOLVED)
4. **Admin** verifies and closes complaint (Status: CLOSED)

## 🔒 Security Features
- JWT token-based authentication
- Password encryption with BCrypt
- CORS configuration
- Protected routes
- Role-based endpoint access
- Automatic token validation

## 📄 License
This project was created for educational purposes as a Final Year Project (FYP).

## 👨‍💻 Development
Built with ❤️ using Spring Boot and Vue.js
