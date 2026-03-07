<template>
  <div>
    <v-row class="mb-4">
      <v-col cols="12">
        <h2 class="text-h4 font-weight-bold">Settings</h2>
        <p class="text-body-1 text-grey">Manage your account preferences</p>
      </v-col>
    </v-row>

    <!-- Profile Settings -->
    <v-card elevation="2" class="mb-4">
      <v-card-title>Profile Information</v-card-title>
      <v-card-text>
        <v-form>
          <v-text-field
            v-model="profile.fullName"
            label="Full Name"
            variant="outlined"
            prepend-inner-icon="mdi-account"
            class="mb-3"
          ></v-text-field>

          <v-text-field
            v-model="profile.email"
            label="Email"
            variant="outlined"
            prepend-inner-icon="mdi-email"
            readonly
            class="mb-3"
          ></v-text-field>

          <v-text-field
            v-model="profile.department"
            label="Department"
            variant="outlined"
            prepend-inner-icon="mdi-office-building"
            class="mb-3"
          ></v-text-field>

          <v-btn color="primary" @click="saveProfile">
            <v-icon start>mdi-content-save</v-icon>
            Save Changes
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>

    <!-- Password Change -->
    <v-card elevation="2" class="mb-4">
      <v-card-title>Change Password</v-card-title>
      <v-card-text>
        <v-form>
          <v-text-field
            v-model="password.current"
            label="Current Password"
            type="password"
            variant="outlined"
            prepend-inner-icon="mdi-lock"
            class="mb-3"
          ></v-text-field>

          <v-text-field
            v-model="password.new"
            label="New Password"
            type="password"
            variant="outlined"
            prepend-inner-icon="mdi-lock-reset"
            class="mb-3"
          ></v-text-field>

          <v-text-field
            v-model="password.confirm"
            label="Confirm New Password"
            type="password"
            variant="outlined"
            prepend-inner-icon="mdi-lock-check"
            class="mb-3"
          ></v-text-field>

          <v-btn color="primary" @click="changePassword">
            <v-icon start>mdi-key</v-icon>
            Update Password
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>

    <!-- Notification Preferences -->
    <v-card elevation="2">
      <v-card-title>Notification Preferences</v-card-title>
      <v-card-text>
        <v-switch
          v-model="preferences.emailNotifications"
          label="Email Notifications"
          color="primary"
          hide-details
          class="mb-3"
        ></v-switch>

        <v-switch
          v-model="preferences.statusUpdates"
          label="Status Update Notifications"
          color="primary"
          hide-details
          class="mb-3"
        ></v-switch>

        <v-switch
          v-model="preferences.assignmentNotifications"
          label="Assignment Notifications"
          color="primary"
          hide-details
          class="mb-3"
        ></v-switch>

        <v-btn color="primary" @click="savePreferences">
          <v-icon start>mdi-content-save</v-icon>
          Save Preferences
        </v-btn>
      </v-card-text>
    </v-card>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const profile = ref({
  fullName: authStore.user?.fullName || '',
  email: authStore.user?.email || '',
  department: authStore.user?.department || ''
})

const password = ref({
  current: '',
  new: '',
  confirm: ''
})


const preferences = ref({
  emailNotifications: true,
  statusUpdates: true,
  assignmentNotifications: true
})

const saveProfile = () => {
  // TODO: Implement profile update API call
  alert('Profile updated successfully!')
}

const changePassword = async () => {
  if (password.value.new !== password.value.confirm) {
    alert('Passwords do not match!')
    return
  }
  if (password.value.new.length < 6) {
    alert('New password must be at least 6 characters!')
    return
  }
  try {
    await authStore.changePassword(password.value.current, password.value.new)
    alert('Password changed successfully!')
    password.value = { current: '', new: '', confirm: '' }
  } catch (err) {
    alert(err.response?.data?.error || 'Failed to change password')
  }
}

const savePreferences = () => {
  // TODO: Implement preferences save API call
  alert('Preferences saved successfully!')
}

</script>
