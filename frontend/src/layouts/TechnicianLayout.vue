<template>
  <div class="technician-layout">
    <v-navigation-drawer
      v-model="drawer"
      :temporary="mobile"
      class="layout-sidebar"
    >
      <div class="pa-4 sidebar-header">
        <div class="d-flex align-center cursor-pointer" @click="$router.push('/')">
          <v-icon color="white" size="28" class="mr-2">mdi-shield-check</v-icon>
          <span class="text-subtitle-1 font-weight-bold text-white">CUI Complaints</span>
        </div>
        <p class="text-caption mt-1" style="color: rgba(255,255,255,0.7);">{{ authStore.user?.fullName }}</p>
      </div>

      <v-divider style="border-color: rgba(255,255,255,0.15);"></v-divider>

      <v-list density="compact" nav class="mt-2">
        <v-list-item
          v-for="item in menuItems"
          :key="item.title"
          :to="item.to"
          :prepend-icon="item.icon"
          :title="item.title"
          rounded="xl"
          class="my-1 mx-2 sidebar-item"
          @click="onNavClick"
        ></v-list-item>
      </v-list>

      <template v-slot:append>
        <div class="pa-2">
          <v-btn block class="logout-btn" @click="logout">
            <v-icon start>mdi-logout</v-icon>
            Logout
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <v-app-bar app elevation="1" class="layout-header">
      <v-app-bar-nav-icon color="white" @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title class="text-white">{{ currentPageTitle }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon color="white" @click="$router.push('/technician/notifications')">
        <v-icon>mdi-bell</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <router-view />
      </v-container>
    </v-main>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useDisplay } from 'vuetify'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const { mobile } = useDisplay()

const drawer = ref(!mobile.value)

const onNavClick = () => {
  if (mobile.value) {
    drawer.value = false
  }
}

watch(mobile, (isMobile) => {
  drawer.value = !isMobile
})

const menuItems = [
  {
    title: 'Dashboard',
    icon: 'mdi-view-dashboard',
    to: '/technician/dashboard'
  },
  {
    title: 'My Assignments',
    icon: 'mdi-clipboard-list',
    to: '/technician/assignments'
  },
  {
    title: 'Notifications',
    icon: 'mdi-bell',
    to: '/technician/notifications'
  },
  {
    title: 'Settings',
    icon: 'mdi-cog',
    to: '/technician/settings'
  }
]

const currentPageTitle = computed(() => {
  const item = menuItems.find(item => item.to === route.path)
  return item ? item.title : 'Technician Portal'
})

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-header {
  background: linear-gradient(135deg, #312E81 0%, #4338CA 50%, #6D28D9 100%) !important;
}

.layout-sidebar {
  background: linear-gradient(180deg, #312E81 0%, #4338CA 100%) !important;
}

.sidebar-header {
  padding-bottom: 16px;
}

.sidebar-item {
  color: rgba(255, 255, 255, 0.85) !important;
}
.sidebar-item:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}
.sidebar-item :deep(.v-icon) {
  color: rgba(255, 255, 255, 0.7) !important;
}

.v-list-item--active.sidebar-item {
  background-color: rgba(255, 255, 255, 0.18) !important;
  color: #fff !important;
}
.v-list-item--active.sidebar-item :deep(.v-icon) {
  color: #fff !important;
}

.logout-btn {
  color: rgba(255, 255, 255, 0.85) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  background-color: rgba(255, 255, 255, 0.08) !important;
}
.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.15) !important;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
