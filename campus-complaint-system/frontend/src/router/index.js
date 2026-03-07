import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
    {
        path: '/',
        name: 'Landing',
        component: () => import('../views/LandingPage.vue'),
        meta: { requiresGuest: true }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/LoginView.vue'),
        meta: { requiresGuest: true }
    },
    {
        path: '/signup',
        name: 'Signup',
        component: () => import('../views/SignupView.vue'),
        meta: { requiresGuest: true }
    },
    {
        path: '/verify-email',
        name: 'VerifyEmail',
        component: () => import('../views/EmailVerificationView.vue'),
        meta: { requiresGuest: true }
    },
    {
        path: '/student',
        component: () => import('../layouts/StudentLayout.vue'),
        meta: { requiresAuth: true, role: 'STUDENT' },
        children: [
            {
                path: '',
                redirect: '/student/dashboard'
            },
            {
                path: 'dashboard',
                name: 'StudentDashboard',
                component: () => import('../views/StudentDashboard.vue')
            },
            {
                path: 'my-complaints',
                name: 'MyComplaints',
                component: () => import('../views/MyComplaints.vue')
            },
            {
                path: 'notifications',
                name: 'StudentNotifications',
                component: () => import('../views/StudentNotifications.vue')
            },
            {
                path: 'settings',
                name: 'StudentSettings',
                component: () => import('../views/StudentSettings.vue')
            }
        ]
    },
    {
        path: '/technician',
        component: () => import('../layouts/TechnicianLayout.vue'),
        meta: { requiresAuth: true, role: 'TECHNICIAN' },
        children: [
            {
                path: '',
                redirect: '/technician/dashboard'
            },
            {
                path: 'dashboard',
                name: 'TechnicianDashboard',
                component: () => import('../views/TechnicianDashboard.vue')
            },
            {
                path: 'assignments',
                name: 'TechnicianAssignments',
                component: () => import('../views/TechnicianAssignments.vue')
            },
            {
                path: 'notifications',
                name: 'TechnicianNotifications',
                component: () => import('../views/TechnicianNotifications.vue')
            },
            {
                path: 'settings',
                name: 'TechnicianSettings',
                component: () => import('../views/TechnicianSettings.vue')
            }
        ]
    },
    {
        path: '/admin',
        component: () => import('../views/AdminLayout.vue'),
        meta: { requiresAuth: true, roles: ['ADMIN'] },
        children: [
            {
                path: '',
                name: 'AdminDashboard',
                component: () => import('../views/AdminDashboard.vue')
            },
            {
                path: 'complaints',
                name: 'AllComplaints',
                component: () => import('../views/AllComplaints.vue')
            },
            {
                path: 'users',
                name: 'UserManagement',
                component: () => import('../views/AdminUserManagement.vue')
            },
            {
                path: 'reports',
                name: 'Reports',
                component: () => import('../views/AdminReports.vue')
            },
            {
                path: 'notifications',
                name: 'Notifications',
                component: () => import('../views/AdminNotifications.vue')
            },
            {
                path: 'settings',
                name: 'Settings',
                component: () => import('../views/AdminSettings.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()
    const isAuthenticated = authStore.isAuthenticated
    const userRole = authStore.userRole

    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login')
    } else if (to.meta.requiresGuest && isAuthenticated) {
        // Redirect authenticated users based on role
        if (userRole === 'ADMIN') next('/admin')
        else if (userRole === 'TECHNICIAN') next('/technician/dashboard')
        else next('/student/dashboard')
    } else if (to.meta.roles && !to.meta.roles.includes(userRole)) {
        // Redirect to appropriate dashboard if role doesn't match
        if (userRole === 'ADMIN') next('/admin')
        else if (userRole === 'TECHNICIAN') next('/technician/dashboard')
        else next('/student/dashboard')
    } else {
        next()
    }
})

export default router
