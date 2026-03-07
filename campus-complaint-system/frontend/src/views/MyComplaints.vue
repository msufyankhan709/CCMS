<template>
  <div>
    <v-row class="mb-4">
      <v-col cols="12">
        <h2 :class="mobile ? 'text-h5' : 'text-h4'" class="font-weight-bold">My Complaints</h2>
        <p class="text-body-1 text-grey">View and track all your submitted complaints</p>
      </v-col>
    </v-row>

    <v-card elevation="2">
      <v-card-title class="d-flex flex-wrap justify-space-between align-center ga-2">
        <span>All My Complaints</span>
        <v-btn color="primary" :size="mobile ? 'small' : 'default'" @click="$router.push('/student/dashboard')">
          <v-icon start>mdi-plus</v-icon>
          Submit New Complaint
        </v-btn>
      </v-card-title>

      <v-card-text>
        <!-- Desktop: Data Table -->
        <v-data-table
          v-if="!mobile"
          :headers="headers"
          :items="complaints"
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
            <v-btn size="small" color="primary" @click="viewDetails(item)">
              View Details
            </v-btn>
            <v-btn
              size="small"
              color="error"
              variant="tonal"
              class="ml-2"
              @click="handleDelete(item)"
              :disabled="!!item.technicianId || !!item.technicianName"
            >
              Delete
            </v-btn>
          </template>
        </v-data-table>

        <!-- Mobile: Card List -->
        <div v-else>
          <v-progress-linear v-if="loading" indeterminate></v-progress-linear>
          <div v-else-if="complaints.length === 0" class="text-center py-8 text-grey">
            <v-icon size="48">mdi-file-document-outline</v-icon>
            <p class="mt-2">No complaints found</p>
          </div>
          <v-card
            v-for="item in complaints"
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
              <span class="text-caption text-grey">{{ item.category }}</span>
            </div>
            <div class="text-caption text-grey">{{ formatDate(item.createdAt) }}</div>
            <div class="d-flex ga-2 mt-2">
              <v-btn
                size="x-small"
                color="error"
                variant="tonal"
                @click.stop="handleDelete(item)"
                :disabled="!!item.technicianId || !!item.technicianName"
              >
                <v-icon start size="14">mdi-delete</v-icon>
                Delete
              </v-btn>
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
                <div class="text-caption text-grey">Assigned Technician</div>
                <div class="text-body-1">{{ selectedComplaint.technicianName || 'Not assigned yet' }}</div>
              </div>
            </v-col>

            <v-col cols="6" md="6">
              <div class="mb-4">
                <div class="text-caption text-grey">Created At</div>
                <div class="text-body-1">{{ formatDate(selectedComplaint.createdAt) }}</div>
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


            <!-- Comments Section - Only shown when technician is assigned -->
            <v-col cols="12" v-if="selectedComplaint.technicianId || selectedComplaint.technicianName">
              <v-divider class="my-4"></v-divider>
              <div class="d-flex align-center mb-4">
                <v-icon class="mr-2" color="primary">mdi-comment-multiple</v-icon>
                <span class="text-h6">Comments</span>
                <v-chip size="small" class="ml-2" color="primary" variant="tonal">
                  {{ commentStore.comments.length }}
                </v-chip>
              </div>

              <!-- Comments List -->
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

              <!-- Add Comment Input -->
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

            <!-- Message when no technician assigned -->
            <v-col cols="12" v-else>
              <v-divider class="my-4"></v-divider>
              <v-alert type="info" variant="tonal">
                <v-icon class="mr-2">mdi-information</v-icon>
                Comments will be available once a technician is assigned to your complaint.
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


    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.text }}
    </v-snackbar>
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
const complaints = ref([])
const detailsDialog = ref(false)
const selectedComplaint = ref(null)
const newComment = ref('')

const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
})

const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Category', key: 'category' },
  { title: 'Title', key: 'title' },
  { title: 'Priority', key: 'priority' },
  { title: 'Status', key: 'status' },
  { title: 'Created', key: 'createdAt' },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(async () => {
  await loadComplaints()
})

const loadComplaints = async () => {
  loading.value = true
  try {
    await complaintStore.fetchMyComplaints()
    complaints.value = complaintStore.myComplaints
  } catch (error) {
    console.error('Error loading complaints:', error)
  } finally {
    loading.value = false
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

const handleDelete = async (complaint) => {
  // If already assigned, guide user to request via comments
  if (complaint.technicianId || complaint.technicianName) {
    showSnackbar('This complaint is already assigned. Please request deletion from admin in the comments.', 'info')
    return
  }

  const confirmed = window.confirm('Are you sure you want to delete this complaint? This action cannot be undone.')
  if (!confirmed) return

  try {
    await complaintStore.deleteMyComplaint(complaint.id)
    await loadComplaints()
    showSnackbar('Complaint deleted successfully', 'success')
  } catch (error) {
    console.error('Error deleting complaint:', error)
    showSnackbar('Failed to delete complaint', 'error')
  }
}

const showSnackbar = (text, color = 'success') => {
  snackbar.value = {
    show: true,
    text,
    color
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
