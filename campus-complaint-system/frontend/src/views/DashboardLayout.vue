<template>
  <v-app>
    <!-- App Bar -->
    <v-app-bar color="primary" elevation="2">
      <template v-slot:prepend>
        <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      </template>

      <v-toolbar-title class="font-weight-bold">
        <v-icon class="mr-2">mdi-alert-circle-outline</v-icon>
        Campus Complaints
      </v-toolbar-title>

      <v-spacer></v-spacer>

      <v-chip class="mr-4" color="white" variant="outlined">
        <v-icon start>mdi-account-circle</v-icon>
        {{ authStore.user?.fullName }}
      </v-chip>

      <v-btn icon @click="handleLogout">
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <!-- Navigation Drawer -->
    <v-navigation-drawer v-model="drawer" temporary>
      <v-list>
        <v-list-item
          prepend-icon="mdi-account"
          :title="authStore.user?.fullName"
          :subtitle="authStore.user?.role"
        ></v-list-item>
      </v-list>

      <v-divider></v-divider>

      <v-list density="comfortable" nav>
        <!-- Student/Faculty Menu -->
        <template v-if="['STUDENT', 'FACULTY'].includes(authStore.userRole)">
          <v-list-item
            prepend-icon="mdi-home"
            title="Dashboard"
            value="home"
            @click="$router.push('/home')"
          ></v-list-item>
        </template>

        <!-- Admin Menu -->
        <template v-if="authStore.userRole === 'ADMIN'">
          <v-list-item
            prepend-icon="mdi-view-dashboard"
            title="Admin Dashboard"
            value="admin"
            @click="$router.push('/admin')"
          ></v-list-item>
        </template>

        <!-- Technician Menu -->
        <template v-if="authStore.userRole === 'TECHNICIAN'">
          <v-list-item
            prepend-icon="mdi-toolbox"
            title="My Tasks"
            value="technician"
            @click="$router.push('/technician')"
          ></v-list-item>
        </template>
      </v-list>
    </v-navigation-drawer>

    <!-- Main Content -->
    <v-main class="bg-grey-lighten-4">
      <v-container fluid>
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const drawer = ref(false)

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>
