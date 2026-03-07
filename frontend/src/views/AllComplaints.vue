<template>
  <div>
    <v-row class="mb-4">
      <v-col cols="12">
        <h2 :class="mobile ? 'text-h5' : 'text-h4'" class="font-weight-bold">All Complaints</h2>
      </v-col>
    </v-row>

    <v-card elevation="2">
      <v-card-title class="d-flex flex-wrap align-center ga-2">
        <v-icon class="mr-2">mdi-format-list-bulleted</v-icon>
        Complaint Management
        <v-spacer></v-spacer>
        <v-btn @click="loadComplaints" icon size="small">
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-progress-linear v-if="complaintStore.loading" indeterminate></v-progress-linear>

        <!-- Desktop: Data Table -->
        <v-data-table
          v-else-if="!mobile"
          :headers="headers"
          :items="complaintStore.complaints"
          :items-per-page="15"
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

          <template v-slot:item.technician="{ item }">
            <span v-if="item.technicianName">{{ item.technicianName }}</span>
            <v-chip v-else color="grey" size="small">Unassigned</v-chip>
          </template>

          <template v-slot:item.actions="{ item }">
            <v-btn color="info" size="small" @click="viewDetails(item)" class="mr-2">
              View
            </v-btn>
            <v-btn v-if="!item.technicianId" color="primary" size="small" @click="openAssignDialog(item)" class="mr-2">
              Assign
            </v-btn>
            <v-menu>
              <template v-slot:activator="{ props }">
                <v-btn size="small" v-bind="props">Actions</v-btn>
              </template>
              <v-list>
                <v-list-item v-if="item.technicianId" @click="openStatusDialog(item)">
                  <v-list-item-title>Change Status</v-list-item-title>
                </v-list-item>
                <v-list-item @click="confirmDelete(item)">
                  <v-list-item-title class="text-error">Delete Complaint</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </template>
        </v-data-table>

        <!-- Mobile: Card List -->
        <div v-else>
          <div v-if="complaintStore.complaints.length === 0" class="text-center py-8 text-grey">
            <v-icon size="48">mdi-file-document-outline</v-icon>
            <p class="mt-2">No complaints found</p>
          </div>
          <v-card
            v-for="item in complaintStore.complaints"
            :key="item.id"
            variant="outlined"
            class="mb-3 pa-3"
          >
            <div class="d-flex justify-space-between align-start mb-2">
              <div class="font-weight-bold text-body-1 flex-grow-1 mr-2">{{ item.title }}</div>
              <v-chip :color="getStatusColor(item.status)" size="x-small">{{ item.status }}</v-chip>
            </div>
            <div class="d-flex flex-wrap align-center ga-2 mb-2">
              <v-chip :color="getPriorityColor(item.priority)" size="x-small">{{ item.priority }}</v-chip>
              <span class="text-caption text-grey">{{ item.studentName }}</span>
              <v-chip v-if="item.technicianName" size="x-small" color="teal" variant="tonal">{{ item.technicianName }}</v-chip>
              <v-chip v-else size="x-small" color="grey" variant="tonal">Unassigned</v-chip>
            </div>
            <div class="d-flex flex-wrap ga-2 mt-2">
              <v-btn size="x-small" color="info" variant="tonal" @click="viewDetails(item)">View</v-btn>
              <v-btn v-if="!item.technicianId" size="x-small" color="primary" variant="tonal" @click="openAssignDialog(item)">Assign</v-btn>
              <v-btn v-if="item.technicianId" size="x-small" color="warning" variant="tonal" @click="openStatusDialog(item)">Status</v-btn>
              <v-btn size="x-small" color="error" variant="tonal" @click="confirmDelete(item)">Delete</v-btn>
            </div>
          </v-card>
        </div>
      </v-card-text>
    </v-card>

    <!-- Details Dialog with Comments -->
    <v-dialog v-model="detailsDialog" :max-width="mobile ? undefined : 800" :fullscreen="mobile" scrollable>
      <v-card>
        <v-card-title class="text-h5 bg-primary d-flex justify-space-between align-center">
          <div>
            <v-icon class="mr-2">mdi-file-document-outline</v-icon>
            Complaint Details
          </div>
          <v-btn v-if="mobile" icon variant="text" color="white" @click="closeDetailsDialog">
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
                <div class="text-caption text-grey">Priority</div>
                <v-chip :color="getPriorityColor(selectedComplaint.priority)" size="small">
                  {{ selectedComplaint.priority }}
                </v-chip>
              </div>
            </v-col>

            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Location</div>
                <div class="text-body-1">{{ selectedComplaint.location || 'Not specified' }}</div>
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

            <!-- Comments Section - Only when technician is assigned -->
            <v-col cols="12" v-if="selectedComplaint.technicianId || selectedComplaint.technicianName">
              <v-divider class="my-4"></v-divider>
              <div class="d-flex align-center mb-4">
                <v-icon class="mr-2" color="primary">mdi-comment-multiple</v-icon>
                <span class="text-h6">Comments</span>
                <v-chip size="small" class="ml-2" color="primary" variant="tonal">
                  {{ commentStore.comments.length }}
                </v-chip>
              </div>

              <div class="comments-container" style="max-height: 300px; overflow-y: auto;">
                <v-progress-linear v-if="commentStore.loading && commentStore.comments.length === 0" indeterminate color="primary"></v-progress-linear>

                <div v-if="commentStore.comments.length === 0 && !commentStore.loading" class="text-center py-4 text-grey">
                  <v-icon size="48" color="grey-lighten-1">mdi-comment-outline</v-icon>
                  <p class="mt-2">No comments yet. Start the conversation!</p>
                </div>

                <v-card
                  v-for="comment in commentStore.comments"
                  :key="comment.id"
                  variant="tonal"
                  class="mb-2 pa-3"
                  :color="comment.userName === authStore.user?.fullName ? 'primary' : 'grey'"
                  :style="{ opacity: comment.isUserActive === false ? 0.6 : 1 }"
                >
                  <div class="d-flex flex-wrap justify-space-between align-center mb-1 ga-1">
                    <span class="font-weight-bold text-body-2" :class="{ 'text-grey': comment.isUserActive === false }">
                      {{ comment.userName }}
                      <span v-if="comment.isUserActive === false" class="text-caption font-weight-regular">(Inactive)</span>
                    </span>
                    <span class="text-caption text-grey">{{ formatDateTime(comment.timestamp) }}</span>
                  </div>
                  <div class="text-body-2" :class="{ 'text-grey': comment.isUserActive === false }">{{ comment.content }}</div>
                </v-card>
              </div>

              <div class="mt-4">
                <v-textarea
                  v-model="newComment"
                  label="Add a comment (as Admin)..."
                  variant="outlined"
                  rows="2"
                  hide-details
                  :disabled="commentStore.loading"
                ></v-textarea>
                <div class="d-flex justify-end mt-2">
                  <v-btn
                    color="primary"
                    :loading="commentStore.loading"
                    :disabled="!newComment.trim()"
                    @click="submitComment"
                  >
                    <v-icon left>mdi-send</v-icon>
                    Send
                  </v-btn>
                </div>
              </div>
            </v-col>

            <!-- Message when no technician assigned -->
            <v-col cols="12" v-else>
              <v-divider class="my-4"></v-divider>
              <v-alert type="info" variant="tonal">
                <v-icon class="mr-2">mdi-information</v-icon>
                Comments will be available once a technician is assigned to this complaint.
              </v-alert>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="closeDetailsDialog">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Assign Technician Dialog -->
    <v-dialog v-model="assignDialog" :max-width="mobile ? undefined : 500" :fullscreen="mobile">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          Assign Technician
          <v-btn v-if="mobile" icon variant="text" @click="assignDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <p class="mb-4"><strong>Complaint:</strong> {{ selectedComplaint?.title }}</p>

          <v-select
            v-model="selectedTechnicianType"
            label="Technician Type"
            :items="technicianTypeOptions"
            variant="outlined"
            class="mb-4"
          ></v-select>

          <v-select
            v-model="selectedTechnicianId"
            label="Select Technician"
            :items="filteredTechnicians"
            item-title="fullName"
            item-value="id"
            variant="outlined"
          ></v-select>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="assignDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="assignTechnician" :loading="complaintStore.loading">
            Assign
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Status Change Dialog -->
    <v-dialog v-model="statusDialog" :max-width="mobile ? undefined : 500" :fullscreen="mobile">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          Change Complaint Status
          <v-btn v-if="mobile" icon variant="text" @click="statusDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <p class="mb-4"><strong>Complaint:</strong> {{ selectedComplaint?.title }}</p>

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

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="deleteDialog" :max-width="mobile ? undefined : 500" :fullscreen="mobile">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          Confirm Delete
          <v-btn v-if="mobile" icon variant="text" @click="deleteDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <p>Are you sure you want to delete this complaint?</p>
          <p class="mt-2"><strong>Title:</strong> {{ selectedComplaint?.title }}</p>
          <p><strong>Student:</strong> {{ selectedComplaint?.studentName }}</p>
          <v-alert type="warning" variant="tonal" class="mt-4">
            This action cannot be undone.
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="deleteDialog = false">Cancel</v-btn>
          <v-btn color="error" @click="deleteComplaint" :loading="deleting">
            Delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDisplay } from 'vuetify'
