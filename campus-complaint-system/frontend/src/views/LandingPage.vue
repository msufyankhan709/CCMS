<template>
  <div class="landing-app">
    <!-- Sticky Navigation Bar -->
    <header class="nav-bar" :class="{ 'nav-scrolled': scrolled }">
      <div class="nav-container">
        <div class="nav-brand cursor-pointer" @click="scrollToTop">
          <v-icon color="white" size="32" class="mr-2">mdi-shield-check</v-icon>
          <div>
            <span class="nav-brand-title">CUI Complaints</span>
            <span class="nav-brand-subtitle">Management System</span>
          </div>
        </div>
        <!-- Desktop Nav -->
        <nav class="nav-links d-none d-md-flex">
          <a @click="scrollTo('features')" class="nav-link">Features</a>
          <a @click="scrollTo('how-it-works')" class="nav-link">How It Works</a>
          <a @click="scrollTo('testimonials')" class="nav-link">Testimonials</a>
        </nav>
        <div class="nav-actions d-none d-md-flex">
          <button class="btn-login" @click="$router.push('/login')">
            <v-icon size="16" class="mr-1">mdi-account</v-icon>
            Login
          </button>
          <button class="btn-signup" @click="$router.push('/signup')">Sign Up</button>
        </div>
        <!-- Mobile Hamburger -->
        <button class="nav-hamburger d-md-none" @click="mobileMenu = !mobileMenu">
          <v-icon color="white" size="28">{{ mobileMenu ? 'mdi-close' : 'mdi-menu' }}</v-icon>
        </button>
      </div>
    </header>

    <!-- Mobile Drawer -->
    <v-navigation-drawer v-model="mobileMenu" temporary location="right" width="280" class="mobile-drawer">
      <div class="pa-6">
        <div class="d-flex align-center mb-6">
          <v-icon color="primary" size="28" class="mr-2">mdi-shield-check</v-icon>
          <span class="text-subtitle-1 font-weight-bold">CUI Complaints</span>
        </div>
        <div class="d-flex flex-column ga-1 mb-6">
          <a class="mobile-link" @click="scrollTo('features'); mobileMenu = false">Features</a>
          <a class="mobile-link" @click="scrollTo('how-it-works'); mobileMenu = false">How It Works</a>
          <a class="mobile-link" @click="scrollTo('testimonials'); mobileMenu = false">Testimonials</a>
          <a class="mobile-link" href="https://vhr-cms.comsats.edu.pk/" target="_blank">COMSATS Website</a>
        </div>
        <button class="btn-mobile-login" @click="$router.push('/login'); mobileMenu = false">Login</button>
        <button class="btn-mobile-signup" @click="$router.push('/signup'); mobileMenu = false">Sign Up</button>
      </div>
    </v-navigation-drawer>

    <!-- ======== HERO SECTION ======== -->
    <section class="hero-section">
      <div class="hero-container">
        <div class="hero-grid">
          <div class="hero-text">
            <div class="hero-badge">
              <v-icon size="14" class="mr-1" color="white">mdi-rocket-launch</v-icon>
              Streamlined Campus Maintenance
            </div>
            <h1 class="hero-heading">
              Campus Complaint &amp;<br>
              <span class="hero-heading-accent">Maintenance</span> System
            </h1>
            <p class="hero-description">
              Empowering students and administrators at COMSATS University Islamabad, Vehari Campus
              with a modern, efficient platform for seamless complaint resolution and transparent communication.
            </p>

            <!-- Stats Cards -->
            <div class="hero-stats">
              <div class="stat-card" v-for="stat in heroStats" :key="stat.label">
                <v-icon :color="stat.color" size="22" class="mr-3">{{ stat.icon }}</v-icon>
                <div>
                  <div class="stat-value">{{ stat.value }}</div>
                  <div class="stat-label">{{ stat.label }}</div>
                </div>
              </div>
            </div>

            <!-- CTA Buttons -->
            <div class="hero-buttons">
              <button class="btn-primary-hero" @click="$router.push('/signup')">
                Get Started Now
                <v-icon size="18" class="ml-1">mdi-arrow-right</v-icon>
              </button>
              <button class="btn-secondary-hero" @click="openComsats">
                <v-icon size="18" class="mr-1">mdi-play</v-icon>
                Visit COMSATS
              </button>
            </div>

            <!-- Trust Badges -->
            <div class="hero-trust">
              <span class="trust-text">Trusted by COMSATS University nationwide</span>
              <div class="trust-badges">
                <span class="trust-item"><v-icon size="14" class="mr-1">mdi-shield-lock</v-icon>Secure &amp; Private</span>
                <span class="trust-item"><v-icon size="14" class="mr-1">mdi-check-circle</v-icon>24/7 Support</span>
                <span class="trust-item"><v-icon size="14" class="mr-1">mdi-thumb-up</v-icon>Easy to Use</span>
              </div>
            </div>
          </div>

          <!-- Hero Illustration -->
          <div class="hero-illustration d-none d-md-flex">
            <img src="/images/campus-hero.jpg" alt="Campus" class="hero-image" />
          </div>
        </div>

        <!-- Scroll Indicator -->
        <div class="scroll-indicator" @click="scrollTo('features')">
          <v-icon color="white" size="24">mdi-chevron-down</v-icon>
        </div>
      </div>
    </section>

    <!-- ======== FEATURES SECTION ======== -->
    <section id="features" class="section features-section">
      <div class="container">
        <div class="section-header">
          <span class="section-badge">
            <v-icon size="14" class="mr-1">mdi-star-four-points</v-icon>
            Powerful Features
          </span>
          <h2 class="section-title">Everything You Need for <span class="text-accent">Efficient Complaint Management</span></h2>
          <p class="section-subtitle">
            Our comprehensive platform provides all the tools and features needed to
            streamline complaint resolution and improve communication between students and administrators.
          </p>
        </div>

        <div class="features-grid">
          <div class="feature-card" v-for="feature in features" :key="feature.title">
            <div class="feature-icon" :style="{ background: feature.bgColor }">
              <v-icon :color="feature.iconColor" size="24">{{ feature.icon }}</v-icon>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ======== HOW IT WORKS SECTION ======== -->
    <section id="how-it-works" class="section steps-section">
      <div class="container">
        <div class="section-header">
          <span class="section-badge">
            <v-icon size="14" class="mr-1">mdi-clipboard-check</v-icon>
            Simple Process
          </span>
          <h2 class="section-title">How It <span class="text-accent">Works</span></h2>
          <p class="section-subtitle">
            Our streamlined 4-step process makes complaint submission and tracking simple,
            efficient, and transparent for everyone.
          </p>
        </div>

        <div class="steps-grid">
          <div class="step-card" v-for="(step, index) in steps" :key="step.title">
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-icon" :style="{ background: step.bgColor }">
              <v-icon :color="step.iconColor" size="28">{{ step.icon }}</v-icon>
            </div>
            <h3 class="step-title">{{ step.title }}</h3>
            <p class="step-desc">{{ step.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ======== TESTIMONIALS SECTION ======== -->
    <section id="testimonials" class="section testimonials-section">
      <div class="container">
        <div class="section-header">
          <span class="section-badge">
            <v-icon size="14" class="mr-1">mdi-message-text</v-icon>
            Student Stories
          </span>
          <h2 class="section-title">What Students <span class="text-accent">Say</span></h2>
          <p class="section-subtitle">
            Real experiences from students who have used our complaint management system.
          </p>
        </div>

        <!-- Testimonial Carousel -->
        <div class="testimonial-carousel">
          <button class="carousel-arrow carousel-arrow-left" @click="prevTestimonial">
            <v-icon size="24">mdi-chevron-left</v-icon>
          </button>

          <div class="testimonial-card">
            <div class="testimonial-avatar">{{ testimonials[currentTestimonial].avatar }}</div>
            <div class="testimonial-quote">"</div>
            <p class="testimonial-text">{{ testimonials[currentTestimonial].text }}</p>
            <div class="testimonial-author">
              <strong>{{ testimonials[currentTestimonial].name }}</strong>
              <span class="testimonial-dept-badge">{{ testimonials[currentTestimonial].dept }}</span>
            </div>
            <div class="testimonial-role">{{ testimonials[currentTestimonial].role }}</div>
            <div class="testimonial-stars">
              <v-icon v-for="s in 5" :key="s" size="20" color="#FBBF24">mdi-star</v-icon>
            </div>
            <div class="testimonial-verified">Verified Student</div>
          </div>

          <button class="carousel-arrow carousel-arrow-right" @click="nextTestimonial">
            <v-icon size="24">mdi-chevron-right</v-icon>
          </button>
        </div>

        <!-- Carousel Dots -->
        <div class="carousel-dots">
          <span
            v-for="(_, i) in testimonials"
            :key="i"
            class="dot"
            :class="{ active: i === currentTestimonial }"
            @click="currentTestimonial = i"
          ></span>
        </div>
      </div>
    </section>

    <!-- ======== CTA SECTION ======== -->
    <section class="section cta-section">
      <div class="container">
        <div class="cta-inner">
          <span class="cta-badge">
            <v-icon size="14" class="mr-1">mdi-rocket-launch</v-icon>
            Ready to Transform Your Institution?
          </span>
          <h2 class="cta-title">Start Your <span class="cta-title-accent">Digital Transformation</span> Today</h2>
          <p class="cta-subtitle">
            Join thousands of educational institutions that have already revolutionized their
            complaint management process with our modern, efficient platform.
          </p>

          <!-- CTA Trust Cards -->
          <div class="cta-trust-row">
            <div class="cta-trust-card" v-for="item in ctaTrust" :key="item.label">
              <v-icon :color="item.color" size="20" class="mr-2">{{ item.icon }}</v-icon>
              <span>{{ item.label }}</span>
            </div>
          </div>

          <!-- CTA Buttons -->
          <div class="cta-buttons">
            <button class="btn-cta-primary" @click="$router.push('/signup')">
              <v-icon size="18" class="mr-1">mdi-rocket</v-icon>
              Get Started Free
              <v-icon size="18" class="ml-1">mdi-arrow-right</v-icon>
            </button>
            <button class="btn-cta-secondary" @click="$router.push('/login')">
              Sign In
              <v-icon size="18" class="ml-1">mdi-arrow-right</v-icon>
            </button>
          </div>

          <div class="cta-divider"></div>

          <div class="cta-bottom-trust">
            <span class="cta-bottom-text">Trusted by educational institutions nationwide</span>
            <div class="cta-bottom-badges">
              <span class="cta-bottom-item"><span class="green-dot"></span>No Credit Card Required</span>
              <span class="cta-bottom-item"><span class="green-dot"></span>Free 30-Day Trial</span>
              <span class="cta-bottom-item"><span class="gray-dot"></span>Cancel Anytime</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ======== FOOTER ======== -->
    <footer class="footer">
      <div class="container">
        <div class="footer-grid">
          <!-- About -->
          <div class="footer-about">
            <div class="footer-brand">
              <v-icon color="#818CF8" size="28" class="mr-2">mdi-shield-check</v-icon>
              <div>
                <span class="footer-brand-title">CUI Complaints</span>
                <span class="footer-brand-sub">Management System</span>
              </div>
            </div>
            <p class="footer-desc">
              A secure and efficient platform for campus issue resolution and complaint management.
              Empowering students and administrators with streamlined communication and transparent processes.
            </p>
            <div class="footer-contact-info">
              <span><v-icon size="14" class="mr-1">mdi-email</v-icon>complaints@cuivehari.edu.pk</span>
              <span><v-icon size="14" class="mr-1">mdi-phone</v-icon>+92 (67) 3001234</span>
              <span><v-icon size="14" class="mr-1">mdi-map-marker</v-icon>CUI, Vehari Campus</span>
            </div>
          </div>

          <!-- Quick Links -->
          <div class="footer-links-col">
            <h4 class="footer-col-title">Quick Links</h4>
            <ul class="footer-links">
              <li><a @click="scrollToTop">Home</a></li>
              <li><a @click="$router.push('/login')">Login</a></li>
              <li><a @click="$router.push('/signup')">Sign Up</a></li>
              <li><a href="https://vhr-cms.comsats.edu.pk/" target="_blank">About COMSATS</a></li>
            </ul>
          </div>

          <!-- Connect -->
          <div class="footer-connect-col">
            <h4 class="footer-col-title">Connect With Us</h4>
            <p class="footer-connect-text">Follow us on social media for updates and announcements.</p>
            <div class="social-icons">
              <a class="social-icon" href="#"><v-icon size="18">mdi-facebook</v-icon></a>
              <a class="social-icon" href="#"><v-icon size="18">mdi-twitter</v-icon></a>
              <a class="social-icon" href="#"><v-icon size="18">mdi-linkedin</v-icon></a>
              <a class="social-icon" href="#"><v-icon size="18">mdi-github</v-icon></a>
            </div>
          </div>
        </div>

        <div class="footer-divider"></div>
        <div class="footer-bottom">
          <span>© {{ new Date().getFullYear() }} CUI Complaints Management System. All rights reserved.</span>
          <span class="footer-bottom-right">Crafted with ❤️ for students and administrators</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import axios from 'axios'

const mobileMenu = ref(false)
const scrolled = ref(false)
const currentTestimonial = ref(0)

const displayStats = ref({
  resolvedComplaints: 0,
  activeUsers: 0,
  institutions: 0
})

const heroStats = computed(() => [
  { value: displayStats.value.resolvedComplaints.toLocaleString() + '+', label: 'Complaints Resolved', icon: 'mdi-check-circle', color: '#4ADE80' },
  { value: displayStats.value.activeUsers.toLocaleString() + '+', label: 'Active Users', icon: 'mdi-account-group', color: '#60A5FA' },
  { value: displayStats.value.institutions.toLocaleString(), label: 'Educational Institutions', icon: 'mdi-office-building', color: '#C084FC' }
])

const animateValue = (key, target, duration = 1500) => {
  displayStats.value[key] = 0 // Explicitly start from 0
  const start = 0
  const startTime = performance.now()
  
  const update = (now) => {
    const elapsed = now - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    // Ease out expo (fast start, slow finish)
    const easeProgress = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress)
    
    displayStats.value[key] = Math.floor(start + (target - start) * easeProgress)
    
    if (progress < 1) {
      requestAnimationFrame(update)
    } else {
      displayStats.value[key] = target
    }
  }
  
  requestAnimationFrame(update)
}

const fetchStats = async () => {
  try {
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
    const response = await axios.get(`${apiBaseUrl}/stats`)
    const data = response.data
    
    animateValue('resolvedComplaints', data.resolvedComplaints)
    animateValue('activeUsers', data.activeUsers)
    animateValue('institutions', data.institutions)
  } catch (error) {
    console.error('Error fetching stats:', error)
  }
}

const handleScroll = () => { scrolled.value = window.scrollY > 40 }

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  fetchStats()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const scrollToTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })
const scrollTo = (id) => document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
const openComsats = () => window.open('https://vhr-cms.comsats.edu.pk/', '_blank')

