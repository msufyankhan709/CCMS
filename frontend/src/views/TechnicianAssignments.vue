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
    <v-dialog v-model="detailsDialog" :max-width="mobile ? undefined : 850" :fullscreen="mobile" scrollable>
      <v-card class="rounded-xl overflow-hidden elevation-24" style="border: 1px solid #e2e8f0;">
        <!-- Premium Header with Gradient -->
        <div class="px-6 py-5 bg-gradient-header d-flex justify-space-between align-center text-white">
          <div class="d-flex align-center">
            <v-avatar color="rgba(255, 255, 255, 0.2)" class="mr-3" size="40">
              <v-icon size="24" color="white">mdi-file-document-outline</v-icon>
            </v-avatar>
            <div>
              <div class="text-caption font-weight-medium" style="opacity: 0.85; text-transform: uppercase; letter-spacing: 1px;">Assignment Tracking</div>
              <div class="text-h6 font-weight-bold" v-if="selectedComplaint">Complaint #{{ selectedComplaint.id }}</div>
            </div>
          </div>
          <v-btn icon variant="tonal" color="white" size="small" @click="closeDetailsDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </div>

        <v-card-text class="pa-6" style="background-color: #f8fafc;">
          <v-row v-if="selectedComplaint">
            
            <!-- Core Metadata Grid of Cards -->
            <v-col cols="12">
              <v-row dense>
                <!-- Status Card -->
                <v-col cols="6" sm="3">
                  <v-card variant="outlined" class="pa-3 rounded-lg border-light d-flex align-center h-100 bg-white">
                    <v-avatar size="32" color="blue-lighten-5" class="mr-3">
                      <v-icon size="18" color="blue-darken-2">mdi-list-status</v-icon>
                    </v-avatar>
                    <div>
                      <div class="text-caption text-grey-darken-1 font-weight-medium">Status</div>
                      <v-chip :color="getStatusColor(selectedComplaint.status)" size="x-small" class="font-weight-bold mt-1">
                        {{ selectedComplaint.status }}
                      </v-chip>
                    </div>
                  </v-card>
                </v-col>

                <!-- Priority Card -->
                <v-col cols="6" sm="3">
                  <v-card variant="outlined" class="pa-3 rounded-lg border-light d-flex align-center h-100 bg-white">
                    <v-avatar size="32" color="red-lighten-5" class="mr-3">
                      <v-icon size="18" color="red-darken-2">mdi-alert-circle-outline</v-icon>
                    </v-avatar>
                    <div>
                      <div class="text-caption text-grey-darken-1 font-weight-medium">Priority</div>
                      <v-chip :color="getPriorityColor(selectedComplaint.priority)" size="x-small" class="font-weight-bold mt-1">
                        {{ selectedComplaint.priority }}
                      </v-chip>
                    </div>
                  </v-card>
                </v-col>

                <!-- Category Card -->
                <v-col cols="6" sm="3">
                  <v-card variant="outlined" class="pa-3 rounded-lg border-light d-flex align-center h-100 bg-white">
                    <v-avatar size="32" color="teal-lighten-5" class="mr-3">
                      <v-icon size="18" color="teal-darken-2">mdi-tag-outline</v-icon>
                    </v-avatar>
                    <div>
                      <div class="text-caption text-grey-darken-1 font-weight-medium">Category</div>
                      <div class="text-subtitle-2 font-weight-bold text-slate mt-1">{{ selectedComplaint.category || 'N/A' }}</div>
                    </div>
                  </v-card>
                </v-col>

                <!-- Created At Card -->
                <v-col cols="6" sm="3">
                  <v-card variant="outlined" class="pa-3 rounded-lg border-light d-flex align-center h-100 bg-white">
                    <v-avatar size="32" color="indigo-lighten-5" class="mr-3">
                      <v-icon size="18" color="indigo-darken-2">mdi-calendar-range</v-icon>
                    </v-avatar>
                    <div>
                      <div class="text-caption text-grey-darken-1 font-weight-medium">Submitted On</div>
                      <div class="text-subtitle-2 font-weight-bold text-slate mt-1">{{ formatDate(selectedComplaint.createdAt) }}</div>
                    </div>
                  </v-card>
                </v-col>
              </v-row>
            </v-col>

            <!-- Main Details Card -->
            <v-col cols="12">
              <v-card class="pa-5 rounded-lg border-light bg-white" variant="outlined">
                <!-- Subject Header -->
                <div class="text-caption text-indigo font-weight-bold mb-1 uppercase tracking-wider">Subject</div>
                <h3 class="text-h6 font-weight-bold text-indigo-darken-4 mb-4">{{ selectedComplaint.title }}</h3>
                
                <v-row>
                  <!-- Location Info -->
                  <v-col cols="12" sm="6" class="py-1">
                    <div class="d-flex align-center mb-3">
                      <v-icon size="18" class="mr-2 text-grey-darken-1">mdi-map-marker-outline</v-icon>
                      <span class="text-body-2 text-slate-dark"><strong>Location:</strong> {{ selectedComplaint.location || 'Not specified' }}</span>
                    </div>
                  </v-col>

                  <!-- Submitted By Info -->
                  <v-col cols="12" sm="6" class="py-1">
                    <div class="d-flex align-center mb-3">
                      <v-icon size="18" class="mr-2 text-grey-darken-1">mdi-account-outline</v-icon>
                      <span class="text-body-2 text-slate-dark">
                        <strong>Submitted By:</strong> 
                        <v-chip size="x-small" color="blue" class="ml-1 font-weight-bold">{{ selectedComplaint.studentName }}</v-chip>
                      </span>
                    </div>
                  </v-col>
                </v-row>

                <v-divider class="my-3"></v-divider>

                <!-- Description Block -->
                <div class="text-caption text-indigo font-weight-bold mb-1 uppercase tracking-wider">Description</div>
                <div class="description-box pa-4 rounded-lg bg-slate-light border-left-accent">
                  <p class="text-body-1 text-slate-dark leading-relaxed" style="white-space: pre-line;">{{ selectedComplaint.description }}</p>
                </div>
              </v-card>
            </v-col>

            <!-- Attachment Section (Beautiful card with preview link) -->
            <v-col cols="12" v-if="selectedComplaint.attachmentUrl">
              <v-card class="pa-4 rounded-lg bg-white border-light d-flex align-center justify-space-between" variant="outlined">
                <div class="d-flex align-center">
                  <v-avatar size="44" color="blue-lighten-5" class="mr-3 rounded-lg">
                    <v-icon size="24" color="blue-darken-3">mdi-file-image-outline</v-icon>
                  </v-avatar>
                  <div>
                    <div class="text-subtitle-2 font-weight-bold text-slate-dark">Attachment File</div>
                    <div class="text-caption text-grey">Click to view/download complaint attachment</div>
                  </div>
                </div>
                <v-btn 
                  size="small" 
                  color="indigo" 
                  variant="flat" 
                  class="rounded-lg font-weight-bold elevation-1"
                  :href="getAttachmentUrl(selectedComplaint.attachmentUrl)" 
                  target="_blank"
                >
                  <v-icon start size="18">mdi-download-box-outline</v-icon>
                  Download File
                </v-btn>
              </v-card>
            </v-col>

            <!-- Comments Section -->
            <v-col cols="12">
              <v-card class="pa-5 rounded-lg border-light bg-white" variant="outlined">
                <div class="d-flex align-center mb-4">
                  <v-avatar size="32" color="indigo-lighten-5" class="mr-2">
                    <v-icon size="18" color="indigo">mdi-forum-outline</v-icon>
                  </v-avatar>
                  <span class="text-h6 font-weight-bold text-slate-dark">Discussion Panel</span>
                  <v-chip size="x-small" class="ml-2 font-weight-bold" color="indigo" variant="flat">
                    {{ commentStore.comments.length }} comments
                  </v-chip>
                </div>

                <!-- Modern Bubble Style Comments Container -->
                <div class="comments-scroll pa-3 rounded-lg bg-slate-light mb-4" style="max-height: 300px; overflow-y: auto;">
                  <v-progress-linear v-if="commentStore.loading && commentStore.comments.length === 0" indeterminate color="indigo" class="rounded-pill"></v-progress-linear>

                  <div v-if="commentStore.comments.length === 0 && !commentStore.loading" class="text-center py-6 text-grey">
                    <v-icon size="40" color="grey-lighten-1" class="mb-2">mdi-chat-question-outline</v-icon>
                    <p class="text-body-2 font-weight-medium">No comments yet. Post a message to ask the student or admin!</p>
                  </div>

                  <div 
                    v-for="comment in commentStore.comments" 
                    :key="comment.id"
                    class="d-flex mb-3 align-start"
                    :class="comment.userName === authStore.user?.fullName ? 'justify-end' : 'justify-start'"
                  >
                    <!-- Avatar for incoming comments -->
                    <v-avatar v-if="comment.userName !== authStore.user?.fullName" size="32" color="indigo-darken-1" class="mr-2 mt-1">
                      <span class="text-caption font-weight-bold text-white">{{ (comment.userName || 'User').charAt(0).toUpperCase() }}</span>
                    </v-avatar>

                    <!-- Bubble -->
                    <v-card 
                      elevation="0"
                      class="pa-3 rounded-xl max-w-75"
                      :style="comment.userName === authStore.user?.fullName 
                        ? 'background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%); color: white; border-top-right-radius: 4px;' 
                        : 'background-color: #f1f5f9; border-top-left-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.05) !important;'
                      "
                    >
                      <div 
                        class="text-caption font-weight-bold mb-1"
                        :class="comment.userName === authStore.user?.fullName ? 'text-indigo-lighten-5' : 'text-indigo'"
                      >
                        {{ comment.userName || 'Anonymous User' }}
                      </div>
                      <div class="text-body-2 leading-relaxed" style="word-break: break-word;" :style="comment.userName === authStore.user?.fullName ? 'color: #ffffff;' : 'color: #1e293b;'">{{ comment.content }}</div>
                      <div 
                        class="text-right text-caption mt-1 font-weight-medium" 
                        style="font-size: 0.7rem; opacity: 0.75;"
                        :class="comment.userName === authStore.user?.fullName ? 'text-indigo-lighten-5' : 'text-grey'"
                      >
                        {{ formatDateTime(comment.timestamp) }}
                      </div>
                    </v-card>

                    <!-- Avatar for outgoing comments -->
                    <v-avatar v-if="comment.userName === authStore.user?.fullName" size="32" color="blue-darken-3" class="ml-2 mt-1">
                      <span class="text-caption font-weight-bold text-white">{{ (comment.userName || 'User').charAt(0).toUpperCase() }}</span>
                    </v-avatar>
                  </div>
                </div>

                <!-- Modern Chat Box Input -->
                <div class="d-flex align-end ga-2">
                  <v-textarea
                    v-model="newComment"
                    placeholder="Type your message here..."
                    variant="outlined"
                    rows="1"
                    auto-grow
                    hide-details
                    density="comfortable"
                    class="rounded-xl flex-grow-1"
                    bg-color="white"
                    :disabled="commentStore.loading"
                  ></v-textarea>
                  <v-btn
                    color="indigo"
                    size="large"
                    class="rounded-xl px-4"
                    :loading="commentStore.loading"
                    :disabled="!newComment.trim()"
                    @click="submitComment"
                    elevation="2"
                  >
                    <v-icon class="mr-1">mdi-send</v-icon>
                    Send
                  </v-btn>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions class="px-6 py-4 bg-slate-light border-top">
          <v-spacer></v-spacer>
          <v-btn 
            color="indigo-darken-2" 
            variant="flat" 
            class="rounded-lg font-weight-bold px-5" 
            @click="closeDetailsDialog"
          >
            Close Details
          </v-btn>
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
import { useRoute } from 'vue-router'
import { useComplaintStore } from '../stores/complaint'
import { useCommentStore } from '../stores/comment'
import { useAuthStore } from '../stores/auth'

