<template>
  <div class="login-page">
    <v-container fluid class="fill-height">
      <v-row no-gutters class="fill-height">
        <!-- Left Section (Login Form) -->
        <v-col cols="12" md="6" class="d-flex align-center justify-center pa-6 bg-white">
          <v-card flat class="pa-6" max-width="400" rounded="lg">
            <h1 class="text-h4 font-weight-bold mb-4">Welcome Back!</h1>
            <p class="text-subtitle-1 text-medium-emphasis mb-6">
              Enter your credentials to access your RoleAccess account.
            </p>

            <v-form ref="form" @submit.prevent="handleLogin">
              <v-text-field
                v-model="email"
                label="Email"
                type="email"
                variant="outlined"
                dense
                class="mb-4"
                placeholder="john.doe@example.com"
              ></v-text-field>

              <v-text-field
                v-model="password"
                label="Password"
                type="password"
                variant="outlined"
                dense
                class="mb-4"
                placeholder="Enter your password"
              >
                <template v-slot:append-outer>
                  <v-btn text color="primary" class="text-none text-caption" @click="forgotPassword">
                    Forgot your password?
                  </v-btn>
                </template>
              </v-text-field>

              <v-alert
                v-if="error"
                type="error"
                variant="tonal"
                closable
                class="mb-4"
                @click:close="error = ''"
              >
                {{ error }}
              </v-alert>

              <v-btn
                color="#4C6EF5"
                block
                class="mb-4"
                type="submit"
                :loading="loading"
                rounded
              >
                Login
              </v-btn>

              <div class="text-center">
                <span class="text-caption text-medium-emphasis">Don't have an account? </span>
                <v-btn text color="primary" class="text-none text-caption" @click="goToSignup">
                  Sign Up
                </v-btn>
              </div>
            </v-form>
          </v-card>
        </v-col>

        <!-- Right Section (RoleAccess Info) -->
        <v-col cols="12" md="6" class="d-flex align-center justify-center pa-6" style="background-color: #A31F3D; color: white;">
          <div class="text-center">
            <div class="mb-6" style="position: relative;">
              <!-- Placeholder for decorative elements (e.g., hexagons and logo) -->
              <div class="hexagon-pattern"></div>
            </div>
            <h2 class="text-h5 font-weight-medium mb-4">RoleAccess</h2>
            <p class="text-subtitle-1 text-opacity-75">
              Your secure portal for streamlined access and role management.
            </p>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const router = useRouter();
const authStore = useAuthStore();
const form = ref(null);

const email = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');

const rules = {
  required: v => !!v || 'This field is required',
  email: v => /.+@.+\..+/.test(v) || 'E-mail must be valid'
};

async function handleLogin() {
  error.value = '';
  const { valid } = await form.value.validate();
  if (!valid) return;

  loading.value = true;
  try {
    const success = await authStore.login({
      email: email.value,
      password: password.value
    });

    if (success) {
      router.push('/dashboard');
    } else {
      error.value = 'Invalid email or password';
    }
  } catch (e) {
    console.error('Login failed:', e);
    error.value = 'An error occurred during login';
  } finally {
    loading.value = false;
  }
}

function forgotPassword() {
  router.push('/forgot-password');
}

function goToSignup() {
  router.push('/register');
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  overflow: hidden;
}

.v-card {
  border-radius: 8px;
}

.hexagon-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="40" fill="rgba(255, 255, 255, 0.1)"/></svg>') repeat;
  opacity: 0.3;
  z-index: 0;
}

.v-btn {
  text-transform: none;
  height: 48px;
}

.v-text-field.v-text-field--outlined {
  border-radius: 4px;
}
</style>