const prevTestimonial = () => {
  currentTestimonial.value = currentTestimonial.value === 0
    ? testimonials.length - 1
    : currentTestimonial.value - 1
}
const nextTestimonial = () => {
  currentTestimonial.value = (currentTestimonial.value + 1) % testimonials.length
}

const features = [
  { icon: 'mdi-file-document-edit', iconColor: '#3B82F6', bgColor: '#DBEAFE', title: 'Easy Complaint Submission', description: 'Submit your issues effortlessly with our intuitive and user-friendly platform designed for seamless interaction.' },
  { icon: 'mdi-progress-check', iconColor: '#10B981', bgColor: '#D1FAE5', title: 'Real-Time Tracking', description: 'Stay informed with live updates on the status of your complaints with instant notifications and progress tracking.' },
  { icon: 'mdi-shield-lock', iconColor: '#8B5CF6', bgColor: '#EDE9FE', title: 'Secure & Private', description: 'Your data and complaints are protected with top-tier security and privacy protocols ensuring complete confidentiality.' },
  { icon: 'mdi-account-group', iconColor: '#8B5CF6', bgColor: '#EDE9FE', title: 'Multi-Role Support', description: 'Comprehensive support for students, administrators, and staff with role-based access and tailored interfaces.' },
  { icon: 'mdi-shield-check', iconColor: '#EF4444', bgColor: '#FEE2E2', title: 'Data Protection', description: 'Advanced encryption and compliance with data protection regulations to safeguard all user information.' },
  { icon: 'mdi-bell-ring', iconColor: '#F59E0B', bgColor: '#FEF3C7', title: 'Smart Notifications', description: 'Intelligent notification system that keeps you updated on complaint progress and important announcements.' },
  { icon: 'mdi-chart-arc', iconColor: '#14B8A6', bgColor: '#CCFBF1', title: 'Analytics Dashboard', description: 'Comprehensive analytics and reporting tools to track complaint trends and improve institutional efficiency.' },
  { icon: 'mdi-cellphone', iconColor: '#EC4899', bgColor: '#FCE7F3', title: 'Mobile Responsive', description: 'Fully responsive design that works seamlessly across all devices - desktop, tablet, and mobile phones.' }
]

