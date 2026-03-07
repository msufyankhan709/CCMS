import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../services/api'

export const useCommentStore = defineStore('comment', () => {
    const comments = ref([])
    const loading = ref(false)
    const error = ref(null)

    async function fetchComments(complaintId) {
        try {
            loading.value = true
            error.value = null
            const response = await api.get(`/comments/complaint/${complaintId}`)
            comments.value = response.data
            return response.data
        } catch (err) {
            error.value = err.response?.data || 'Failed to load comments'
            throw err
        } finally {
            loading.value = false
        }
    }

    async function addComment(complaintId, content) {
        try {
            loading.value = true
            error.value = null
            const response = await api.post('/comments', {
                complaintId,
                content
            })
            // Add new comment to the list
            comments.value.push(response.data)
            return response.data
        } catch (err) {
            error.value = err.response?.data || 'Failed to add comment'
            throw err
        } finally {
            loading.value = false
        }
    }

    function clearComments() {
        comments.value = []
    }

    return {
        comments,
        loading,
        error,
        fetchComments,
        addComment,
        clearComments
    }
})
