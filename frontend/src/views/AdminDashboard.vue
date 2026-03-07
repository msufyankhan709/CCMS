<template>
  <div>
    <!-- Header -->
    <v-row class="mb-6">
      <v-col cols="12">
        <h2 :class="mobile ? 'text-h5' : 'text-h4'" class="font-weight-bold">Dashboard</h2>
        <p class="text-body-1">Hello, {{ authStore.user?.fullName }}!</p>
      </v-col>
    </v-row>

    <!-- Statistics Cards -->
    <v-row class="mb-6">
      <v-col cols="6" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="text-center">
            <h1 :class="mobile ? 'text-h4' : 'text-h3'" class="font-weight-bold">{{ stats.total }}</h1>
            <p class="text-body-2 text-grey">Total Complaints</p>
          </div>
        </v-card>
      </v-col>
      <v-col cols="6" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="text-center">
            <h1 :class="mobile ? 'text-h4' : 'text-h3'" class="font-weight-bold">{{ stats.open }}</h1>
            <p class="text-body-2 text-grey">Open Complaints</p>
          </div>
        </v-card>
      </v-col>
      <v-col cols="6" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="text-center">
            <h1 :class="mobile ? 'text-h4' : 'text-h3'" class="font-weight-bold">{{ stats.inProgress }}</h1>
            <p class="text-body-2 text-grey">In Progress</p>
          </div>
        </v-card>
      </v-col>
      <v-col cols="6" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="text-center">
            <h1 :class="mobile ? 'text-h4' : 'text-h3'" class="font-weight-bold">{{ stats.resolved }}</h1>
            <p class="text-body-2 text-grey">Resolved</p>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Recent Complaints -->
    <v-card elevation="2" class="mb-6">
      <v-card-title class="d-flex flex-wrap justify-space-between align-center ga-2">
        <span>Recent Complaints</span>
        <v-btn
          color="primary"
          variant="text"
          size="small"
          @click="$router.push('/admin/complaints')"
        >
          VIEW ALL COMPLAINTS
        </v-btn>
      </v-card-title>
      <v-card-text>
        <!-- Desktop: Table -->
        <v-table v-if="!mobile">
          <thead>
            <tr>
              <th>Title</th>
              <th>Date</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="complaint in recentComplaints" :key="complaint.id">
              <td>{{ complaint.title }}</td>
              <td>{{ formatDate(complaint.createdAt) }}</td>
              <td>
                <v-chip :color="getStatusColor(complaint.status)" size="small">
                  {{ complaint.status }}
                </v-chip>
              </td>
              <td>
                <v-btn size="small" variant="text" @click="viewDetails(complaint)">VIEW DETAILS</v-btn>
              </td>
            </tr>
            <tr v-if="recentComplaints.length === 0">
              <td colspan="4" class="text-center text-grey pa-4">
                No recent complaints
              </td>
            </tr>
          </tbody>
        </v-table>

        <!-- Mobile: Card List -->
        <div v-else>
          <div v-if="recentComplaints.length === 0" class="text-center py-8 text-grey">
            <v-icon size="48">mdi-file-document-outline</v-icon>
            <p class="mt-2">No recent complaints</p>
          </div>
          <v-card
            v-for="complaint in recentComplaints"
            :key="complaint.id"
            variant="outlined"
            class="mb-3 pa-3"
            @click="viewDetails(complaint)"
          >
            <div class="d-flex justify-space-between align-start mb-1">
              <div class="font-weight-bold text-body-1 flex-grow-1 mr-2">{{ complaint.title }}</div>
              <v-chip :color="getStatusColor(complaint.status)" size="x-small">{{ complaint.status }}</v-chip>
            </div>
            <div class="text-caption text-grey">{{ formatDate(complaint.createdAt) }}</div>
          </v-card>
        </div>
      </v-card-text>
    </v-card>

    <!-- Complaint Status Distribution Chart -->
    <v-card elevation="2">
      <v-card-title>Complaint Status Distribution</v-card-title>
      <v-card-text>
        <div class="d-flex justify-center align-center" :style="{ height: mobile ? '280px' : '400px' }">
          <canvas ref="chartCanvas" :width="mobile ? 280 : 400" :height="mobile ? 280 : 400"></canvas>
        </div>
      </v-card-text>
    </v-card>

    <!-- Complaint Details Dialog -->
    <v-dialog v-model="detailsDialog" :max-width="mobile ? undefined : 700" :fullscreen="mobile" scrollable>
      <v-card>
        <v-card-title class="text-h5 bg-primary d-flex justify-space-between align-center">
          <div>
            <v-icon class="mr-2">mdi-file-document-outline</v-icon>
            Complaint Details
          </div>
          <v-btn v-if="mobile" icon variant="text" color="white" @click="detailsDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text class="pt-4">
          <v-row v-if="selectedComplaint">
            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Complaint ID</div>
                <div class="text-h6">#{{ selectedComplaint.id }}</div>
              </div>
            </v-col>
            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Status</div>
                <v-chip :color="getStatusColor(selectedComplaint.status)" size="small">
                  {{ selectedComplaint.status }}
                </v-chip>
              </div>
            </v-col>

            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Category</div>
                <div class="text-body-1">{{ selectedComplaint.category || 'N/A' }}</div>
              </div>
            </v-col>
            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Priority</div>
                <v-chip :color="getPriorityColor(selectedComplaint.priority)" size="small">
                  {{ selectedComplaint.priority }}
                </v-chip>
              </div>
            </v-col>

            <v-col cols="12">
              <div class="mb-4">
                <div class="text-caption text-grey">Subject</div>
                <div class="text-h6">{{ selectedComplaint.title }}</div>
              </div>
            </v-col>

            <v-col cols="12">
              <div class="mb-4">
                <div class="text-caption text-grey">Description</div>
                <div class="text-body-1">{{ selectedComplaint.description }}</div>
              </div>
            </v-col>

            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Submitted By</div>
                <div class="text-body-1">{{ selectedComplaint.studentName }}</div>
              </div>
            </v-col>

            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Assigned Technician</div>
                <div class="text-body-1">{{ selectedComplaint.technicianName || 'Not assigned' }}</div>
              </div>
            </v-col>

            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Created At</div>
                <div class="text-body-1">{{ formatDate(selectedComplaint.createdAt) }}</div>
              </div>
            </v-col>

            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Last Updated</div>
                <div class="text-body-1">{{ formatDate(selectedComplaint.updatedAt) }}</div>
              </div>
            </v-col>

            <v-col cols="12" v-if="selectedComplaint.attachmentUrl">
              <div class="mb-4">
                <div class="text-caption text-grey">Attachment</div>
                <v-btn size="small" color="primary" :href="selectedComplaint.attachmentUrl" target="_blank">
                  <v-icon start>mdi-download</v-icon>
                  Download Attachment
                </v-btn>
              </div>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="detailsDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useDisplay } from 'vuetify'