const steps = [
  { icon: 'mdi-account-plus', iconColor: '#3B82F6', bgColor: '#DBEAFE', title: 'Create an Account', description: 'Sign up with your institutional email and basic information to get started with our secure platform.' },
  { icon: 'mdi-file-document-edit', iconColor: '#10B981', bgColor: '#D1FAE5', title: 'Submit Your Complaint', description: 'Fill out our intuitive complaint form with all relevant details, attachments, and category selection.' },
  { icon: 'mdi-chart-timeline-variant', iconColor: '#8B5CF6', bgColor: '#EDE9FE', title: 'Track Progress', description: 'Monitor real-time updates as administrators work on your case with detailed status tracking.' },
  { icon: 'mdi-check-decagram', iconColor: '#4F46E5', bgColor: '#E0E7FF', title: 'Resolution Complete', description: 'Receive notifications when your complaint is resolved and provide feedback on the solution.' }
]

const testimonials = [
  {
    avatar: '👨‍🎓',
    text: 'I submitted a complaint from my phone while waiting for class, and it actually worked! The interface is clean, and I got a reply from the admin the same day. Super convenient for busy students like me.',
    name: 'Mohammad Faraz',
    dept: 'General',
    role: 'Arts Student',
    stars: 5
  },
  {
    avatar: '👩‍💻',
    text: 'The real-time tracking feature is amazing. I could see exactly when my complaint was being reviewed and who was assigned to it. Very transparent process.',
    name: 'Ayesha Khan',
    dept: 'IT',
    role: 'Computer Science Student',
    stars: 5
  },
  {
    avatar: '👨‍🔬',
    text: 'As a lab user, I needed quick maintenance support. The complaint system made it so easy to report issues and get them resolved within 24 hours. Highly recommended!',
    name: 'Ahmed Ali',
    dept: 'Technical',
    role: 'Biotechnology Student',
    stars: 5
  }
]

