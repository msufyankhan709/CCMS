<template>
  <v-dialog v-model="isOpen" :max-width="mobile ? undefined : 700" :fullscreen="mobile">
    <v-card>
      <v-card-title class="text-h5 d-flex justify-space-between align-center">
        Submit New Complaint
        <v-btn v-if="mobile" icon variant="text" @click="cancelComplaint">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-form ref="complaintForm" @submit.prevent="submitComplaint">
          <v-row>
            <v-col cols="12" md="4">
              <v-select
                v-model="newComplaint.category"
                label="Category"
                :items="categoryOptions"
                :rules="[v => !!v || 'Category is required']"
                variant="outlined"
                prepend-inner-icon="mdi-tag"
              ></v-select>
            </v-col>

            <v-col cols="12" md="4">
              <v-select
                v-model="newComplaint.priority"
                label="Priority"
                :items="priorityOptions"
                :rules="[v => !!v || 'Priority is required']"
                variant="outlined"
                prepend-inner-icon="mdi-alert-circle"
              ></v-select>
            </v-col>

            <v-col cols="12" md="4">
              <v-select
                v-model="newComplaint.visibility"
                label="Visibility"
                :items="visibilityOptions"
                item-title="text"
                item-value="value"
                :rules="[v => !!v || 'Visibility is required']"
                variant="outlined"
                prepend-inner-icon="mdi-eye"
              ></v-select>
            </v-col>
          </v-row>

          <v-text-field
            v-model="newComplaint.title"
            label="Subject"
            :rules="[v => !!v || 'Subject is required']"
            variant="outlined"
            prepend-inner-icon="mdi-text"
            class="mb-3"
          ></v-text-field>

          <v-text-field
            v-model="newComplaint.location"
            label="Location (Optional)"
            variant="outlined"
            prepend-inner-icon="mdi-map-marker"
            placeholder="e.g., CS 2 bathroom"
            class="mb-3"
          ></v-text-field>

          <v-textarea
            v-model="newComplaint.description"
            label="Description"
            :rules="[v => !!v || 'Description is required']"
            variant="outlined"
            prepend-inner-icon="mdi-text-box"
            rows="4"
            class="mb-3"
          ></v-textarea>

          <v-file-input
            v-model="newComplaint.attachment"
            label="Attachments (Optional)"
            variant="outlined"
            prepend-icon="mdi-paperclip"
            accept="image/*,.pdf,.doc,.docx"
            hint="Max file size: 5MB"
            persistent-hint
          ></v-file-input>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="cancelComplaint">Cancel</v-btn>
        <v-btn color="primary" @click="submitComplaint" :loading="complaintStore.loading">
          Submit Complaint
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useDisplay } from 'vuetify'
import { useComplaintStore } from '../stores/complaint'

const props = defineProps({
  modelValue: Boolean
})

const emit = defineEmits(['update:modelValue', 'submitted'])

const isOpen = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const { mobile } = useDisplay()
const complaintStore = useComplaintStore()

const complaintForm = ref(null)

const newComplaint = ref({
  category: '',
  title: '',
  location: '',
  description: '',
  priority: 'MEDIUM',
  visibility: 'PUBLIC',
  attachment: null
})

const categoryOptions = [
  'Security',
  'Maintenance',
  'Facilities',
  'IT Support',
  'Cleanliness',
  'Safety',
  'Other'
]

const priorityOptions = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']

const visibilityOptions = [
  { text: '🌐 Public — Visible to all users', value: 'PUBLIC' },
  { text: '🔒 Private — Visible only to admin', value: 'PRIVATE' }
]

const cancelComplaint = () => {
  isOpen.value = false
  newComplaint.value = {
    category: '',
    title: '',
    location: '',
    description: '',
    priority: 'MEDIUM',
    visibility: 'PUBLIC',
    attachment: null
  }
}

const submitComplaint = async () => {
  const { valid } = await complaintForm.value.validate()
  if (!valid) return

  try {
    await complaintStore.createComplaint(newComplaint.value)
    cancelComplaint()
    
    // Refresh complaints for the global state
    await Promise.all([
      complaintStore.fetchRecentComplaints(),
      complaintStore.fetchMyComplaints()
    ])
    
    emit('submitted') // Notify parent component explicitly
  } catch (error) {
    console.error('Error creating complaint:', error)
  }
}
</script>
