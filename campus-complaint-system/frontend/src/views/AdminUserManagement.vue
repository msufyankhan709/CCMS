<template>
  <div>
    <!-- Header -->
    <v-row class="mb-4">
      <v-col cols="12">
        <div :class="mobile ? '' : 'd-flex justify-space-between align-center'">
          <h2 :class="mobile ? 'text-h5 mb-3' : 'text-h4'" class="font-weight-bold">User Management</h2>
          <v-btn color="primary" prepend-icon="mdi-plus" :block="mobile" @click="openAddDialog">
            ADD USER
          </v-btn>
        </div>
      </v-col>
    </v-row>

    <!-- Filters -->
    <v-card elevation="2" class="mb-4">
      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="searchQuery"
              prepend-inner-icon="mdi-magnify"
              label="Search users"
              variant="outlined"
              density="compact"
              clearable
              @input="filterUsers"
            ></v-text-field>
          </v-col>
          <v-col cols="6" md="4">
            <v-select
              v-model="roleFilter"
              label="Filter by Role"
              :items="roleOptions"
              variant="outlined"
              density="compact"
              clearable
              @update:modelValue="filterUsers"
            ></v-select>
          </v-col>
          <v-col cols="6" md="4">
            <v-select
              v-model="departmentFilter"
              label="Filter by Dept"
              :items="departmentOptions"
              variant="outlined"
              density="compact"
              clearable
              @update:modelValue="filterUsers"
            ></v-select>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Users Table / Card List -->
    <v-card elevation="2">
      <v-card-text>
        <!-- Desktop: Data Table -->
        <v-data-table
          v-if="!mobile"
          :headers="headers"
          :items="filteredUsers"
          :items-per-page="10"
          :loading="loading"
        >
          <template v-slot:item.fullName="{ item }">
            <span :class="{ 'text-grey': !item.active }">
              {{ item.fullName }}
            </span>
          </template>

          <template v-slot:item.email="{ item }">
            <span :class="{ 'text-grey': !item.active }">
              {{ item.email }}
            </span>
          </template>
          <template v-slot:item.role="{ item }">
            <v-chip :color="getRoleColor(item.role)" size="small">
              {{ item.role === 'TECHNICIAN' ? (item.technicianType || item.role) : item.role }}
            </v-chip>
          </template>

          <template v-slot:item.active="{ item }">
            <v-chip :color="item.active ? 'green' : 'red'" size="small">
              {{ item.active ? 'Active' : 'Inactive' }}
            </v-chip>
          </template>

          <template v-slot:item.actions="{ item }">
            <v-tooltip text="Base admin cannot be edited" :disabled="item.username !== 'admin'">
              <template v-slot:activator="{ props }">
                <v-btn
                  v-bind="props"
                  icon
                  size="small"
                  @click="openEditDialog(item)"
                  class="mr-2"
                  :disabled="item.username === 'admin'"
                >
                  <v-icon color="primary">mdi-pencil</v-icon>
                </v-btn>
              </template>
            </v-tooltip>

            <v-tooltip text="Base admin cannot be deleted" :disabled="item.username !== 'admin'">
              <template v-slot:activator="{ props }">
                <v-btn
                  v-bind="props"
                  icon
                  size="small"
                  @click="confirmDelete(item)"
                  :disabled="item.username === 'admin'"
                >
                  <v-icon color="error">mdi-delete</v-icon>
                </v-btn>
              </template>
            </v-tooltip>
          </template>
        </v-data-table>

        <!-- Mobile: Card List -->
        <div v-else>
          <v-progress-linear v-if="loading" indeterminate></v-progress-linear>
          <div v-else-if="filteredUsers.length === 0" class="text-center py-8 text-grey">
            <v-icon size="48">mdi-account-off</v-icon>
            <p class="mt-2">No users found</p>
          </div>
          <v-card
            v-for="item in filteredUsers"
            :key="item.id"
            variant="outlined"
            class="mb-3 pa-3"
          >
            <div class="d-flex justify-space-between align-start mb-1">
              <div class="font-weight-bold text-body-1" :class="{ 'text-grey': !item.active }">
                {{ item.fullName }}
              </div>
              <v-chip :color="item.active ? 'green' : 'red'" size="x-small">
                {{ item.active ? 'Active' : 'Inactive' }}
              </v-chip>
            </div>
            <div class="text-caption mb-1" :class="item.active ? 'text-grey' : 'text-grey-lighten-1'">
              {{ item.email }}
            </div>
            <div class="d-flex flex-wrap align-center ga-2 mb-2">
              <v-chip :color="getRoleColor(item.role)" size="x-small">
                {{ item.role === 'TECHNICIAN' ? (item.technicianType || item.role) : item.role }}
              </v-chip>
              <span v-if="item.department" class="text-caption text-grey">{{ item.department }}</span>
            </div>
            <div class="d-flex ga-2">
              <v-btn
                size="x-small"
                color="primary"
                variant="tonal"
                @click="openEditDialog(item)"
                :disabled="item.username === 'admin'"
              >
                <v-icon start size="14">mdi-pencil</v-icon> Edit
              </v-btn>
              <v-btn
                size="x-small"
                color="error"
                variant="tonal"
                @click="confirmDelete(item)"
                :disabled="item.username === 'admin'"
              >
                <v-icon start size="14">mdi-delete</v-icon> Delete
              </v-btn>
            </div>
          </v-card>
        </div>
      </v-card-text>
    </v-card>

    <!-- Add/Edit User Dialog -->
    <v-dialog v-model="userDialog" :max-width="mobile ? undefined : 600" :fullscreen="mobile">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          {{ editMode ? 'Edit User' : 'Add New User' }}
          <v-btn v-if="mobile" icon variant="text" @click="userDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-form ref="userForm">
            <v-text-field
              v-model="currentUser.username"
              label="Username"
              :rules="[rules.required]"
              variant="outlined"
              class="mb-3"
              :disabled="editMode"
            ></v-text-field>

            <v-text-field
              v-model="currentUser.fullName"
              label="Full Name"
              :rules="[rules.required]"
              variant="outlined"
              class="mb-3"
            ></v-text-field>

            <v-text-field
              v-model="currentUser.email"
              label="Email"
              type="email"
              :rules="[rules.required, rules.email]"
              variant="outlined"
              class="mb-3"
            ></v-text-field>

            <v-text-field
              v-if="!editMode"
              v-model="currentUser.password"
              label="Password"
              type="password"
              :rules="[rules.required, rules.minLength]"
              variant="outlined"
              class="mb-3"
            ></v-text-field>

            <v-select
              v-model="currentUser.role"
              label="Role"
              :items="allRoles"
              :rules="[rules.required]"
              variant="outlined"
              class="mb-3"
            ></v-select>

            <v-select
              v-if="currentUser.role === 'TECHNICIAN'"
              v-model="currentUser.technicianType"
              label="Technician Type"
              :items="technicianTypeOptions"
              :rules="[rules.required]"
              variant="outlined"
              class="mb-3"
            ></v-select>

            <v-select
              v-if="shouldShowDepartment"
              v-model="currentUser.department"
              label="Department"
              :items="departments"
              :rules="shouldShowDepartment ? [rules.required] : []"
              variant="outlined"
              class="mb-3"
            ></v-select>

            <v-switch
              v-if="editMode"
              v-model="currentUser.active"
              label="Active"
              color="primary"
            ></v-switch>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="userDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="saveUser" :loading="saving">
            {{ editMode ? 'Update' : 'Create' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="deleteDialog" :max-width="mobile ? undefined : 400" :fullscreen="mobile">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          Confirm Deactivation
          <v-btn v-if="mobile" icon variant="text" @click="deleteDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          Are you sure you want to set user <strong>{{ userToDelete?.fullName }}</strong> to <strong>Inactive</strong>?
          This status can be changed back later if needed.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="deleteDialog = false">Cancel</v-btn>
          <v-btn color="error" @click="deleteUser" :loading="deleting">
            Confirm
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Deactivation Error Dialog -->
    <v-dialog v-model="deactivateErrorDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h5 bg-error text-white">
          <v-icon class="mr-2">mdi-alert-circle</v-icon>
          User Deactivation Blocked
        </v-card-title>
        <v-card-text class="pt-4">
          <p class="mb-4">
            This user cannot be deactivated because they have <strong>active complaints</strong>
            (Open, In-Progress, or Resolved).
          </p>
          <v-alert type="warning" variant="tonal" class="mb-4">
            All complaints associated with a user must be <strong>CLOSED</strong> before the user can be set to Inactive.
          </v-alert>
          <div v-if="deactivateErrorDetails" class="text-caption text-grey-darken-1 pa-2 bg-grey-lighten-4 rounded">
            <strong>Technical Details:</strong><br>
            {{ deactivateErrorDetails }}
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="deactivateErrorDialog = false">Understand</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar for notifications -->
    <v-snackbar v-model="snackbar" :color="snackbarColor" :timeout="3000">
      {{ snackbarMessage }}
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useDisplay } from 'vuetify'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'

const { mobile } = useDisplay()
const router = useRouter()
const authStore = useAuthStore()

const users = ref([])
const filteredUsers = ref([])
const loading = ref(false)
const saving = ref(false)
const userDialog = ref(false)
const deleteDialog = ref(false)
const userToDelete = ref(null)
const deleting = ref(false)
const deactivateErrorDialog = ref(false)
const deactivateErrorDetails = ref('')
const editMode = ref(false)

const searchQuery = ref('')
const roleFilter = ref(null)
const departmentFilter = ref(null)

const currentUser = ref({
  username: '',
  fullName: '',
  email: '',
  password: '',
  role: 'TECHNICIAN',
  department: '',
  technicianType: '',
  active: true
})

const userForm = ref(null)

const snackbar = ref(false)
const snackbarMessage = ref('')
const snackbarColor = ref('success')

const isAdmin = computed(() => authStore.user?.role === 'ADMIN')
const isBaseAdmin = computed(() => authStore.user?.username === 'admin')

const allRoles = computed(() => {
  if (!isAdmin.value) return []
  if (isBaseAdmin.value) {
    return ['ADMIN', 'TECHNICIAN']
  }
  return ['TECHNICIAN']
})
const roleOptions = ['All', 'STUDENT', 'FACULTY', 'TECHNICIAN', 'ADMIN']
const departmentOptions = ['All', 'Computer Science', 'Management Science', 'Environment Science', 'Humanities', 'Mathematics', 'Biotechnology', 'Economics']

const departments = [
  'Computer Science',
  'Management Science',
  'Environment Science',
  'Humanities',
  'Mathematics',
  'Biotechnology',
  'Economics'
]

const technicianTypeOptions = [
  'General Technician',
  'Electrician',
  'Plumber',
  'Carpenter',
  'Network Technician',
  'Other'
]

const headers = [
  { title: 'Name', key: 'fullName' },
  { title: 'Email', key: 'email' },
  { title: 'Role', key: 'role' },
  { title: 'Department', key: 'department' },
  { title: 'Status', key: 'active' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const rules = {
  required: value => !!value || 'This field is required',
  email: value => /.+@.+\..+/.test(value) || 'Invalid email address',
  minLength: value => value.length >= 6 || 'Password must be at least 6 characters'
}

const shouldShowDepartment = computed(() => {
  return currentUser.value.role !== 'TECHNICIAN'
})

onMounted(async () => {
  await loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const response = await api.get('/users')
    users.value = response.data
    filterUsers()
  } catch (error) {
    console.error('Error loading users:', error)
    showSnackbar('Failed to load users', 'error')
  } finally {
    loading.value = false
  }
}

const filterUsers = () => {
  let result = [...users.value]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(user =>
      user.fullName.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query) ||
      user.username.toLowerCase().includes(query)
    )
  }

  if (roleFilter.value && roleFilter.value !== 'All') {
    result = result.filter(user => user.role === roleFilter.value)
  }

  if (departmentFilter.value && departmentFilter.value !== 'All') {
    result = result.filter(user => user.department === departmentFilter.value)
  }

  filteredUsers.value = result
}

const openAddDialog = () => {
  editMode.value = false
  currentUser.value = {
    username: '',
    fullName: '',
    email: '',
    password: '',
    role: allRoles.value[0] || 'TECHNICIAN',
    department: '',
    technicianType: '',
    active: true
  }
  userDialog.value = true
}

const openEditDialog = (user) => {
  editMode.value = true
  currentUser.value = { ...user }
  userDialog.value = true
}

const saveUser = async () => {
  const { valid } = await userForm.value.validate()
  if (!valid) return

  saving.value = true
  try {
    if (editMode.value) {
      await api.put(`/users/${currentUser.value.id}`, currentUser.value)
      showSnackbar('User updated successfully', 'success')
    } else {
      await api.post('/users', currentUser.value)
      showSnackbar('User created successfully', 'success')
    }
    userDialog.value = false
    await loadUsers()
  } catch (error) {
    console.error('Error saving user:', error)
    showSnackbar(error.response?.data || 'Failed to save user', 'error')
  } finally {
    saving.value = false
  }
}

const confirmDelete = (user) => {
  userToDelete.value = user
  deleteDialog.value = true
}

const deleteUser = async () => {
  deleting.value = true
  const isSelfDelete = userToDelete.value.id === authStore.user?.id

  try {
    await api.delete(`/users/${userToDelete.value.id}`)
    showSnackbar('User deleted successfully', 'success')
    deleteDialog.value = false

    if (isSelfDelete) {
      setTimeout(() => {
        authStore.logout()
        router.push('/login')
      }, 1500)
    } else {
      await loadUsers()
    }
  } catch (error) {
    console.error('Error deleting user:', error)
    const errorMsg = error.response?.data?.error || error.response?.data || 'Failed to deactivate user'
    
    if (typeof errorMsg === 'string' && errorMsg.includes('Cannot deactivate user')) {
      deactivateErrorDetails.value = errorMsg
      deactivateErrorDialog.value = true
      deleteDialog.value = false
    } else {
      showSnackbar(errorMsg, 'error')
    }
  } finally {
    deleting.value = false
  }
}

const showSnackbar = (message, color = 'success') => {
  snackbarMessage.value = message
  snackbarColor.value = color
  snackbar.value = true
}

const getRoleColor = (role) => {
  const colors = {
    ADMIN: 'red',
    TECHNICIAN: 'blue',
    FACULTY: 'orange',
    STUDENT: 'cyan'
  }
  return colors[role] || 'grey'
}
</script>