const { mobile } = useDisplay()
const route = useRoute()
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
  
  // Auto-open complaint from notification click
  const complaintId = route.query.complaintId
  if (complaintId) {
    const complaint = assignments.value.find(c => c.id === Number(complaintId))
    if (complaint) {
      viewDetails(complaint)
    }
  }
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

const getAttachmentUrl = (relativeUrl) => {
  if (!relativeUrl) return ''
  if (relativeUrl.startsWith('http://') || relativeUrl.startsWith('https://')) return relativeUrl
  const base = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  const origin = base.endsWith('/api') ? base.substring(0, base.length - 4) : base
  return `${origin}${relativeUrl}`
}
</script>

<style scoped>
.bg-gradient-header {
  background: linear-gradient(135deg, #4f46e5 0%, #2563eb 100%) !important;
}
.border-light {
  border-color: #f1f5f9 !important;
}
.bg-slate-light {
  background-color: #f8fafc !important;
}
.text-slate-dark {
  color: #0f172a !important;
}
.text-slate {
  color: #475569 !important;
}
.border-left-accent {
  border-left: 4px solid #4f46e5 !important;
}
.max-w-75 {
  max-width: 75% !important;
}
.leading-relaxed {
  line-height: 1.625 !important;
}
.uppercase {
  text-transform: uppercase !important;
}
.tracking-wider {
  letter-spacing: 0.05em !important;
}
.comments-scroll::-webkit-scrollbar {
  width: 6px;
}
.comments-scroll::-webkit-scrollbar-track {
  background: transparent;
}
.comments-scroll::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 3px;
}
</style>