import { useComplaintStore } from '../stores/complaint'
import { useCommentStore } from '../stores/comment'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'

const { mobile } = useDisplay()
const complaintStore = useComplaintStore()
const commentStore = useCommentStore()
const authStore = useAuthStore()

const assignDialog = ref(false)
const statusDialog = ref(false)
const deleteDialog = ref(false)
const detailsDialog = ref(false)
const selectedComplaint = ref(null)
const selectedTechnicianId = ref(null)
const selectedStatus = ref(null)
const technicians = ref([])
const selectedTechnicianType = ref('All')
const deleting = ref(false)
const newComment = ref('')

const statusOptions = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED']

const technicianTypeOptions = [
  'All',
  'General Technician',
  'Electrician',
  'Plumber',
  'Carpenter',
  'Network Technician',
  'Other'
]


const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Title', key: 'title' },
  { title: 'Priority', key: 'priority', sortable: true },
  { title: 'Status', key: 'status', sortable: true },
  { title: 'Student', key: 'studentName' },
  { title: 'Technician', key: 'technician' },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(async () => {
  await loadComplaints()
  await loadTechnicians()
})

const loadComplaints = async () => {
  await complaintStore.fetchRecentComplaints()
}

const loadTechnicians = async () => {
  try {
    const response = await api.get('/users/role/TECHNICIAN')
    technicians.value = response.data
  } catch (error) {
    console.error('Error loading technicians:', error)
  }
}

