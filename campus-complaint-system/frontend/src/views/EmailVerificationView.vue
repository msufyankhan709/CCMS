<template>
  <v-app>
    <v-main class="bg-gradient">
      <v-container fluid fill-height>
        <v-row align="center" justify="center">
          <v-col cols="12" sm="8" md="6" lg="5">
            <v-card elevation="12" class="pa-6" rounded="lg">
              <v-card-title class="text-h4 font-weight-bold text-center mb-4">
                <v-icon 
                  :size="60" 
                  :color="verificationState === 'success' ? 'success' : verificationState === 'error' ? 'error' : 'primary'" 
                  class="mb-2"
                >
                  {{ verificationState === 'success' ? 'mdi-check-circle' : verificationState === 'error' ? 'mdi-alert-circle' : 'mdi-email-check' }}
                </v-icon>
                <br />
                Email Verification
              </v-card-title>

              <v-card-text>
                <!-- Loading State -->
                <div v-if="verificationState === 'loading'" class="text-center">
                  <v-progress-circular
                    indeterminate
                    color="primary"
                    size="64"
                    class="mb-4"
                  ></v-progress-circular>
                  <p class="text-h6">Verifying your email...</p>
                  <p class="text-body-2 text-medium-emphasis">Please wait while we confirm your email address.</p>
                </div>

                <!-- Success State -->
                <div v-else-if="verificationState === 'success'" class="text-center">
                  <v-alert
                    type="success"
                    variant="tonal"
                    class="mb-4"
                  >
                    {{ message }}
                  </v-alert>
                  <p class="text-body-1 mb-4">Your email has been successfully verified!</p>
                  <p class="text-body-2 text-medium-emphasis mb-4">
                    You can now log in to your account and start using the Campus Complaint System.
                  </p>
                  <v-btn
                    color="primary"
                    size="large"
                    block
                    @click="goToLogin"
                    prepend-icon="mdi-login"
                  >
                    Go to Login
                  </v-btn>
                </div>

                <!-- Error State -->
                <div v-else-if="verificationState === 'error'" class="text-center">
                  <v-alert
                    type="error"
                    variant="tonal"
                    class="mb-4"
                  >
                    {{ message }}
                  </v-alert>
                  <p class="text-body-2 text-medium-emphasis mb-4">
                    The verification link may be invalid or expired. Please try signing up again or contact support.
                  </p>
                  <v-btn
                    color="primary"
                    size="large"
                    block
                    @click="goToSignup"
                    prepend-icon="mdi-account-plus"
                  >
                    Go to Signup
                  </v-btn>
                </div>

                <!-- No Token State -->
                <div v-else class="text-center">
                  <v-alert
                    type="warning"
                    variant="tonal"
                    class="mb-4"
                  >
                    No verification token found
                  </v-alert>
                  <p class="text-body-2 text-medium-emphasis mb-4">
                    Please use the verification link sent to your email.
                  </p>
                  <v-btn
                    color="primary"
                    size="large"
                    block
                    @click="goToLogin"
                    prepend-icon="mdi-login"
                  >
                    Go to Login
                  </v-btn>
                </div>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

const verificationState = ref('loading')
const message = ref('')

const API_URL = 'http://localhost:8080/api/auth'

onMounted(async () => {
  const token = route.query.token

  if (!token) {
    verificationState.value = 'no-token'
    return
  }

  try {
    const response = await axios.post(`${API_URL}/verify-email?token=${token}`)
    message.value = response.data
    verificationState.value = 'success'
    
    // Redirect to login after 3 seconds
    setTimeout(() => {
      goToLogin()
    }, 3000)
  } catch (error) {
    console.error('Verification error:', error)
    message.value = error.response?.data || 'Verification failed. Please try again.'
    verificationState.value = 'error'
  }
})

const goToLogin = () => {
  router.push('/login')
}

const goToSignup = () => {
  router.push('/signup')
}
</script>

<style scoped>
.bg-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}
</style>
