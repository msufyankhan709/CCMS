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
        <v-btn variant="outlined" color="white" size="small" rounded="lg" @click="$router.push('/login')">
          <v-icon start size="16">mdi-account</v-icon>
          Login
        </v-btn>
      </v-container>
    </v-app-bar>

    <v-main class="auth-bg">
      <div class="auth-center">
        <div class="auth-form-wrapper">
          <!-- Card with gradient header -->
          <v-card elevation="8" rounded="xl" class="overflow-hidden">
            <!-- Gradient Header -->
            <div class="card-header text-center py-7 px-4">
              <h2 class="text-h5 font-weight-bold text-white mb-1">Create Account</h2>
              <p class="text-body-2 text-white" style="opacity: 0.8;">Join the Campus Complaint System</p>
            </div>

            <!-- Form Body -->
            <v-card-text class="pa-6 pa-sm-8">
              <v-form @submit.prevent="handleSignup" ref="signupForm">

                <div class="mb-4">
                  <label class="text-body-2 font-weight-bold mb-1 d-block">
                    <v-icon size="16" class="mr-1">mdi-account-box</v-icon>Full Name <span class="text-red">*</span>
                  </label>
                  <v-text-field
                    v-model="formData.fullName"
                    placeholder="Enter your full name"
                    variant="outlined"
                    density="comfortable"
                    :rules="[rules.required]"
                    rounded="lg"
                    bg-color="#F8FAFC"
                    hide-details="auto"
                  ></v-text-field>
                </div>

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

                <div class="mb-4">
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
                    :rules="[rules.required, rules.minLength]"
                    rounded="lg"
                    bg-color="#F8FAFC"
                    hide-details="auto"
                  ></v-text-field>
                </div>

                <v-row class="mb-4" no-gutters>
                  <v-col cols="12" sm="6" class="pr-sm-2 mb-4 mb-sm-0">
                    <label class="text-body-2 font-weight-bold mb-1 d-block">
                      <v-icon size="16" class="mr-1">mdi-office-building</v-icon>Department <span class="text-red">*</span>
                    </label>
                    <v-select
                      v-model="formData.department"
                      placeholder="Select department"
                      :items="departments"
                      variant="outlined"
                      density="comfortable"
                      :rules="[rules.required]"
                      rounded="lg"
                      bg-color="#F8FAFC"
                      hide-details="auto"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" sm="6" class="pl-sm-2">
                    <label class="text-body-2 font-weight-bold mb-1 d-block">
                      <v-icon size="16" class="mr-1">mdi-account-group</v-icon>I am a... <span class="text-red">*</span>
                    </label>
                    <v-select
                      v-model="formData.role"
                      placeholder="Select role"
                      :items="roles"
                      item-title="title"
                      item-value="value"
                      variant="outlined"
                      density="comfortable"
                      :rules="[rules.required]"
                      rounded="lg"
                      bg-color="#F8FAFC"
                      hide-details="auto"
                    ></v-select>
                  </v-col>
                </v-row>

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
                  Sign Up
                </v-btn>

                <!-- Divider -->
                <div class="d-flex align-center my-4">
                  <v-divider></v-divider>
                  <span class="mx-3 text-caption text-grey">or</span>
                  <v-divider></v-divider>
                </div>

                <div class="text-center">
                  <span class="text-body-2">Already have an account?</span>
                  <a class="text-body-2 font-weight-bold ml-1 cursor-pointer" style="color: #4F46E5;" @click="$router.push('/login')">Sign In</a>
                </div>
              </v-form>
            </v-card-text>
          </v-card>
        </div>
      </div>

      <!-- Success Dialog -->
      <v-dialog v-model="showSuccessDialog" :max-width="$vuetify.display.mobile ? undefined : 500" :fullscreen="$vuetify.display.mobile" persistent>
        <v-card rounded="xl" class="overflow-hidden">
          <div class="card-header text-center py-6">
            <v-icon size="48" color="white" class="mb-2">mdi-email-check</v-icon>
            <h3 class="text-h6 text-white">Check Your Email</h3>
          </div>

          <v-card-text class="pa-6">
            <p class="text-body-1 mb-3">
              Thank you for signing up! We've sent a verification email to:
            </p>
            <p class="text-h6 text-center mb-3" style="color: #4F46E5;">
              {{ formData.email }}
            </p>
            <v-alert type="info" variant="tonal" class="mb-3" rounded="lg">
              Please check your inbox and click the verification link to activate your account.
            </v-alert>
            <p class="text-body-2 text-medium-emphasis">
              The verification link will expire in 24 hours. If you don't see the email, please check your spam folder.
            </p>
          </v-card-text>

          <v-card-actions class="pa-4">
            <v-spacer></v-spacer>
            <v-btn color="#4F46E5" variant="flat" @click="goToLogin" size="large" rounded="lg" class="text-white">
              Go to Login
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formData = ref({
  password: '',
  fullName: '',
  email: '',
  department: '',
  role: 'STUDENT'
})

const showPassword = ref(false)
const signupForm = ref(null)
const showSuccessDialog = ref(false)

const departments = [
  'Computer Science',
  'Management Science',
  'Environment Science',
  'Humanities',
  'Mathematics',
  'Biotechnology',
  'Economics'
]

const roles = [
  { title: 'Student', value: 'STUDENT' },
  { title: 'Faculty', value: 'FACULTY' }
]

const rules = {
  required: value => !!value || 'This field is required',
  email: value => /.+@.+\..+/.test(value) || 'Invalid email address',
  minLength: value => value.length >= 6 || 'Password must be at least 6 characters'
}

const handleSignup = async () => {
  const { valid } = await signupForm.value.validate()
  if (!valid) return
  try {
    await authStore.signup(formData.value)
    showSuccessDialog.value = true
  } catch (error) {
    console.error('Signup error:', error)
  }
}

const goToLogin = () => {
  showSuccessDialog.value = false
  router.push('/login')
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
  max-width: 520px;
}
.card-header {
  background: linear-gradient(135deg, #4338CA 0%, #4F46E5 50%, #6366F1 100%);
}
.cursor-pointer { cursor: pointer; }
</style>