const ctaTrust = [
  { icon: 'mdi-shield-lock', color: '#60A5FA', label: 'Secure & Private' },
  { icon: 'mdi-account-group', color: '#FBBF24', label: 'Easy to Use' },
  { icon: 'mdi-check-circle', color: '#4ADE80', label: '24/7 Support' }
]
</script>

<style scoped>
/* ============ RESETS & GLOBALS ============ */
.landing-app { overflow-x: hidden !important; }
.landing-app :deep(.v-main) { overflow-x: hidden; padding: 0 !important; }
* { box-sizing: border-box; }
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}
.cursor-pointer { cursor: pointer; }

/* ============ NAVIGATION ============ */
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: transparent;
  transition: all 0.3s ease;
  padding: 0 24px;
}
.nav-scrolled {
  background: rgba(49, 46, 129, 0.95);
  backdrop-filter: blur(12px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}
.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}
.nav-brand {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.nav-brand-title {
  display: block;
  font-weight: 700;
  font-size: 1rem;
  color: white;
  line-height: 1.2;
}
.nav-brand-subtitle {
  display: block;
  font-size: 0.7rem;
  color: rgba(255,255,255,0.6);
  line-height: 1;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nav-link {
  color: rgba(255,255,255,0.8);
  text-decoration: none;
  padding: 8px 16px;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.2s;
  cursor: pointer;
}
.nav-link:hover { color: white; background: rgba(255,255,255,0.1); }
.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.btn-login {
  display: flex;
  align-items: center;
  padding: 8px 20px;
  border-radius: 10px;
  border: 1.5px solid rgba(255,255,255,0.5);
  background: transparent;
  color: white;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-login:hover { background: rgba(255,255,255,0.1); border-color: white; }
.btn-signup {
  padding: 8px 20px;
  border-radius: 10px;
  border: 1.5px solid rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.1);
  color: white;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-signup:hover { background: rgba(255,255,255,0.2); }
.nav-hamburger {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
}

/* Mobile Drawer */
.mobile-link {
  display: block;
  padding: 12px 16px;
  color: #374151;
  text-decoration: none;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}
.mobile-link:hover { background: #F3F4F6; }
.btn-mobile-login {
  width: 100%;
  padding: 12px;
  border: 2px solid #4F46E5;
  background: transparent;
  color: #4F46E5;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 12px;
}
.btn-mobile-signup {
  width: 100%;
  padding: 12px;
  border: none;
  background: #4F46E5;
  color: white;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
}

/* ============ HERO SECTION ============ */
.hero-section {
  background: linear-gradient(to right bottom, #312E81, #581C87, #3730A3);
  min-height: 100vh;
  padding-top: 64px;
  position: relative;
  overflow: hidden;
}
.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -25%;
  width: 80%;
  height: 200%;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.15) 0%, transparent 60%);
  pointer-events: none;
}
.hero-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
  z-index: 1;
}
.hero-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-items: center;
  min-height: calc(100vh - 64px);
  gap: 48px;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.15);
  padding: 6px 16px;
  border-radius: 999px;
  color: rgba(255,255,255,0.9);
  font-size: 0.8rem;
  font-weight: 500;
  margin-bottom: 32px;
}
.hero-heading {
  font-size: 3.2rem;
  font-weight: 800;
  line-height: 1.1;
  color: white;
  margin-bottom: 24px;
}
.hero-heading-accent {
  background: linear-gradient(135deg, #818CF8, #C084FC);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.hero-description {
  font-size: 1.05rem;
  line-height: 1.7;
  color: rgba(255,255,255,0.75);
  margin-bottom: 32px;
  max-width: 520px;
}
.hero-stats {
  display: flex;
  flex-direction: row;
  gap: 12px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}
.stat-card {
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.12);
  flex: 1;
  min-width: 160px;
  border-radius: 14px;
  padding: 14px 20px;
}
.stat-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: white;
  line-height: 1.2;
}
.stat-label {
  font-size: 0.8rem;
  color: rgba(255,255,255,0.6);
}
.hero-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}
.btn-primary-hero {
  display: inline-flex;
  align-items: center;
  padding: 14px 32px;
  background: linear-gradient(135deg, #7C3AED, #6D28D9);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}
.btn-primary-hero:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(124, 58, 237, 0.5); }
.btn-secondary-hero {
  display: inline-flex;
  align-items: center;
  padding: 14px 28px;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.25);
  color: white;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-secondary-hero:hover { background: rgba(255,255,255,0.2); }
.hero-trust { margin-top: 8px; }
.trust-text {
  display: block;
  color: rgba(255,255,255,0.45);
  font-size: 0.8rem;
  margin-bottom: 8px;
}
.trust-badges { display: flex; gap: 16px; flex-wrap: wrap; }
.trust-item {
  display: inline-flex;
  align-items: center;
  color: rgba(255,255,255,0.5);
  font-size: 0.78rem;
}
/* Hero Image / Illustration */
.hero-illustration {
  display: flex;
  align-items: center;
  justify-content: center;
}
.hero-image {
  width: 100%;
  max-width: 450px;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  object-fit: cover;
  aspect-ratio: 4/3;
}
/* Scroll Indicator */
.scroll-indicator {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 40px;
  border: 2px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  animation: bounce 2s infinite;
}
@keyframes bounce {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(8px); }
}

