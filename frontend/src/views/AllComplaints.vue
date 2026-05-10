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
    <v-dialog v-model="detailsDialog" :max-width="mobile ? undefined : 850" :fullscreen="mobile" scrollable>
      <v-card class="rounded-xl overflow-hidden elevation-24" style="border: 1px solid #e2e8f0;">
        <!-- Premium Header with Gradient -->
        <div class="px-6 py-5 bg-gradient-header d-flex justify-space-between align-center text-white">
          <div class="d-flex align-center">
            <v-avatar color="rgba(255, 255, 255, 0.2)" class="mr-3" size="40">
              <v-icon size="24" color="white">mdi-file-document-outline</v-icon>
            </v-avatar>
            <div>
              <div class="text-caption font-weight-medium" style="opacity: 0.85; text-transform: uppercase; letter-spacing: 1px;">Admin Complaint Control</div>
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
                  <v-col cols="12" sm="4" class="py-1">
                    <div class="d-flex align-center mb-3">
                      <v-icon size="18" class="mr-2 text-grey-darken-1">mdi-map-marker-outline</v-icon>
                      <span class="text-body-2 text-slate-dark"><strong>Location:</strong> {{ selectedComplaint.location || 'Not specified' }}</span>
                    </div>
                  </v-col>

                  <!-- Submitted By Info -->
                  <v-col cols="12" sm="4" class="py-1">
                    <div class="d-flex align-center mb-3">
                      <v-icon size="18" class="mr-2 text-grey-darken-1">mdi-account-outline</v-icon>
                      <span class="text-body-2 text-slate-dark">
                        <strong>Submitted By:</strong> 
                        <v-chip size="x-small" color="blue" class="ml-1 font-weight-bold">{{ selectedComplaint.studentName }}</v-chip>
                      </span>
                    </div>
                  </v-col>

                  <!-- Assigned Technician Info -->
                  <v-col cols="12" sm="4" class="py-1">
                    <div class="d-flex align-center mb-3">
                      <v-icon size="18" class="mr-2 text-grey-darken-1">mdi-wrench-outline</v-icon>
                      <span class="text-body-2 text-slate-dark">
                        <strong>Technician:</strong> 
                        <v-chip v-if="selectedComplaint.technicianName" size="x-small" color="teal" class="ml-1 font-weight-bold">{{ selectedComplaint.technicianName }}</v-chip>
                        <span v-else class="text-grey ml-1">Not assigned</span>
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
            <v-col cols="12" v-if="selectedComplaint.technicianId || selectedComplaint.technicianName">
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
                    <p class="text-body-2 font-weight-medium">No comments yet. Post a message to discuss with the student and technician!</p>
                  </div>

                  <div 
                    v-for="comment in commentStore.comments" 
                    :key="comment.id"
                    class="d-flex mb-3 align-start"
                    :class="comment.userName === authStore.user?.fullName ? 'justify-end' : 'justify-start'"
                  >
                    <!-- Avatar for incoming comments -->
                    <v-avatar v-if="comment.userName !== authStore.user?.fullName" size="32" color="indigo-darken-1" class="mr-2 mt-1">
                      <span class="text-caption font-weight-bold text-white">{{ comment.userName.charAt(0).toUpperCase() }}</span>
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
                        {{ comment.userName }}
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
                      <span class="text-caption font-weight-bold text-white">{{ comment.userName.charAt(0).toUpperCase() }}</span>
                    </v-avatar>
                  </div>
                </div>

                <!-- Modern Chat Box Input -->
                <div class="d-flex align-end ga-2">
                  <v-textarea
                    v-model="newComment"
                    placeholder="Type your message here (as Admin)..."
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

            <!-- Message when no technician assigned -->
            <v-col cols="12" v-else>
              <v-alert type="info" variant="tonal" class="rounded-lg">
                <v-icon class="mr-2">mdi-information-outline</v-icon>
                <strong>Discussion Panel:</strong> Comments and discussions will become available once an expert technician is assigned to resolve this complaint.
              </v-alert>
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
import { useRoute } from 'vue-router'
import { useComplaintStore } from '../stores/complaint'
import { useCommentStore } from '../stores/comment'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'

const { mobile } = useDisplay()
const route = useRoute()
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
  
  // Auto-open complaint from notification click
  const complaintId = route.query.complaintId
  if (complaintId) {
    const complaint = complaintStore.complaints.find(c => c.id === Number(complaintId))
    if (complaint) {
      viewDetails(complaint)
    }
  }
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

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
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
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%) !important;
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
  border-left: 4px solid #1e3a8a !important;
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
