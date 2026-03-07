<template>
  <div>
    <h2 class="text-h4 font-weight-bold mb-6">Notifications</h2>

    <!-- Header Actions -->
    <v-row class="mb-4">
      <v-col cols="12" class="d-flex justify-space-between align-center">
        <v-tabs v-model="activeTab" color="primary">
          <v-tab value="all">All</v-tab>
          <v-tab value="unread">
            Unread
            <v-badge
              v-if="notificationStore.unreadCount > 0"
              :content="notificationStore.unreadCount"
              color="error"
              inline
              class="ml-2"
            ></v-badge>
          </v-tab>
          <v-tab value="read">Read</v-tab>
        </v-tabs>

        <v-btn
          v-if="filteredNotifications.length > 0 && activeTab !== 'read'"
          color="primary"
          variant="text"
          @click="markAllRead"
          :loading="markingAllRead"
        >
          <v-icon start>mdi-check-all</v-icon>
          Mark All as Read
        </v-btn>
      </v-col>
    </v-row>

    <!-- Notifications List -->
    <v-card elevation="2">
      <v-card-text class="pa-0">
        <v-progress-linear v-if="notificationStore.loading" indeterminate color="primary"></v-progress-linear>

        <v-list v-if="filteredNotifications.length > 0" lines="three">
          <template v-for="(notification, index) in filteredNotifications" :key="notification.id">
            <v-list-item
              :class="{ 'bg-blue-lighten-5': !notification.isRead }"
              @click="handleNotificationClick(notification)"
            >
              <template v-slot:prepend>
                <v-avatar :color="getNotificationColor(notification.type)">
                  <v-icon color="white">{{ getNotificationIcon(notification.type) }}</v-icon>
                </v-avatar>
              </template>

              <v-list-item-title class="font-weight-bold">
                {{ notification.title }}
              </v-list-item-title>

              <v-list-item-subtitle class="mt-1">
                {{ notification.message }}
              </v-list-item-subtitle>

              <v-list-item-subtitle class="mt-2 text-caption text-grey">
                {{ formatDate(notification.createdAt) }}
              </v-list-item-subtitle>

              <template v-slot:append>
                <div class="d-flex align-center">
                  <v-btn
                    icon
                    size="small"
                    variant="text"
                    @click.stop="toggleReadStatus(notification)"
                    :title="notification.isRead ? 'Mark as unread' : 'Mark as read'"
                  >
                    <v-icon>{{ notification.isRead ? 'mdi-email-open' : 'mdi-email' }}</v-icon>
                  </v-btn>

                  <v-btn
                    icon
                    size="small"
                    variant="text"
                    color="error"
                    @click.stop="deleteNotif(notification)"
                    title="Delete notification"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </div>
              </template>
            </v-list-item>

            <v-divider v-if="index < filteredNotifications.length - 1"></v-divider>
          </template>
        </v-list>

        <!-- Empty State -->
        <div v-else class="text-center pa-8">
          <v-icon size="64" color="grey-lighten-1">mdi-bell-outline</v-icon>
          <p class="text-h6 mt-4 text-grey">
            {{ activeTab === 'unread' ? 'No unread notifications' : activeTab === 'read' ? 'No read notifications' : 'No notifications yet' }}
          </p>
          <p class="text-body-2 text-grey-lighten-1">
            You'll see notifications here when there are updates
          </p>
        </div>
      </v-card-text>
    </v-card>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title>Delete Notification?</v-card-title>
        <v-card-text>
          Are you sure you want to delete this notification? This action cannot be undone.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="deleteDialog = false">Cancel</v-btn>
          <v-btn color="error" @click="confirmDelete" :loading="deleting">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../stores/notification'

const router = useRouter()
const notificationStore = useNotificationStore()

const activeTab = ref('all')
const deleteDialog = ref(false)
const notificationToDelete = ref(null)
const deleting = ref(false)
const markingAllRead = ref(false)

onMounted(() => {
  notificationStore.fetchNotifications()
})

const filteredNotifications = computed(() => {
  if (activeTab.value === 'unread') {
    return notificationStore.unreadNotifications
  } else if (activeTab.value === 'read') {
    return notificationStore.readNotifications
  }
  return notificationStore.notifications
})

const handleNotificationClick = async (notification) => {
  if (!notification.isRead) {
    await notificationStore.markAsRead(notification.id)
  }
  
  // Navigate to related entity if available
  if (notification.relatedEntityId && notification.type === 'COMPLAINT') {
    router.push(`/technician/complaints`)
  }
}

const toggleReadStatus = async (notification) => {
  if (!notification.isRead) {
    await notificationStore.markAsRead(notification.id)
  }
}

const deleteNotif = (notification) => {
  notificationToDelete.value = notification
  deleteDialog.value = true
}

const confirmDelete = async () => {
  deleting.value = true
  try {
    await notificationStore.deleteNotification(notificationToDelete.value.id)
    deleteDialog.value = false
    notificationToDelete.value = null
  } catch (error) {
    console.error('Error deleting notification:', error)
  } finally {
    deleting.value = false
  }
}

const markAllRead = async () => {
  markingAllRead.value = true
  try {
    await notificationStore.markAllAsRead()
  } catch (error) {
    console.error('Error marking all as read:', error)
  } finally {
    markingAllRead.value = false
  }
}

const getNotificationIcon = (type) => {
  const icons = {
    COMPLAINT: 'mdi-alert-circle',
    SYSTEM: 'mdi-cog',
    ASSIGNMENT: 'mdi-account-wrench',
    USER: 'mdi-account',
    RESOLVED: 'mdi-check-circle',
    COMMENT: 'mdi-comment'
  }
  return icons[type] || 'mdi-bell'
}

const getNotificationColor = (type) => {
  const colors = {
    COMPLAINT: 'blue',
    SYSTEM: 'grey',
    ASSIGNMENT: 'orange',
    USER: 'purple',
    RESOLVED: 'green',
    COMMENT: 'teal'
  }
  return colors[type] || 'grey'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`
  if (hours < 24) return `${hours} hour${hours > 1 ? 's' : ''} ago`
  if (days < 7) return `${days} day${days > 1 ? 's' : ''} ago`
  
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>