/* ============ SECTIONS COMMON ============ */
.section {
  padding: 96px 0;
}
.section-header {
  text-align: center;
  margin-bottom: 64px;
}
.section-badge {
  display: inline-flex;
  align-items: center;
  background: rgba(79, 70, 229, 0.08);
  color: #4F46E5;
  padding: 6px 16px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
  margin-bottom: 20px;
  border: 1px solid rgba(79, 70, 229, 0.15);
}
.section-title {
  font-size: 2.2rem;
  font-weight: 800;
  color: #1E293B;
  line-height: 1.2;
  margin-bottom: 16px;
}
.text-accent { color: #4F46E5; }
.section-subtitle {
  font-size: 1rem;
  line-height: 1.7;
  color: #64748B;
  max-width: 600px;
  margin: 0 auto;
}

/* ============ FEATURES ============ */
.features-section { background: #F8FAFC; }
.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}
.feature-card {
  background: white;
  border: 1px solid #E2E8F0;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  transition: all 0.3s ease;
}
.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  border-color: #C7D2FE;
}
.feature-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}
.feature-title {
  font-size: 0.95rem;
  font-weight: 700;
  color: #1E293B;
  margin-bottom: 10px;
}
.feature-desc {
  font-size: 0.85rem;
  line-height: 1.6;
  color: #64748B;
}

