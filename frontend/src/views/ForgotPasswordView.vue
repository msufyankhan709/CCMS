<template>
  <div class="forgot-pwd-container d-flex align-center justify-center min-vh-100 pa-4">
    <div class="glow-sphere sphere-1"></div>
    <div class="glow-sphere sphere-2"></div>

    <v-card class="forgot-card pa-8 rounded-xl elevation-24 text-center" max-width="480">
      <!-- Icon Header -->
      <v-avatar color="indigo-lighten-5" size="72" class="mb-4">
        <v-icon size="36" color="indigo-darken-3">mdi-lock-reset</v-icon>
      </v-avatar>

      <!-- Step 1: Form Input -->
      <div v-if="!submitted">
        <h2 class="text-h4 font-weight-bold mb-2 text-slate-dark">Forgot Password?</h2>
        <p class="text-body-2 text-slate mb-6 leading-relaxed">
          Enter your registered email address and we'll send you a secure link to reset your password.
        </p>

        <v-form ref="form" @submit.prevent="handleForgotPassword">
          <v-text-field
            v-model="email"
            label="Email Address"
            placeholder="e.g., student@university.edu"
            type="email"
            variant="outlined"
            prepend-inner-icon="mdi-email-outline"
            :rules="[
              v => !!v || 'Email is required',
              v => /.+@.+\..+/.test(v) || 'Must be a valid email address'
            ]"
            required
            class="mb-4 rounded-lg"
          ></v-text-field>

          <v-alert
            v-if="errorMessage"
            type="error"
            variant="tonal"
            class="mb-4 rounded-lg text-left"
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
            Send Reset Link
          </v-btn>
        </v-form>
      </div>

      <!-- Step 2: Success State -->
      <div v-else>
        <v-avatar color="green-lighten-5" size="80" class="mb-4 animate-scale">
          <v-icon size="42" color="green-darken-2">mdi-check-circle-outline</v-icon>
        </v-avatar>
        <h2 class="text-h4 font-weight-bold mb-2 text-slate-dark">Email Sent!</h2>
        <p class="text-body-2 text-slate mb-6 leading-relaxed">
          We have sent a secure password reset link to <strong class="text-indigo">{{ email }}</strong>. Please check your inbox and spam folder.
        </p>
        <v-alert type="info" variant="tonal" class="mb-6 rounded-lg text-left text-body-2">
          The password reset link will expire in <strong>1 hour</strong>.
        </v-alert>
      </div>

      <!-- Footer back-to-login link -->
      <v-divider class="my-6"></v-divider>
      <v-btn
        variant="text"
        color="indigo"
        prepend-icon="mdi-arrow-left"
        class="font-weight-bold text-none rounded-lg"
        to="/login"
      >
        Back to Login
      </v-btn>
    </v-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'

const email = ref('')
const loading = ref(false)
const submitted = ref(false)
const errorMessage = ref('')
const form = ref(null)

const authStore = useAuthStore()

const handleForgotPassword = async () => {
  const { valid } = await form.value.validate()
  if (!valid) return

  loading.value = true
  errorMessage.value = ''

  try {
    await authStore.forgotPassword(email.value)
    submitted.value = true
  } catch (err) {
    errorMessage.value = err.message || 'We could not process your request. Please check your email and try again.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-pwd-container {
  background: radial-gradient(circle at 0% 0%, #1e1b4b 0%, #0f172a 100%);
  position: relative;
  overflow: hidden;
  min-height: 100vh;
}

.forgot-card {
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
