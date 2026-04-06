<template>
  <div>
    <v-row class="mb-4">
      <v-col cols="12">
        <h2 :class="mobile ? 'text-h5' : 'text-h4'" class="font-weight-bold">
          <v-icon size="36" class="mr-2">mdi-view-dashboard</v-icon>
          Dashboard
        </h2>
        <p class="text-subtitle-1 text-grey">Welcome back, {{ authStore.user?.fullName }}!</p>
      </v-col>
    </v-row>

    <!-- Action Button -->
    <v-row class="mb-4">
      <v-col>
        <v-btn
          color="primary"
          :size="mobile ? 'default' : 'large'"
          :block="mobile"
          prepend-icon="mdi-plus"
          @click="newComplaintDialog = true"
        >
          Submit New Complaint
        </v-btn>
      </v-col>
    </v-row>

    <!-- Recent Complaints -->
    <v-card elevation="2">
      <v-card-title class="d-flex flex-wrap justify-space-between align-center ga-2">
        <span>Recent Complaints</span>
        <v-btn variant="text" color="primary" size="small" @click="$router.push('/student/my-complaints')">
          View All My Complaints
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-progress-linear v-if="complaintStore.loading" indeterminate></v-progress-linear>

        <!-- Desktop: Data Table -->
        <v-data-table
          v-else-if="!mobile"
          :headers="headers"
          :items="complaintStore.complaints"
          :items-per-page="5"
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
            <v-btn size="small" color="primary" @click="viewDetails(item)">
              View Details
            </v-btn>
          </template>
        </v-data-table>

        <!-- Mobile: Card List -->
        <div v-else>
          <div v-if="complaintStore.complaints.length === 0" class="text-center py-8 text-grey">
            <v-icon size="48">mdi-file-document-outline</v-icon>
            <p class="mt-2">No complaints found</p>
          </div>
          <v-card
            v-for="item in complaintStore.complaints.slice(0, 5)"
            :key="item.id"
            variant="outlined"
            class="mb-3 pa-3"
            @click="viewDetails(item)"
          >
            <div class="d-flex justify-space-between align-start mb-2">
              <div class="font-weight-bold text-body-1 flex-grow-1 mr-2">{{ item.title }}</div>
              <v-chip :color="getStatusColor(item.status)" size="x-small">{{ item.status }}</v-chip>
            </div>
            <div class="d-flex align-center ga-2 mb-1">
              <v-chip :color="getPriorityColor(item.priority)" size="x-small">{{ item.priority }}</v-chip>
              <span class="text-caption text-grey">{{ item.studentName }}</span>
            </div>
            <div class="text-caption text-grey">{{ formatDate(item.createdAt) }}</div>
          </v-card>
        </div>
      </v-card-text>
    </v-card>

    <NewComplaintModal v-model="newComplaintDialog" />

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
                <div class="text-caption text-grey">Location</div>
                <div class="text-body-1">{{ selectedComplaint.location || 'Not specified' }}</div>
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

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.text }}
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDisplay } from 'vuetify'
import { useAuthStore } from '../stores/auth'
import { useComplaintStore } from '../stores/complaint'
import NewComplaintModal from '../components/NewComplaintModal.vue'

const { mobile } = useDisplay()
const authStore = useAuthStore()
const complaintStore = useComplaintStore()

const tab = ref('recent')
const newComplaintDialog = ref(false)
const detailsDialog = ref(false)
const selectedComplaint = ref(null)

const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
})

const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Title', key: 'title' },
  { title: 'Priority', key: 'priority' },
  { title: 'Status', key: 'status' },
  { title: 'Submitted By', key: 'studentName' },
  { title: 'Created', key: 'createdAt' },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(async () => {
  await Promise.all([
    complaintStore.fetchRecentComplaints(),
    complaintStore.fetchMyComplaints()
  ])
})

const viewDetails = (complaint) => {
  selectedComplaint.value = complaint
  detailsDialog.value = true
}

const showSnackbar = (text, color = 'success') => {
  snackbar.value = {
    show: true,
    text,
    color
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
