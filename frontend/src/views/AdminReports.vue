<template>
  <div class="reports-container">
    <!-- Header -->
    <v-row class="mb-6">
      <v-col cols="12">
        <h2 class="text-h4 font-weight-bold">Reports & Analytics</h2>
        <p class="text-body-1 text-grey">Comprehensive insights into campus complaints data.</p>
      </v-col>
    </v-row>

    <!-- Summary Cards -->
    <v-row class="mb-6">
      <v-col cols="12" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="d-flex align-center mb-2">
            <v-icon color="primary" size="40">mdi-file-document-multiple</v-icon>
            <v-spacer></v-spacer>
          </div>
          <h3 class="text-h4 font-weight-bold">{{ stats.totalComplaints }}</h3>
          <p class="text-body-2 text-grey">Total Complaints</p>
          <p class="text-caption">All complaints submitted to date</p>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="d-flex align-center mb-2">
            <v-icon color="orange" size="40">mdi-alert-circle</v-icon>
            <v-spacer></v-spacer>
          </div>
          <h3 class="text-h4 font-weight-bold">{{ stats.openComplaints }}</h3>
          <p class="text-body-2 text-grey">Open Complaints</p>
          <p class="text-caption">Currently awaiting resolution</p>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="d-flex align-center mb-2">
            <v-icon color="green" size="40">mdi-check-circle</v-icon>
            <v-spacer></v-spacer>
          </div>
          <h3 class="text-h4 font-weight-bold">{{ stats.resolvedThisMonth }}</h3>
          <p class="text-body-2 text-grey">Resolved This Month</p>
          <p class="text-caption">Complaints closed in last 30 days</p>
        </v-card>
      </v-col>

      <v-col cols="12" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="d-flex align-center mb-2">
            <v-icon color="purple" size="40">mdi-clock-outline</v-icon>
            <v-spacer></v-spacer>
          </div>
          <h3 class="text-h4 font-weight-bold">{{ stats.avgResolutionDays }} Days</h3>
          <p class="text-body-2 text-grey">Average Resolution Time</p>
          <p class="text-caption">Avg. time from submission to resolution</p>
        </v-card>
      </v-col>
    </v-row>

    <!-- Charts Row -->
    <v-row class="mb-6">
      <!-- Category Distribution Chart -->
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title>Complaints by Category</v-card-title>
          <v-card-subtitle>Distribution of complaints across different categories.</v-card-subtitle>
          <v-card-text>
            <canvas ref="categoryChart" height="300"></canvas>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Status Trend Chart -->
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title>Complaint Status Trend</v-card-title>
          <v-card-subtitle>Monthly trend of open, resolved, and closed complaints.</v-card-subtitle>
          <v-card-text>
            <canvas ref="trendChart" height="300"></canvas>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Recent Complaints Table -->
    <v-card elevation="2">
      <v-card-title class="d-flex justify-space-between align-center">
        <span>Recent Complaints Overview</span>
        <div>
          <v-btn color="success" class="mr-2" @click="exportCSV">
            <v-icon start>mdi-download</v-icon>
            Download CSV
          </v-btn>
          <v-btn color="error" @click="exportPDF">
            <v-icon start>mdi-file-pdf-box</v-icon>
            Export PDF
          </v-btn>
        </div>
      </v-card-title>
      <v-card-subtitle>A detailed list of recent complaints with their current status and assignment details.</v-card-subtitle>
      <v-card-text>
        <v-data-table
          :headers="tableHeaders"
          :items="complaints"
          :items-per-page="10"
          class="elevation-1"
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
        </v-data-table>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { Chart, registerables } from 'chart.js'
import api from '../services/api'

Chart.register(...registerables)

const stats = ref({
  totalComplaints: 0,
  openComplaints: 0,
  resolvedThisMonth: 0,
  avgResolutionDays: 0
})

const complaints = ref([])
const categoryChart = ref(null)
const trendChart = ref(null)
let categoryChartInstance = null
let trendChartInstance = null

const tableHeaders = [
  { title: 'ID', key: 'id', sortable: true },
  { title: 'Category', key: 'category' },
  { title: 'Title', key: 'title' },
  { title: 'Status', key: 'status' },
  { title: 'Assigned To', key: 'technicianName' },
  { title: 'Date', key: 'createdAt' },
  { title: 'Priority', key: 'priority' }
]

onMounted(async () => {
  await loadData()
  await nextTick()
  createCharts()
})

const loadData = async () => {
  try {
    // Load statistics
    const statsResponse = await api.get('/reports/statistics')
    stats.value = statsResponse.data

    // Load complaints for table
    const complaintsResponse = await api.get('/complaints/recent')
    complaints.value = complaintsResponse.data
  } catch (error) {
    console.error('Error loading reports data:', error)
  }
}

const createCharts = async () => {
  try {
    // Category Distribution Chart
    const categoryResponse = await api.get('/reports/category-distribution')
    const categoryData = categoryResponse.data

    if (categoryChart.value) {
      const ctx = categoryChart.value.getContext('2d')
      categoryChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: Object.keys(categoryData),
          datasets: [{
            label: 'Number of Complaints',
            data: Object.values(categoryData),
            backgroundColor: [
              '#4F46E5',
              '#7C3AED',
              '#2563EB',
              '#0891B2',
              '#059669',
              '#DC2626',
              '#EA580C'
            ],
            borderRadius: 8
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1
              }
            }
          }
        }
      })
    }

    // Status Trend Chart
    const trendResponse = await api.get('/reports/status-trend')
    const trendData = trendResponse.data

    if (trendChart.value && trendData.length > 0) {
      const ctx = trendChart.value.getContext('2d')
      trendChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
          labels: trendData.map(d => d.month),
          datasets: [
            {
              label: 'Open',
              data: trendData.map(d => d.open),
              borderColor: '#3B82F6',
              backgroundColor: 'rgba(59, 130, 246, 0.1)',
              fill: true,
              tension: 0.4
            },
            {
              label: 'Resolved',
              data: trendData.map(d => d.resolved),
              borderColor: '#10B981',
              backgroundColor: 'rgba(16, 185, 129, 0.1)',
              fill: true,
              tension: 0.4
            },
            {
              label: 'Closed',
              data: trendData.map(d => d.closed),
              borderColor: '#6B7280',
              backgroundColor: 'rgba(107, 114, 128, 0.1)',
              fill: true,
              tension: 0.4
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom'
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1
              }
            }
          }
        }
      })
    }
  } catch (error) {
    console.error('Error creating charts:', error)
  }
}

const exportCSV = () => {
  const headers = ['ID', 'Category', 'Title', 'Status', 'Assigned To', 'Date', 'Priority']
  const rows = complaints.value.map(c => [
    c.id,
    c.category || 'N/A',
    c.title,
    c.status,
    c.technicianName || 'Not assigned',
    formatDate(c.createdAt),
    c.priority
  ])

  let csvContent = headers.join(',') + '\n'
  rows.forEach(row => {
    csvContent += row.map(cell => `"${cell}"`).join(',') + '\n'
  })

  const blob = new Blob([csvContent], { type: 'text/csv' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `complaints-report-${new Date().toISOString().split('T')[0]}.csv`
  a.click()
  window.URL.revokeObjectURL(url)
}

const exportPDF = () => {
  // Simple PDF export using print
  window.print()
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

<style scoped>
.reports-container {
  padding: 20px;
}
</style>
