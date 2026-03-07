<template>
  <v-app class="auth-app">
    <!-- Header -->
    <v-app-bar app elevation="0" class="auth-nav">
      <v-container class="d-flex align-center py-0">
        <div class="d-flex align-center cursor-pointer" @click="$router.push('/')">
          <v-icon color="white" size="28" class="mr-2">mdi-shield-check</v-icon>
          <div>
            <span class="text-subtitle-2 font-weight-bold text-white" style="line-height: 1.2;">CUI Complaints</span>
            <br>
            <span class="text-caption text-white" style="opacity: 0.7;">Management System</span>
          </div>
        </div>
        <v-spacer></v-spacer>
        <v-btn variant="outlined" color="white" size="small" rounded="lg" @click="$router.push('/signup')">Sign Up</v-btn>
      </v-container>
    </v-app-bar>

    <v-main class="auth-bg">
      <div class="auth-center">
        <div class="auth-form-wrapper">
          <!-- Card with gradient header -->
          <v-card elevation="8" rounded="xl" class="overflow-hidden">
            <!-- Gradient Header -->
            <div class="card-header text-center py-8 px-4">
              <h2 class="text-h5 font-weight-bold text-white mb-1">Welcome Back</h2>
              <p class="text-body-2 text-white" style="opacity: 0.8;">Sign in to your account</p>
            </div>

            <!-- Form Body -->
            <v-card-text class="pa-6 pa-sm-8">
              <v-form @submit.prevent="handleLogin" ref="loginForm">
                <div class="mb-4">
                  <label class="text-body-2 font-weight-bold mb-1 d-block">
                    <v-icon size="16" class="mr-1">mdi-email</v-icon>Email Address <span class="text-red">*</span>
                  </label>
                  <v-text-field
                    v-model="formData.email"
                    placeholder="Enter your email address"
                    type="email"
                    variant="outlined"
                    density="comfortable"
                    :rules="[rules.required, rules.email]"
                    rounded="lg"
                    bg-color="#F8FAFC"
                    hide-details="auto"
                  ></v-text-field>
                </div>

                <div class="mb-5">
                  <label class="text-body-2 font-weight-bold mb-1 d-block">
                    <v-icon size="16" class="mr-1">mdi-lock</v-icon>Password <span class="text-red">*</span>
                  </label>
                  <v-text-field
                    v-model="formData.password"
                    placeholder="Enter your password"
                    :type="showPassword ? 'text' : 'password'"
                    :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append-inner="showPassword = !showPassword"
                    variant="outlined"
                    density="comfortable"
                    :rules="[rules.required]"
                    rounded="lg"
                    bg-color="#F8FAFC"
                    hide-details="auto"
                  ></v-text-field>
                </div>

                <v-alert
                  v-if="authStore.error"
                  type="error"
                  variant="tonal"
                  class="mb-4"
                  closable
                  rounded="lg"
                >
                  {{ authStore.error }}
                </v-alert>

                <v-btn
                  type="submit"
                  color="#4F46E5"
                  size="large"
                  block
                  rounded="lg"
                  :loading="authStore.loading"
                  class="mb-4 text-white font-weight-bold"
                >
                  Sign In
                </v-btn>

                <!-- Divider -->
                <div class="d-flex align-center my-4">
                  <v-divider></v-divider>
                  <span class="mx-3 text-caption text-grey">or</span>
                  <v-divider></v-divider>
                </div>

                <div class="text-center">
                  <span class="text-body-2">Don't have an account?</span>
                  <a class="text-body-2 font-weight-bold ml-1 cursor-pointer" style="color: #4F46E5;" @click="$router.push('/signup')">Create Account</a>
                </div>
              </v-form>
            </v-card-text>
          </v-card>

          <!-- Demo Account -->
          <v-card elevation="2" class="mt-4 pa-3" rounded="xl">
            <div class="text-center text-caption font-weight-bold mb-1">Demo Account</div>
            <div class="d-flex align-center justify-center">
              <v-icon size="16" color="red" class="mr-2">mdi-shield-account</v-icon>
              <span class="text-caption"><strong>Admin:</strong> admin@campus.edu / admin123</span>
            </div>
          </v-card>
        </div>
      </div>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formData = ref({ email: '', password: '' })
const showPassword = ref(false)
const loginForm = ref(null)

const rules = {
  required: value => !!value || 'This field is required',
  email: value => /.+@.+\..+/.test(value) || 'Invalid email address'
}

const handleLogin = async () => {
  const { valid } = await loginForm.value.validate()
  if (!valid) return
  try {
    await authStore.login(formData.value)
    if (authStore.userRole === 'ADMIN') router.push('/admin')
    else if (authStore.userRole === 'TECHNICIAN') router.push('/technician')
    else router.push('/student')
  } catch (error) {
    console.error('Login error:', error)
  }
}
</script>

<style scoped>
.auth-app { overflow-x: hidden; }
.auth-nav {
  background: linear-gradient(135deg, #312E81 0%, #4338CA 50%, #6D28D9 100%) !important;
}
.auth-bg {
  background: linear-gradient(180deg, #EEF2FF 0%, #F8FAFC 100%);
}
.auth-center {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 64px);
  padding: 24px 16px;
}
.auth-form-wrapper {
  width: 100%;
  max-width: 460px;
}
.card-header {
  background: linear-gradient(135deg, #4338CA 0%, #4F46E5 50%, #6366F1 100%);
}
.cursor-pointer { cursor: pointer; }
</style>
