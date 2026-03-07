<template>
  <div>
    <v-row class="mb-4">
      <v-col cols="12">
        <h2 :class="mobile ? 'text-h5' : 'text-h4'" class="font-weight-bold">
          <v-icon size="36" class="mr-2" color="info">mdi-toolbox</v-icon>
          Technician Dashboard
        </h2>
        <p class="text-subtitle-1 text-grey">My assigned complaints</p>
      </v-col>
    </v-row>

    <!-- Assigned Complaints -->
    <v-card elevation="2">
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-clipboard-list</v-icon>
        My Assigned Tasks
        <v-spacer></v-spacer>
        <v-btn @click="loadComplaints" icon size="small">
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-progress-linear v-if="complaintStore.loading" indeterminate></v-progress-linear>

        <div v-else-if="complaintStore.technicianComplaints.length === 0" class="text-center pa-8">
          <v-icon size="64" color="grey">mdi-inbox</v-icon>
          <p class="text-h6 text-grey mt-4">No complaints assigned yet</p>
        </div>

        <!-- Desktop: Data Table -->
        <v-data-table
          v-else-if="!mobile"
          :headers="headers"
          :items="complaintStore.technicianComplaints"
          :items-per-page="10"
          class="elevation-1"
        >
          <template v-slot:item.priority="{ item }">
            <v-chip :color="getPriorityColor(item.priority)" size="small">
              {{ item.priority }}
            </v-chip>
          </template>

          <template v-slot:item.status="{ item }">
            <v-chip :color="getStatusColor(item.status)" size="small">
              {{ item.status }}
            </v-chip>
          </template>

          <template v-slot:item.createdAt="{ item }">
            {{ formatDate(item.createdAt) }}
          </template>

          <template v-slot:item.actions="{ item }">
            <v-btn
              color="primary"
              size="small"
              @click="openStatusDialog(item)"
            >
              Update Status
            </v-btn>
          </template>
        </v-data-table>

        <!-- Mobile: Card List -->
        <div v-else>
          <v-card
            v-for="item in complaintStore.technicianComplaints"
            :key="item.id"
            variant="outlined"
            class="mb-3 pa-3"
          >
            <div class="d-flex justify-space-between align-start mb-2">
              <div class="font-weight-bold text-body-1 flex-grow-1 mr-2">{{ item.title }}</div>
              <v-chip :color="getStatusColor(item.status)" size="x-small">{{ item.status }}</v-chip>
            </div>
            <div class="text-body-2 text-grey mb-2" v-if="item.description">
              {{ item.description.length > 80 ? item.description.substring(0, 80) + '...' : item.description }}
            </div>
            <div class="d-flex flex-wrap align-center ga-2 mb-2">
              <v-chip :color="getPriorityColor(item.priority)" size="x-small">{{ item.priority }}</v-chip>
              <span class="text-caption text-grey">by {{ item.studentName }}</span>
              <span class="text-caption text-grey">{{ formatDate(item.createdAt) }}</span>
            </div>
            <v-btn size="small" color="primary" variant="tonal" block @click="openStatusDialog(item)">
              Update Status
            </v-btn>
          </v-card>
        </div>
      </v-card-text>
    </v-card>

    <!-- Status Update Dialog -->
    <v-dialog v-model="statusDialog" :max-width="mobile ? undefined : 500" :fullscreen="mobile">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          Update Complaint Status
          <v-btn v-if="mobile" icon variant="text" @click="statusDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <p class="mb-2"><strong>Complaint:</strong> {{ selectedComplaint?.title }}</p>
          <p class="mb-4 text-grey">{{ selectedComplaint?.description }}</p>

          <v-select
            v-model="selectedStatus"
            label="New Status"
            :items="statusOptions"
            variant="outlined"
          ></v-select>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="statusDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="updateStatus" :loading="complaintStore.loading">
            Update
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDisplay } from 'vuetify'
import { useComplaintStore } from '../stores/complaint'

const { mobile } = useDisplay()
const complaintStore = useComplaintStore()

const statusDialog = ref(false)
const selectedComplaint = ref(null)
const selectedStatus = ref(null)

const statusOptions = ['IN_PROGRESS', 'RESOLVED']

const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Title', key: 'title' },
  { title: 'Priority', key: 'priority', sortable: true },
  { title: 'Status', key: 'status', sortable: true },
  { title: 'Student', key: 'studentName' },
  { title: 'Created', key: 'createdAt', sortable: true },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(async () => {
  await loadComplaints()
})

const loadComplaints = async () => {
  await complaintStore.fetchTechnicianComplaints()
}

const openStatusDialog = (complaint) => {
  selectedComplaint.value = complaint
  selectedStatus.value = complaint.status
  statusDialog.value = true
}

const updateStatus = async () => {
  if (!selectedStatus.value) return

  try {
    await complaintStore.updateStatus(selectedComplaint.value.id, selectedStatus.value)
    statusDialog.value = false
    await loadComplaints()
  } catch (error) {
    console.error('Error updating status:', error)
  }
}

const getPriorityColor = (priority) => {
  const colors = {
    LOW: 'blue',
    MEDIUM: 'orange',
    HIGH: 'deep-orange',
    CRITICAL: 'red'
  }
  return colors[priority] || 'grey'
}

const getStatusColor = (status) => {
  const colors = {
    OPEN: 'blue',
    IN_PROGRESS: 'orange',
    RESOLVED: 'green',
    CLOSED: 'grey'
  }
  return colors[status] || 'grey'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