const viewDetails = async (complaint) => {
  selectedComplaint.value = complaint
  detailsDialog.value = true
  newComment.value = ''
  if (complaint.technicianId || complaint.technicianName) {
    try {
      await commentStore.fetchComments(complaint.id)
    } catch (error) {
      console.error('Error loading comments:', error)
    }
  }
}

const closeDetailsDialog = () => {
  detailsDialog.value = false
  commentStore.clearComments()
}

const submitComment = async () => {
  if (!newComment.value.trim() || !selectedComplaint.value) return

  try {
    await commentStore.addComment(selectedComplaint.value.id, newComment.value.trim())
    newComment.value = ''
    await commentStore.fetchComments(selectedComplaint.value.id)
  } catch (error) {
    console.error('Error adding comment:', error)
  }
}

const openAssignDialog = (complaint) => {
  selectedComplaint.value = complaint
  selectedTechnicianId.value = null
  selectedTechnicianType.value = 'All'
  assignDialog.value = true
}


const filteredTechnicians = computed(() => {
  if (!selectedTechnicianType.value || selectedTechnicianType.value === 'All') {
    return technicians.value
  }
  return technicians.value.filter(t => t.technicianType === selectedTechnicianType.value)
})

const openStatusDialog = (complaint) => {
  selectedComplaint.value = complaint
  selectedStatus.value = complaint.status
  statusDialog.value = true
}

const confirmDelete = (complaint) => {
  selectedComplaint.value = complaint
  deleteDialog.value = true
}

const assignTechnician = async () => {
  if (!selectedTechnicianId.value) return

  try {
    await complaintStore.assignTechnician(selectedComplaint.value.id, selectedTechnicianId.value)
    assignDialog.value = false
    await loadComplaints()
  } catch (error) {
    console.error('Error assigning technician:', error)
  }
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

const deleteComplaint = async () => {
  deleting.value = true
  try {
    await api.delete(`/complaints/${selectedComplaint.value.id}`)
    deleteDialog.value = false
    await loadComplaints()
  } catch (error) {
    console.error('Error deleting complaint:', error)
  } finally {
    deleting.value = false
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

const formatDateTime = (dateString) => {
  return new Date(dateString).toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
