import { defineStore } from 'pinia'
import axios from 'axios'

const API_URL = 'http://localhost:8080/api'

export const useNotificationStore = defineStore('notification', {
    state: () => ({
        notifications: [],
        unreadCount: 0,
        loading: false,
        error: null
    }),

    getters: {
        unreadNotifications: (state) => state.notifications.filter(n => !n.isRead),
        readNotifications: (state) => state.notifications.filter(n => n.isRead)
    },

    actions: {
        async fetchNotifications() {
            this.loading = true
            this.error = null
            try {
                const token = localStorage.getItem('token')
                const response = await axios.get(`${API_URL}/notifications`, {
                    headers: { Authorization: `Bearer ${token}` }
                })
                this.notifications = response.data
                await this.fetchUnreadCount()
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch notifications'
                console.error('Error fetching notifications:', error)
            } finally {
                this.loading = false
            }
        },

        async fetchUnreadCount() {
            try {
                const token = localStorage.getItem('token')
                const response = await axios.get(`${API_URL}/notifications/unread-count`, {
                    headers: { Authorization: `Bearer ${token}` }
                })
                this.unreadCount = response.data.count
            } catch (error) {
                console.error('Error fetching unread count:', error)
            }
        },

        async markAsRead(notificationId) {
            try {
                const token = localStorage.getItem('token')
                await axios.put(`${API_URL}/notifications/${notificationId}/read`, {}, {
                    headers: { Authorization: `Bearer ${token}` }
                })

                const notification = this.notifications.find(n => n.id === notificationId)
                if (notification) {
                    notification.isRead = true
                    this.unreadCount = Math.max(0, this.unreadCount - 1)
                }
            } catch (error) {
                console.error('Error marking notification as read:', error)
                throw error
            }
        },

        async markAllAsRead() {
            try {
                const token = localStorage.getItem('token')
                await axios.put(`${API_URL}/notifications/read-all`, {}, {
                    headers: { Authorization: `Bearer ${token}` }
                })

                this.notifications.forEach(n => n.isRead = true)
                this.unreadCount = 0
            } catch (error) {
                console.error('Error marking all as read:', error)
                throw error
            }
        },

        async deleteNotification(notificationId) {
            try {
                const token = localStorage.getItem('token')
                await axios.delete(`${API_URL}/notifications/${notificationId}`, {
                    headers: { Authorization: `Bearer ${token}` }
                })

                const index = this.notifications.findIndex(n => n.id === notificationId)
                if (index !== -1) {
                    const wasUnread = !this.notifications[index].isRead
                    this.notifications.splice(index, 1)
                    if (wasUnread) {
                        this.unreadCount = Math.max(0, this.unreadCount - 1)
                    }
                }
            } catch (error) {
                console.error('Error deleting notification:', error)
                throw error
            }
        }
    }
})
