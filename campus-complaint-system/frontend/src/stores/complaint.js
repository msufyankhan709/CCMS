import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../services/api'

export const useComplaintStore = defineStore('complaint', () => {
    const complaints = ref([])
    const myComplaints = ref([])
    const technicianComplaints = ref([])
    const currentComplaint = ref(null)
    const loading = ref(false)
    const error = ref(null)

    async function fetchRecentComplaints() {
        try {
            loading.value = true
            const response = await api.get('/complaints/recent')
            complaints.value = response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function fetchMyComplaints() {
        try {
            loading.value = true
            const response = await api.get('/complaints/my')
            myComplaints.value = response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function fetchTechnicianComplaints() {
        try {
            loading.value = true
            const response = await api.get('/complaints/technician')
            technicianComplaints.value = response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function createComplaint(complaintData) {
        loading.value = true
        try {
            const formData = new FormData()
            formData.append('title', complaintData.title)
            formData.append('category', complaintData.category || '')
            formData.append('location', complaintData.location || '')
            formData.append('description', complaintData.description)
            formData.append('priority', complaintData.priority)
            formData.append('visibility', complaintData.visibility || 'PUBLIC')

            // Handle file attachment - Vuetify v-file-input returns array
            console.log('Attachment data:', complaintData.attachment)
            if (complaintData.attachment) {
                // Check if it's an array with files
                if (Array.isArray(complaintData.attachment) && complaintData.attachment.length > 0) {
                    console.log('Appending file:', complaintData.attachment[0])
                    formData.append('attachment', complaintData.attachment[0])
                } else if (complaintData.attachment instanceof File) {
                    // If it's directly a File object
                    console.log('Appending file directly:', complaintData.attachment)
                    formData.append('attachment', complaintData.attachment)
                }
            }

            // Log FormData contents
            for (let pair of formData.entries()) {
                console.log(pair[0] + ': ', pair[1])
            }

            const response = await api.post('/complaints', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
            return response.data
        } catch (err) {
            error.value = err.message
            console.error('Error creating complaint:', err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function assignTechnician(complaintId, technicianId) {
        try {
            loading.value = true
            const response = await api.put(`/complaints/${complaintId}/assign`, { technicianId })
            return response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function updateStatus(complaintId, status) {
        try {
            loading.value = true
            const response = await api.put(`/complaints/${complaintId}/status`, { status })
            return response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function getComplaintById(id) {
        try {
            loading.value = true
            const response = await api.get(`/complaints/${id}`)
            currentComplaint.value = response.data
            return response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function deleteMyComplaint(complaintId) {
        try {
            loading.value = true
            error.value = null
            await api.delete(`/complaints/${complaintId}`)
        } catch (err) {
            error.value = err.response?.data?.error || err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    return {
        complaints,
        myComplaints,
        technicianComplaints,
        currentComplaint,
        loading,
        error,
        fetchRecentComplaints,
        fetchMyComplaints,
        fetchTechnicianComplaints,
        createComplaint,
        assignTechnician,
        updateStatus,
        getComplaintById,
        deleteMyComplaint
    }
})
