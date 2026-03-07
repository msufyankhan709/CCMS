<template>
  <div>
    <v-row class="mb-4">
      <v-col cols="12">
        <h2 :class="mobile ? 'text-h5' : 'text-h4'" class="font-weight-bold">My Assignments</h2>
        <p class="text-body-1 text-grey">Complaints assigned to you for resolution</p>
      </v-col>
    </v-row>

    <v-card elevation="2">
      <v-card-title>Assigned Complaints</v-card-title>
      <v-card-text>
        <!-- Desktop: Data Table -->
        <v-data-table
          v-if="!mobile"
          :headers="headers"
          :items="assignments"
          :items-per-page="10"
          class="elevation-1"
          :loading="loading"
        >
          <template v-slot:item.status="{ item }">
            <v-chip :color="getStatusColor(item.status)" size="small">
              {{ item.status }}
            </v-chip>
          </template>

          <template v-slot:item.priority="{ item }">
            <v-chip :color="getPriorityColor(item.priority)" size="small">
              {{ item.priority }}
            </v-chip>
          </template>

          <template v-slot:item.createdAt="{ item }">
            {{ formatDate(item.createdAt) }}
          </template>

          <template v-slot:item.actions="{ item }">
            <v-btn size="small" color="primary" class="mr-2" @click="viewDetails(item)">
              View
            </v-btn>
            <v-btn size="small" color="success" @click="updateStatus(item)">
              Update Status
            </v-btn>
          </template>
        </v-data-table>

        <!-- Mobile: Card List -->
        <div v-else>
          <v-progress-linear v-if="loading" indeterminate></v-progress-linear>
          <div v-else-if="assignments.length === 0" class="text-center py-8 text-grey">
            <v-icon size="48">mdi-clipboard-check-outline</v-icon>
            <p class="mt-2">No assignments found</p>
          </div>
          <v-card
            v-for="item in assignments"
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
              <span class="text-caption text-grey">by {{ item.studentName }}</span>
              <span class="text-caption text-grey">{{ formatDate(item.createdAt) }}</span>
            </div>
            <div class="d-flex ga-2 mt-2">
              <v-btn size="x-small" color="primary" variant="tonal" @click="viewDetails(item)">View</v-btn>
              <v-btn size="x-small" color="success" variant="tonal" @click="updateStatus(item)">Update Status</v-btn>
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

            <!-- Comments Section -->
            <v-col cols="12">
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
                  label="Add a comment..."
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
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="closeDetailsDialog">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Update Status Dialog -->
    <v-dialog v-model="statusDialog" :max-width="mobile ? undefined : 500" :fullscreen="mobile">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          Update Complaint Status
          <v-btn v-if="mobile" icon variant="text" @click="statusDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-select
            v-model="newStatus"
            label="Status"
            :items="statusOptions"
            variant="outlined"
          ></v-select>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="statusDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="saveStatus">Update</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDisplay } from 'vuetify'
import { useComplaintStore } from '../stores/complaint'
import { useCommentStore } from '../stores/comment'
import { useAuthStore } from '../stores/auth'

const { mobile } = useDisplay()
const complaintStore = useComplaintStore()
const commentStore = useCommentStore()
const authStore = useAuthStore()

const loading = ref(false)
const assignments = ref([])
const detailsDialog = ref(false)
const statusDialog = ref(false)
const selectedComplaint = ref(null)
const newStatus = ref('')
const newComment = ref('')

const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Title', key: 'title' },
  { title: 'Priority', key: 'priority' },
  { title: 'Status', key: 'status' },
  { title: 'Student', key: 'studentName' },
  { title: 'Created', key: 'createdAt' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const statusOptions = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED']

onMounted(async () => {
  await loadAssignments()
})

const loadAssignments = async () => {
  loading.value = true
  try {
    await complaintStore.fetchTechnicianComplaints()
    assignments.value = complaintStore.technicianComplaints
  } catch (error) {
    console.error('Error loading assignments:', error)
  } finally {
    loading.value = false
  }
}

const viewDetails = async (complaint) => {
  selectedComplaint.value = complaint
  detailsDialog.value = true
  newComment.value = ''
  try {
    await commentStore.fetchComments(complaint.id)
  } catch (error) {
    console.error('Error loading comments:', error)
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

const updateStatus = (complaint) => {
  selectedComplaint.value = complaint
  newStatus.value = complaint.status
  statusDialog.value = true
}

const saveStatus = async () => {
  try {
    await complaintStore.updateStatus(selectedComplaint.value.id, newStatus.value)
    statusDialog.value = false
    await loadAssignments()
  } catch (error) {
    console.error('Error updating status:', error)
  }
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

const formatDateTime = (dateString) => {
  return new Date(dateString).toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