import { useAuthStore } from '../stores/auth'
import { useComplaintStore } from '../stores/complaint'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

const { mobile } = useDisplay()
const authStore = useAuthStore()
const complaintStore = useComplaintStore()

const chartCanvas = ref(null)
let chartInstance = null

const stats = ref({
  total: 0,
  open: 0,
  inProgress: 0,
  resolved: 0,
  closed: 0
})

const recentComplaints = ref([])
const detailsDialog = ref(false)
const selectedComplaint = ref(null)

onMounted(async () => {
  await loadData()
  await nextTick()
  createChart()
})

const loadData = async () => {
  try {
    await complaintStore.fetchRecentComplaints()
    const complaints = complaintStore.complaints

    stats.value.total = complaints.length
    stats.value.open = complaints.filter(c => c.status === 'OPEN').length
    stats.value.inProgress = complaints.filter(c => c.status === 'IN_PROGRESS').length
    stats.value.resolved = complaints.filter(c => c.status === 'RESOLVED').length
    stats.value.closed = complaints.filter(c => c.status === 'CLOSED').length

    recentComplaints.value = complaints.slice(0, 5)
  } catch (error) {
    console.error('Error loading data:', error)
  }
}

const createChart = () => {
  if (!chartCanvas.value) return

  if (chartInstance) {
    chartInstance.destroy()
  }

  const ctx = chartCanvas.value.getContext('2d')
  chartInstance = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['Open', 'In Progress', 'Resolved', 'Closed'],
      datasets: [{
        data: [
          stats.value.open,
          stats.value.inProgress,
          stats.value.resolved,
          stats.value.closed
        ],
        backgroundColor: [
          '#2196F3',
          '#FF9800',
          '#4CAF50',
          '#9E9E9E'
        ],
        borderWidth: 2,
        borderColor: '#fff'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: mobile.value ? 'bottom' : 'right',
          labels: {
            padding: mobile.value ? 12 : 20,
            font: {
              size: mobile.value ? 12 : 14
            }
          }
        }
      }
    }
  })
}

const viewDetails = (complaint) => {
  selectedComplaint.value = complaint
  detailsDialog.value = true
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

const getPriorityColor = (priority) => {
  const colors = {
    LOW: 'blue',
    MEDIUM: 'orange',
    HIGH: 'deep-orange',
    CRITICAL: 'red'
  }
  return colors[priority] || 'grey'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>
