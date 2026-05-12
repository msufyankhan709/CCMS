<template>
  <div class="reset-pwd-container d-flex align-center justify-center min-vh-100 pa-4">
    <div class="glow-sphere sphere-1"></div>
    <div class="glow-sphere sphere-2"></div>

    <v-card class="reset-card pa-8 rounded-xl elevation-24 text-center" max-width="480">
      <!-- Icon Header -->
      <v-avatar color="indigo-lighten-5" size="72" class="mb-4">
        <v-icon size="36" color="indigo-darken-3">mdi-lock-open-outline</v-icon>
      </v-avatar>

      <!-- Step 0: No token provided -->
      <div v-if="!token">
        <v-avatar color="red-lighten-5" size="80" class="mb-4 animate-scale">
          <v-icon size="42" color="red-darken-2">mdi-alert-circle-outline</v-icon>
        </v-avatar>
        <h2 class="text-h4 font-weight-bold mb-2 text-slate-dark">Invalid Link</h2>
        <p class="text-body-2 text-slate mb-6 leading-relaxed">
          The password reset token is missing. Please request a new password reset link.
        </p>
        <v-btn
          color="indigo"
          class="rounded-xl font-weight-bold py-3 uppercase"
          block
          size="large"
          to="/forgot-password"
        >
          Request Reset Link
        </v-btn>
      </div>

      <!-- Step 1: Input Form -->
      <div v-else-if="!submitted">
        <h2 class="text-h4 font-weight-bold mb-2 text-slate-dark">Reset Password</h2>
        <p class="text-body-2 text-slate mb-6 leading-relaxed">
          Please enter your new password below. Make sure it is secure and strong.
        </p>

        <v-form ref="form" @submit.prevent="handleResetPassword">
          <v-text-field
            v-model="password"
            label="New Password"
            :type="showPassword ? 'text' : 'password'"
            variant="outlined"
            prepend-inner-icon="mdi-lock-outline"
            :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
            @click:append-inner="showPassword = !showPassword"
            :rules="[
              v => !!v || 'Password is required',
              v => v.length >= 6 || 'Password must be at least 6 characters'
            ]"
            required
            class="mb-3 rounded-lg"
          ></v-text-field>

          <v-text-field
            v-model="confirmPassword"
            label="Confirm New Password"
            :type="showPassword ? 'text' : 'password'"
            variant="outlined"
            prepend-inner-icon="mdi-lock-check-outline"
            :rules="[
              v => !!v || 'Confirmation password is required',
              v => v === password || 'Passwords do not match'
            ]"
            required
            class="mb-4 rounded-lg"
          ></v-text-field>

          <v-alert
            v-if="errorMessage"
            type="error"
            variant="tonal"
            class="mb-4 rounded-lg text-left text-body-2"
            closable
            @click:close="errorMessage = ''"
          >
            {{ errorMessage }}
          </v-alert>

          <v-btn
            color="indigo"
            size="large"
            block
            type="submit"
            :loading="loading"
            class="rounded-xl font-weight-bold text-white uppercase py-3 elevation-4"
          >
            Update Password
          </v-btn>
        </v-form>
      </div>

      <!-- Step 2: Success State -->
      <div v-else>
        <v-avatar color="green-lighten-5" size="80" class="mb-4 animate-scale">
          <v-icon size="42" color="green-darken-2">mdi-check-circle-outline</v-icon>
        </v-avatar>
        <h2 class="text-h4 font-weight-bold mb-2 text-slate-dark">Password Updated!</h2>
        <p class="text-body-2 text-slate mb-6 leading-relaxed">
          Your password has been successfully updated. You can now securely log in to the CCMS system.
        </p>
        <v-btn
          color="indigo"
          class="rounded-xl font-weight-bold py-3 uppercase"
          block
          size="large"
          to="/login"
        >
          Go to Login
        </v-btn>
      </div>

      <!-- Footer navigation -->
      <v-divider class="my-6" v-if="token && !submitted"></v-divider>
      <v-btn
        v-if="token && !submitted"
        variant="text"
        color="indigo"
        class="font-weight-bold text-none rounded-lg"
        to="/login"
      >
        Cancel
      </v-btn>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const authStore = useAuthStore()

const token = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const loading = ref(false)
const submitted = ref(false)
const errorMessage = ref('')
const form = ref(null)

onMounted(() => {
  // Extract token from query parameters: /reset-password?token=XYZ
  token.value = route.query.token || ''
})

const handleResetPassword = async () => {
  const { valid } = await form.value.validate()
  if (!valid) return

  loading.value = true
  errorMessage.value = ''

  try {
    await authStore.resetPassword(token.value, password.value)
    submitted.value = true
  } catch (err) {
    errorMessage.value = err.message || 'The password reset token is invalid or has expired.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.reset-pwd-container {
  background: radial-gradient(circle at 0% 0%, #1e1b4b 0%, #0f172a 100%);
  position: relative;
  overflow: hidden;
  min-height: 100vh;
}

.reset-card {
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  width: 100%;
  z-index: 10;
}

.glow-sphere {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.15;
  z-index: 1;
}

.sphere-1 {
  width: 400px;
  height: 400px;
  background: #4f46e5;
  top: -100px;
  left: -100px;
}

.sphere-2 {
  width: 500px;
  height: 500px;
  background: #0284c7;
  bottom: -150px;
  right: -150px;
}

.text-slate-dark {
  color: #0f172a !important;
}

.text-slate {
  color: #475569 !important;
}

.leading-relaxed {
  line-height: 1.625 !important;
}

.uppercase {
  text-transform: uppercase !important;
  letter-spacing: 0.05em !important;
}

.animate-scale {
  animation: scaleUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes scaleUp {
  0% {
    transform: scale(0.6);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
