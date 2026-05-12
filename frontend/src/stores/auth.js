import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../services/api'

export const useAuthStore = defineStore('auth', () => {
    const user = ref(null)
    const token = ref(null)
    const loading = ref(false)
    const error = ref(null)

    // Load from localStorage on init
    const storedUser = localStorage.getItem('user')
    const storedToken = localStorage.getItem('token')
    if (storedUser && storedUser !== 'null') user.value = JSON.parse(storedUser)
    if (storedToken && storedToken !== 'null') token.value = storedToken

    const isAuthenticated = computed(() => !!token.value)
    const userRole = computed(() => user.value?.role)

    async function login(credentials) {
        try {
            loading.value = true
            error.value = null
            const response = await api.post('/auth/login', credentials)

            user.value = response.data
            token.value = response.data.token

            localStorage.setItem('user', JSON.stringify(response.data))
            localStorage.setItem('token', response.data.token)

            return response.data
        } catch (err) {
            error.value = err.response?.data || 'Login failed'
            throw err
        } finally {
            loading.value = false
        }
    }

    async function signup(data) {
        try {
            loading.value = true
            error.value = null
            const response = await api.post('/auth/signup', data)

            // Do not log user in if verification token requires them to verify first
            if (response.data.token) {
                user.value = response.data
                token.value = response.data.token
                localStorage.setItem('user', JSON.stringify(response.data))
                localStorage.setItem('token', response.data.token)
            }

            return response.data
        } catch (err) {
            error.value = err.response?.data || 'Signup failed'
            throw err
        } finally {
            loading.value = false
        }
    }

    function logout() {
        user.value = null
        token.value = null
        localStorage.removeItem('user')
        localStorage.removeItem('token')
    }

    async function changePassword(currentPassword, newPassword) {
        try {
            loading.value = true
            error.value = null
            const response = await api.put('/auth/change-password', {
                currentPassword,
                newPassword
            })
            return response.data
        } catch (err) {
            error.value = err.response?.data?.error || 'Failed to change password'
            throw err
        } finally {
            loading.value = false
        }
    }

    async function forgotPassword(email) {
        try {
            loading.value = true
            error.value = null
            const response = await api.post('/auth/forgot-password', { email })
            return response.data
        } catch (err) {
            error.value = err.response?.data?.error || err.response?.data || 'Failed to request password reset'
            throw err
        } finally {
            loading.value = false
        }
    }

    async function resetPassword(token, newPassword) {
        try {
            loading.value = true
            error.value = null
            const response = await api.post('/auth/reset-password', { token, newPassword })
            return response.data
        } catch (err) {
            error.value = err.response?.data?.error || err.response?.data || 'Failed to reset password'
            throw err
        } finally {
            loading.value = false
        }
    }

    return {
        user,
        token,
        loading,
        error,
        isAuthenticated,
        userRole,
        login,
        signup,
        logout,
        changePassword,
        forgotPassword,
        resetPassword
    }
})
