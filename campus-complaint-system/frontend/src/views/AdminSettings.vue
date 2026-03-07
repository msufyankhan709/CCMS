<template>
  <div>
    <h2 class="text-h4 font-weight-bold mb-6">Settings</h2>

    <v-row>
      <!-- Profile Settings -->
      <v-col cols="12" md="6">
        <v-card elevation="2" class="mb-4">
          <v-card-title class="bg-primary text-white">
            <v-icon start>mdi-account-cog</v-icon>
            Profile Settings
          </v-card-title>
          <v-card-text class="pa-6">
            <v-form ref="profileForm">
              <v-text-field
                v-model="profileData.fullName"
                label="Full Name"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-3"
              ></v-text-field>

              <v-text-field
                v-model="profileData.email"
                label="Email"
                prepend-inner-icon="mdi-email"
                variant="outlined"
                type="email"
                class="mb-3"
              ></v-text-field>

              <v-text-field
                v-model="profileData.department"
                label="Department"
                prepend-inner-icon="mdi-office-building"
                variant="outlined"
                class="mb-3"
              ></v-text-field>

              <v-btn
                color="primary"
                @click="saveProfile"
                :loading="savingProfile"
                block
              >
                <v-icon start>mdi-content-save</v-icon>
                Save Profile
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Security Settings -->
      <v-col cols="12" md="6">
        <v-card elevation="2" class="mb-4">
          <v-card-title class="bg-primary text-white">
            <v-icon start>mdi-lock</v-icon>
            Security
          </v-card-title>
          <v-card-text class="pa-6">
            <v-form ref="passwordForm">
              <v-text-field
                v-model="passwordData.currentPassword"
                label="Current Password"
                prepend-inner-icon="mdi-lock-outline"
                :type="showCurrentPassword ? 'text' : 'password'"
                :append-inner-icon="showCurrentPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showCurrentPassword = !showCurrentPassword"
                variant="outlined"
                class="mb-3"
              ></v-text-field>

              <v-text-field
                v-model="passwordData.newPassword"
                label="New Password"
                prepend-inner-icon="mdi-lock-plus"
                :type="showNewPassword ? 'text' : 'password'"
                :append-inner-icon="showNewPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showNewPassword = !showNewPassword"
                variant="outlined"
                :rules="[rules.minLength]"
                class="mb-3"
              ></v-text-field>

              <v-text-field
                v-model="passwordData.confirmPassword"
                label="Confirm New Password"
                prepend-inner-icon="mdi-lock-check"
                :type="showConfirmPassword ? 'text' : 'password'"
                :append-inner-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showConfirmPassword = !showConfirmPassword"
                variant="outlined"
                :rules="[rules.passwordMatch]"
                class="mb-3"
              ></v-text-field>

              <v-btn
                color="primary"
                @click="changePassword"
                :loading="changingPassword"
                block
              >
                <v-icon start>mdi-key-change</v-icon>
                Change Password
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Notification Preferences -->
      <v-col cols="12" md="6">
        <v-card elevation="2" class="mb-4">
          <v-card-title class="bg-primary text-white">
            <v-icon start>mdi-bell-cog</v-icon>
            Notification Preferences
          </v-card-title>
          <v-card-text class="pa-6">
            <v-switch
              v-model="preferences.emailNotifications"
              label="Email Notifications"
              color="primary"
              hide-details
              class="mb-4"
            ></v-switch>

            <v-switch
              v-model="preferences.complaintNotifications"
              label="New Complaint Notifications"
              color="primary"
              hide-details
              class="mb-4"
            ></v-switch>

            <v-switch
              v-model="preferences.assignmentNotifications"
              label="Assignment Notifications"
              color="primary"
              hide-details
              class="mb-4"
            ></v-switch>

            <v-switch
              v-model="preferences.systemAlerts"
              label="System Alerts"
              color="primary"
              hide-details
              class="mb-4"
            ></v-switch>

            <v-btn
              color="primary"
              @click="savePreferences"
              :loading="savingPreferences"
              block
            >
              <v-icon start>mdi-content-save</v-icon>
              Save Preferences
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- System Settings -->
      <v-col cols="12" md="6">
        <v-card elevation="2" class="mb-4">
          <v-card-title class="bg-primary text-white">
            <v-icon start>mdi-cog</v-icon>
            System Settings
          </v-card-title>
          <v-card-text class="pa-6">
            <v-select
              v-model="systemSettings.defaultAssignment"
              label="Default Complaint Assignment"
              :items="['Auto-assign', 'Manual', 'Round-robin']"
              prepend-inner-icon="mdi-account-wrench"
              variant="outlined"
              class="mb-4"
            ></v-select>

            <v-text-field
              v-model.number="systemSettings.autoCloseDays"
              label="Auto-close Resolved Complaints (days)"
              prepend-inner-icon="mdi-calendar"
              variant="outlined"
              type="number"
              hint="0 = Never auto-close"
              persistent-hint
              class="mb-4"
            ></v-text-field>

            <v-select
              v-model="systemSettings.theme"
              label="Theme Preference"
              :items="['Light', 'Dark', 'System']"
              prepend-inner-icon="mdi-theme-light-dark"
              variant="outlined"
              class="mb-4"
            ></v-select>

            <v-row>
              <v-col cols="6">
                <v-btn
                  color="primary"
                  @click="saveSystemSettings"
                  :loading="savingSystem"
                  block
                >
                  Save
                </v-btn>
              </v-col>
              <v-col cols="6">
                <v-btn
                  variant="outlined"
                  @click="resetSystemSettings"
                  block
                >
                  Reset
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Success Snackbar -->
    <v-snackbar
      v-model="snackbar"
      :color="snackbarColor"
      timeout="3000"
    >
      {{ snackbarMessage }}
      <template v-slot:actions>
        <v-btn variant="text" @click="snackbar = false">Close</v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

