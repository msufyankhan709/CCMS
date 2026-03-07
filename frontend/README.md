# Campus Complaint System - Frontend

## Overview
Vue.js 3 frontend application with Vuetify 3.9 UI framework for the Campus Complaint Management System.

## Technologies
- Vue 3 (Composition API)
- Vuetify 3.9 (Material Design)
- Pinia 3.0 (State Management)
- Vue Router 4 (Routing)
- Axios 1.7 (HTTP Client)
- Vite 6 (Build Tool)

## Prerequisites
- Node.js 18+ and npm
- Backend server running on `http://localhost:8080`

## Installation
```bash
npm install
```

## Running the Application
```bash
npm run dev
```

The app will start on **http://localhost:5174** (or next available port)

## Build for Production
```bash
npm run build
npm run preview
```

## Features

### Authentication
- **Login**: JWT-based authentication
- **Signup**: Self-registration for Students and Faculty

### Dashboards

#### Student/Faculty Dashboard
- View all recent complaints
- View my submitted complaints
- Submit new complaints with priority levels
- Real-time status updates

#### Admin Dashboard
- View all complaints system-wide
- Assign complaints to technicians
- Update complaint status
- Manage system users

#### Technician Dashboard
- View assigned complaints
- Update complaint status (IN_PROGRESS → RESOLVED)
- View complaint details

## Project Structure
```
src/
├── components/       # Reusable Vue components
├── views/           # Page components
│   ├── LoginView.vue
│   ├── SignupView.vue
│   ├── DashboardLayout.vue
│   ├── StudentDashboard.vue
│   ├── AdminDashboard.vue
│   └── TechnicianDashboard.vue
├── stores/          # Pinia stores
│   ├── auth.js      # Authentication state
│   └── complaint.js # Complaint management
├── services/        # API services
│   └── api.js       # Axios instance with interceptors
├── router/          # Vue Router configuration
│   └── index.js
├── plugins/         # Vue plugins
│   └── vuetify.js   # Vuetify configuration
├── App.vue          # Root component
└── main.js          # Application entry point
```

## API Integration
The frontend automatically connects to the backend API at `http://localhost:8080/api`.

JWT tokens are stored in localStorage and automatically included in all authenticated requests.

## Routing
- `/login` - Login page
- `/signup` - Registration page
- `/home` - Student/Faculty dashboard (protected)
- `/admin` - Admin dashboard (protected, admin only)
- `/technician` - Technician dashboard (protected, technician only)

## Default Accounts
Use these credentials to test different user roles:
- **Admin**: username=`admin`, password=`admin123`
- **Technician**: username=`tech1`, password=`tech123`
- **Student/Faculty**: Create via signup page