/* ============ STEPS ============ */
.steps-section { background: #F1F5F9; }
.steps-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  position: relative;
}
.step-card {
  background: white;
  border: 1px solid #E2E8F0;
  border-radius: 16px;
  padding: 36px 24px 32px;
  text-align: center;
  position: relative;
  transition: all 0.3s ease;
}
.step-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 40px rgba(0,0,0,0.06);
}
.step-number {
  position: absolute;
  top: -14px;
  left: 50%;
  transform: translateX(-50%);
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #4F46E5;
  color: white;
  font-size: 0.78rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}
.step-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}
.step-title {
  font-size: 0.95rem;
  font-weight: 700;
  color: #1E293B;
  margin-bottom: 10px;
}
.step-desc {
  font-size: 0.85rem;
  line-height: 1.6;
  color: #64748B;
}

/* ============ TESTIMONIALS ============ */
.testimonials-section { background: #F8FAFC; }
.testimonial-carousel {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  max-width: 700px;
  margin: 0 auto;
}
.carousel-arrow {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 1px solid #D1D5DB;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}
.carousel-arrow:hover {
  background: #F3F4F6;
  border-color: #9CA3AF;
}
.testimonial-card {
  background: white;
  border: 1px solid #E2E8F0;
  border-radius: 20px;
  padding: 40px 32px;
  text-align: center;
  box-shadow: 0 4px 24px rgba(0,0,0,0.04);
  min-width: 0;
  flex: 1;
}
.testimonial-avatar {
  font-size: 3rem;
  margin-bottom: 12px;
}
.testimonial-quote {
  font-size: 3rem;
  color: #C7D2FE;
  font-family: Georgia, serif;
  line-height: 1;
  margin-bottom: 4px;
}
.testimonial-text {
  font-size: 0.95rem;
  line-height: 1.7;
  color: #64748B;
  font-style: italic;
  margin-bottom: 20px;
}
.testimonial-author {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 4px;
}
.testimonial-author strong {
  font-size: 0.95rem;
  color: #1E293B;
}
.testimonial-dept-badge {
  font-size: 0.7rem;
  padding: 2px 10px;
  border-radius: 999px;
  background: #EDE9FE;
  color: #7C3AED;
  font-weight: 600;
}
.testimonial-role {
  font-size: 0.8rem;
  color: #94A3B8;
  margin-bottom: 12px;
}
.testimonial-stars {
  margin-bottom: 6px;
}
.testimonial-verified {
  font-size: 0.75rem;
  color: #94A3B8;
}
.carousel-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #D1D5DB;
  cursor: pointer;
  transition: all 0.2s;
}
.dot.active {
  background: #4F46E5;
  width: 24px;
  border-radius: 4px;
}

/* ============ CTA ============ */
.cta-section {
  background: linear-gradient(to right bottom, #312E81, #581C87, #3730A3);
  padding: 96px 0;
}
.cta-inner { text-align: center; }
.cta-badge {
  display: inline-flex;
  align-items: center;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.15);
  padding: 8px 20px;
  border-radius: 999px;
  color: rgba(255,255,255,0.9);
  font-size: 0.8rem;
  font-weight: 500;
  margin-bottom: 24px;
}
.cta-title {
  font-size: 2.4rem;
  font-weight: 800;
  color: white;
  line-height: 1.2;
  margin-bottom: 16px;
}
.cta-title-accent {
  background: linear-gradient(135deg, #818CF8, #C084FC);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.cta-subtitle {
  font-size: 1rem;
  line-height: 1.7;
  color: rgba(255,255,255,0.75);
  max-width: 600px;
  margin: 0 auto 40px;
}
.cta-trust-row {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}
.cta-trust-card {
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  padding: 12px 32px;
  border-radius: 12px;
  color: white;
  font-size: 0.875rem;
  font-weight: 500;
}
.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 40px;
}
.btn-cta-primary {
  display: inline-flex;
  align-items: center;
  padding: 14px 32px;
  background: linear-gradient(135deg, #7C3AED, #6D28D9);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}
.btn-cta-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(124, 58, 237, 0.5); }
.btn-cta-secondary {
  display: inline-flex;
  align-items: center;
  padding: 14px 32px;
  background: transparent;
  border: 1.5px solid rgba(255,255,255,0.3);
  color: white;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-cta-secondary:hover { background: rgba(255,255,255,0.1); }
.cta-divider {
  width: 100%;
  max-width: 800px;
  height: 1px;
  background: rgba(255,255,255,0.1);
  margin: 0 auto 24px;
}
.cta-bottom-trust { text-align: center; }
.cta-bottom-text {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;
  margin-bottom: 8px;
}
.cta-bottom-badges {
  display: flex;
  justify-content: center;
  gap: 24px;
  flex-wrap: wrap;
}
.cta-bottom-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem;
  color: rgba(255,255,255,0.6);
}
.green-dot { width: 6px; height: 6px; border-radius: 50%; background: #4ADE80; }
.gray-dot { width: 6px; height: 6px; border-radius: 50%; background: #9CA3AF; }

/* ============ FOOTER ============ */
.footer {
  background: #0F172A;
  padding: 64px 0 0;
}
.footer-grid {
  display: grid;
  grid-template-columns: 1.5fr 0.75fr 1fr;
  gap: 48px;
}
.footer-brand {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.footer-brand-title {
  display: block;
  font-weight: 700;
  font-size: 1rem;
  color: #818CF8;
  line-height: 1.2;
}
.footer-brand-sub {
  display: block;
  font-size: 0.75rem;
  color: rgba(255,255,255,0.4);
}
.footer-desc {
  font-size: 0.85rem;
  line-height: 1.7;
  color: rgba(255,255,255,0.5);
  margin-bottom: 20px;
}
.footer-contact-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.footer-contact-info span {
  display: flex;
  align-items: center;
  font-size: 0.8rem;
  color: rgba(255,255,255,0.45);
}
.footer-col-title {
  font-size: 0.9rem;
  font-weight: 700;
  color: white;
  margin-bottom: 20px;
}
.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}
.footer-links li { margin-bottom: 10px; }
.footer-links a {
  color: rgba(255,255,255,0.5);
  text-decoration: none;
  font-size: 0.85rem;
  cursor: pointer;
  transition: color 0.2s;
}
.footer-links a::before { content: '• '; color: rgba(255,255,255,0.3); }
.footer-links a:hover { color: rgba(255,255,255,0.9); }
.footer-connect-text {
  font-size: 0.85rem;
  color: rgba(255,255,255,0.5);
  line-height: 1.6;
  margin-bottom: 20px;
}
.social-icons { display: flex; gap: 12px; }
.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(79, 70, 229, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-decoration: none;
  transition: all 0.2s;
}
.social-icon:hover { background: #4F46E5; transform: translateY(-2px); }
.footer-divider {
  height: 1px;
  background: rgba(255,255,255,0.08);
  margin: 48px 0 24px;
}
.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}
.footer-bottom span {
  font-size: 0.8rem;
  color: rgba(255,255,255,0.35);
}
.footer-bottom-right { color: rgba(255,255,255,0.35); }

/* ============ RESPONSIVE ============ */
@media (max-width: 960px) {
  .hero-grid {
    grid-template-columns: 1fr;
    text-align: center;
  }
  .hero-heading { font-size: 2.2rem; }
  .hero-description {
    margin-left: auto;
    margin-right: auto;
  }
  .hero-stats {
    align-items: stretch;
  }
  .stat-card {
    max-width: 100%;
    width: 100%;
    text-align: left;
  }
  .hero-stats {
    flex-direction: column;
  }
  .hero-buttons {
    justify-content: center;
  }
  .hero-trust { text-align: center; }
  .trust-badges { justify-content: center; }
  .features-grid { grid-template-columns: repeat(2, 1fr); }
  .steps-grid { grid-template-columns: repeat(2, 1fr); }
  .footer-grid { grid-template-columns: 1fr; gap: 32px; }
  .footer-bottom { flex-direction: column; text-align: center; }
  .cta-title { font-size: 1.8rem; }
}

@media (max-width: 600px) {
  .section { padding: 64px 0; }
  .container { padding: 0 16px; }
  .hero-heading { font-size: 1.75rem; }
  .hero-description { font-size: 0.9rem; }
  .hero-badge { font-size: 0.72rem; }
  .hero-buttons { flex-direction: column; }
  .btn-primary-hero, .btn-secondary-hero { width: 100%; justify-content: center; }
  .features-grid { grid-template-columns: 1fr; }
  .steps-grid { grid-template-columns: 1fr; }
  .section-title { font-size: 1.5rem; }
  .section-header { margin-bottom: 40px; }
  .testimonial-carousel { gap: 8px; }
  .carousel-arrow { width: 36px; height: 36px; }
  .testimonial-card { padding: 24px 16px; }
  .testimonial-text { font-size: 0.85rem; }
  .cta-trust-row { flex-direction: column; align-items: center; }
  .cta-trust-card { width: 100%; justify-content: center; }
  .cta-buttons { flex-direction: column; align-items: center; }
  .btn-cta-primary, .btn-cta-secondary { width: 100%; justify-content: center; }
  .cta-title { font-size: 1.5rem; }
  .cta-bottom-badges { flex-direction: column; align-items: center; gap: 8px; }
  .footer-grid { grid-template-columns: 1fr; }
  .social-icons { justify-content: flex-start; }
}
</style>