const profileForm = ref(null)
const passwordForm = ref(null)

const profileData = ref({
  fullName: '',
  email: '',
  department: ''
})

const passwordData = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const preferences = ref({
  emailNotifications: true,
  complaintNotifications: true,
  assignmentNotifications: true,
  systemAlerts: true
})

const systemSettings = ref({
  defaultAssignment: 'Auto-assign',
  autoCloseDays: 30,
  theme: 'Light'
})

const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const savingProfile = ref(false)
const changingPassword = ref(false)
const savingPreferences = ref(false)
const savingSystem = ref(false)

const snackbar = ref(false)
const snackbarMessage = ref('')
const snackbarColor = ref('success')

const rules = {
  minLength: v => (v && v.length >= 6) || 'Password must be at least 6 characters',
  passwordMatch: v => v === passwordData.value.newPassword || 'Passwords do not match'
}

onMounted(() => {
  loadUserData()
  loadPreferences()
  loadSystemSettings()
})

const loadUserData = () => {
  if (authStore.user) {
    profileData.value.fullName = authStore.user.fullName || ''
    profileData.value.email = authStore.user.email || ''
    profileData.value.department = authStore.user.department || ''
  }
}

const loadPreferences = () => {
  const saved = localStorage.getItem('notificationPreferences')
  if (saved) {
    preferences.value = JSON.parse(saved)
  }
}

const loadSystemSettings = () => {
  const saved = localStorage.getItem('systemSettings')
  if (saved) {
    systemSettings.value = JSON.parse(saved)
  }
}

const saveProfile = async () => {
  savingProfile.value = true
  try {
    // TODO: Implement API call to update profile
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    showSnackbar('Profile updated successfully', 'success')
  } catch (error) {
    showSnackbar('Failed to update profile', 'error')
  } finally {
    savingProfile.value = false
  }
}

const changePassword = async () => {
  const { valid } = await passwordForm.value.validate()
  if (!valid) return

  changingPassword.value = true
  try {
    await authStore.changePassword(passwordData.value.currentPassword, passwordData.value.newPassword)
    
    passwordData.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
    showSnackbar('Password changed successfully', 'success')
  } catch (error) {
    showSnackbar(error.response?.data?.error || 'Failed to change password', 'error')
  } finally {
    changingPassword.value = false
  }
}

const savePreferences = async () => {
  savingPreferences.value = true
  try {
    localStorage.setItem('notificationPreferences', JSON.stringify(preferences.value))
    await new Promise(resolve => setTimeout(resolve, 500))
    showSnackbar('Preferences saved successfully', 'success')
  } catch (error) {
    showSnackbar('Failed to save preferences', 'error')
  } finally {
    savingPreferences.value = false
  }
}

const saveSystemSettings = async () => {
  savingSystem.value = true
  try {
    localStorage.setItem('systemSettings', JSON.stringify(systemSettings.value))
    await new Promise(resolve => setTimeout(resolve, 500))
    showSnackbar('System settings saved successfully', 'success')
  } catch (error) {
    showSnackbar('Failed to save system settings', 'error')
  } finally {
    savingSystem.value = false
  }
}

const resetSystemSettings = () => {
  systemSettings.value = {
    defaultAssignment: 'Auto-assign',
    autoCloseDays: 30,
    theme: 'Light'
  }
  showSnackbar('System settings reset to defaults', 'info')
}

const showSnackbar = (message, color = 'success') => {
  snackbarMessage.value = message
  snackbarColor.value = color
  snackbar.value = true
}
</script>
