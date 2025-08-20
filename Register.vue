<template>
  <div class="register-page">
    <v-container fluid class="fill-height">
      <v-row no-gutters class="fill-height">
        <!-- Left Section (Signup Form) -->
        <v-col cols="12" md="6" class="d-flex align-center justify-center pa-6 bg-white">
          <div class="form-wrapper" style="width: 100%; max-width: 420px;">
            <h1 class="text-h4 font-weight-bold mb-4">Create Account</h1>
            <p class="text-subtitle-1 text-medium-emphasis mb-6">
              Sign up for your CCMS account.
            </p>

            <v-form ref="form" @submit.prevent="handleSignup">
              <div class="mb-4">
                <label class="text-subtitle-1 mb-2 d-block">Full Name</label>
                <v-text-field
                  v-model="name"
                  type="text"
                  placeholder="John Doe"
                  variant="outlined"
                  :rules="[rules.required]"
                  bg-color="grey-lighten-4"
                  hide-details
                />
              </div>

              <div class="mb-4">
                <label class="text-subtitle-1 mb-2 d-block">Email</label>
                <v-text-field
                  v-model="email"
                  type="email"
                  placeholder="john@example.com"
                  variant="outlined"
                  :rules="[rules.required, rules.email]"
                  bg-color="grey-lighten-4"
                  hide-details
                />
              </div>

              <div class="mb-4">
                <label class="text-subtitle-1 mb-2 d-block">Password</label>
                <v-text-field
                  v-model="password"
                  type="password"
                  placeholder="Create a secure password"
                  variant="outlined"
                  :rules="[rules.required, rules.password]"
                  bg-color="grey-lighten-4"
                  hide-details
                />
              </div>

              <div class="mb-4">
                <label class="text-subtitle-1 mb-2 d-block">Select Role</label>
                <v-select
                  v-model="role"
                  :items="roles"
                  placeholder="Choose your role"
                  variant="outlined"
                  :rules="[rules.required]"
                  bg-color="grey-lighten-4"
                  hide-details
                />
              </div>

              <div class="mb-6">
                <label class="text-subtitle-1 mb-2 d-block">Department</label>
                <v-text-field
                  v-model="department"
                  type="text"
                  placeholder="e.g. Computer Science"
                  variant="outlined"
                  :rules="[rules.required]"
                  bg-color="grey-lighten-4"
                  hide-details
                />
              </div>

              <v-alert
                v-if="error"
                type="error"
                variant="tonal"
                closable
                class="mb-6"
                @click:close="error = ''"
              >
                {{ error }}
              </v-alert>

              <v-btn
                color="#6B21A8"
                block
                type="submit"
                :loading="loading"
                class="mb-6 signup-btn"
                style="height: 48px"
              >
                Create Account
              </v-btn>

              <div class="text-center">
                <span class="text-body-2 text-medium-emphasis">Already have an account? </span>
                <v-btn
                  variant="text"
                  color="primary"
                  class="text-none text-body-2 font-weight-medium px-1"
                  @click="goToLogin"
                >
                  Login
                </v-btn>
              </div>
            </v-form>
          </div>
        </v-col>

        <!-- Right Section -->
        <v-col cols="12" md="6" class="login-banner pa-0">
          <div class="banner-content text-center">
            <img src="/logo.svg" alt="Logo" class="logo mb-4" />
            <h4 class="text-h5 text-white font-weight-medium mb-2">
              Campus Complaint Management System
            </h4>
            <p class="text-subtitle-1 text-white text-opacity-75">
              Streamline your campus maintenance and support with our efficient complaint management system.
            </p>
          </div>
          <div class="hexagon-pattern"></div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";

const router = useRouter();
const authStore = useAuthStore();
const form = ref(null);

const name = ref("");
const email = ref("");
const password = ref("");
const role = ref("");
const department = ref("");
const loading = ref(false);
const error = ref("");

const roles = [
  { title: "Student", value: "student" },
  { title: "Technician", value: "technician" },
  { title: "Admin", value: "admin" }
];

const rules = {
  required: v => !!v || "This field is required",
  email: v => /.+@.+\..+/.test(v) || "E-mail must be valid",
  password: v => v.length >= 6 || "Password must be at least 6 characters"
};

async function handleSignup() {
  error.value = "";
  const { valid } = await form.value.validate();
  if (!valid) return;

  loading.value = true;
  try {
    const userData = {
      name: name.value,
      email: email.value,
      password: password.value,
      role: role.value,
      department: department.value
    };

    if (role.value === "technician") {
      userData.specialization = department.value;
      userData.status = "Available";
      userData.assignedTasks = 0;
    }

    const { success, error: registerError } = await authStore.register(userData);
    
    if (!success) {
      error.value = registerError || "Registration failed";
      return;
    }

    const loginSuccess = await authStore.login({
      email: email.value,
      password: password.value
    });

    if (loginSuccess) {
      router.push("/dashboard");
    } else {
      error.value = "Registration successful but login failed";
    }
  } catch (e) {
    console.error("Signup failed:", e);
    error.value = "An error occurred during registration";
  } finally {
    loading.value = false;
  }
}

function goToLogin() {
  router.push("/login");
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background-color: #ffffff;
}

.login-banner {
  background-color: #6B21A8;
  min-height: 96vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.banner-content {
  position: relative;
  z-index: 2;
  padding: 2rem;
}

.logo {
  width: 64px;
  height: 64px;
}

.hexagon-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url("/background-pattern.svg");
  background-size: cover;
  background-position: center;
  opacity: 0.2;
}

:deep(.v-field) {
  border-radius: 8px !important;
  background-color: #F8FAFC !important;
}

:deep(.v-field__outline) {
  opacity: 0.08 !important;
}

:deep(.v-field--focused) {
  .v-field__outline {
    opacity: 1 !important;
    color: #6B21A8 !important;
  }
}

.signup-btn {
  border-radius: 8px !important;
  font-weight: 500 !important;
  letter-spacing: 0.5px !important;
}

:deep(.v-btn) {
  text-transform: none;
  letter-spacing: 0;
}

@media (max-width: 960px) {
  .login-banner {
    display: none;
  }

  .form-wrapper {
    padding: 2rem;
  }
}
</style>
