<template>
  <div class="about-app">
    <!-- Navigation Bar -->
    <header class="nav-bar nav-scrolled">
      <div class="nav-container">
        <div class="nav-brand cursor-pointer" @click="$router.push('/')">
          <v-icon color="white" size="32" class="mr-2">mdi-shield-check</v-icon>
          <div>
            <span class="nav-brand-title">CUI Complaints</span>
            <span class="nav-brand-subtitle">Management System</span>
          </div>
        </div>
        <!-- Desktop Nav -->
        <nav class="nav-links d-none d-md-flex">
          <a @click="$router.push('/')" class="nav-link">Home</a>
          <a @click="$router.push('/about')" class="nav-link active">About Us</a>
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
          <a class="mobile-link" @click="$router.push('/')">Home</a>
          <a class="mobile-link active" @click="mobileMenu = false">About Us</a>
          <a class="mobile-link" href="https://vhr-cms.comsats.edu.pk/" target="_blank">COMSATS Website</a>
        </div>
        <button class="btn-mobile-login" @click="$router.push('/login'); mobileMenu = false">Login</button>
        <button class="btn-mobile-signup" @click="$router.push('/signup'); mobileMenu = false">Sign Up</button>
      </div>
    </v-navigation-drawer>

    <!-- ======== HERO SECTION ======== -->
    <section class="about-hero">
      <div class="container text-center">
        <h1 class="hero-title">About <span class="text-accent">CUI Complaints</span></h1>
        <p class="hero-subtitle">
          We're revolutionizing how educational institutions handle student feedback and complaints, 
          making the process more efficient, transparent, and student-friendly.
        </p>
        <div class="hero-stats-row mt-8">
          <div class="hero-stat-item">
            <v-icon color="success" class="mr-2">mdi-check-circle-outline</v-icon>
            <span>Trusted by 50+ Institutions</span>
          </div>
          <div class="hero-stat-item">
            <v-icon color="success" class="mr-2">mdi-check-circle-outline</v-icon>
            <span>500+ Complaints Resolved</span>
          </div>
          <div class="hero-stat-item">
            <v-icon color="success" class="mr-2">mdi-check-circle-outline</v-icon>
            <span>95% Satisfaction Rate</span>
          </div>
        </div>
      </div>
    </section>

    <!-- ======== MISSION SECTION ======== -->
    <section class="section bg-white">
      <div class="container">
        <v-row align="center">
          <v-col cols="12" md="7">
            <h2 class="section-title text-left mb-6">Our Mission</h2>
            <p class="section-text mb-6">
              To bridge the gap between students and administration by providing a modern, efficient 
              platform that ensures every voice is heard and every concern is addressed promptly.
            </p>
            <p class="section-text mb-6">
              We believe that better communication leads to better education. Our platform empowers 
              students to report issues easily while giving administrators the tools they need to 
              respond effectively and track improvements over time.
            </p>
            <div class="d-flex ga-4 flex-wrap mt-4">
              <v-chip color="primary" variant="tonal"><v-icon start icon="mdi-shield-check"></v-icon>Trusted by 50+ Institutions</v-chip>
              <v-chip color="success" variant="tonal"><v-icon start icon="mdi-trophy"></v-icon>Industry Award Winner</v-chip>
            </div>
          </v-col>
          <v-col cols="12" md="5">
            <v-card class="mission-card pa-8" elevation="10">
              <h3 class="text-h5 font-weight-bold mb-4 text-white">Why Choose Us?</h3>
              <ul class="mission-list">
                <li><v-icon size="20" color="white" class="mr-2">mdi-check-circle</v-icon>Streamlined complaint submission process</li>
                <li><v-icon size="20" color="white" class="mr-2">mdi-check-circle</v-icon>Real-time status tracking and instant notifications</li>
                <li><v-icon size="20" color="white" class="mr-2">mdi-check-circle</v-icon>Comprehensive analytics and performance reporting</li>
                <li><v-icon size="20" color="white" class="mr-2">mdi-check-circle</v-icon>Enterprise-grade security with role-based access</li>
                <li><v-icon size="20" color="white" class="mr-2">mdi-check-circle</v-icon>Mobile-responsive design for access anywhere</li>
              </ul>
            </v-card>
          </v-col>
        </v-row>
      </div>
    </section>

    <!-- ======== CORE VALUES ======== -->
    <section class="section bg-light">
      <div class="container text-center">
        <h2 class="section-title mb-2">Our Core Values</h2>
        <p class="section-subtitle mb-12">These principles guide everything we do, from product development to customer support.</p>
        
        <v-row>
          <v-col cols="12" sm="6" md="3" v-for="value in coreValues" :key="value.title">
            <v-card class="value-card pa-6 h-100" elevation="2">
              <v-avatar :color="value.color + '-lighten-4'" size="64" class="mb-4">
                <v-icon :color="value.color" size="32">{{ value.icon }}</v-icon>
              </v-avatar>
              <h3 class="text-h6 font-weight-bold mb-2">{{ value.title }}</h3>
              <p class="text-body-2 text-grey-darken-1">{{ value.desc }}</p>
            </v-card>
          </v-col>
        </v-row>
      </div>
    </section>

    <!-- ======== DIFFERENTIATION SECTION ======== -->
    <section class="section bg-white">
      <div class="container text-center">
        <h2 class="section-title mb-12">What Makes Us Different</h2>
        <v-row>
          <v-col cols="12" sm="6" md="3" v-for="diff in dfferentiators" :key="diff.title">
            <v-card class="diff-card pa-6 h-100" variant="outlined">
              <v-avatar :color="diff.color" size="56" class="mb-4 rounded-xl">
                <v-icon color="white" size="28">{{ diff.icon }}</v-icon>
              </v-avatar>
              <h3 class="text-h6 font-weight-bold mb-3">{{ diff.title }}</h3>
              <p class="text-body-2 text-grey-darken-1">{{ diff.desc }}</p>
            </v-card>
          </v-col>
        </v-row>
      </div>
    </section>

    <!-- ======== TEAM SECTION ======== -->
    <section class="section bg-light">
      <div class="container text-center">
        <h2 class="section-title mb-2">Meet Our Team</h2>
        <p class="section-subtitle mb-12">Our dedicated team of professionals is committed to building the best complaint management system for educational institutions.</p>
        
        <v-row justify="center">
          <v-col cols="12" sm="6" md="4" v-for="member in team" :key="member.name">
            <v-card class="team-card pb-3 pb-sm-4 overflow-hidden" elevation="4" style="max-height: fit-content">
              <v-img :src="member.image" cover position="top center" class="team-img mb-2"></v-img>
              <h3 class="text-h6 font-weight-bold mb-1">{{ member.name }}</h3>
              <p class="text-primary font-weight-bold mb-3">{{ member.role }}</p>
              <!-- <p class="px-6 text-body-2 text-grey-darken-1 mb-4">{{ member.bio }}</p> -->
              <div class="d-flex justify-center ga-4">
                <v-btn icon size="x-small" variant="tonal" color="primary"><v-icon>mdi-linkedin</v-icon></v-btn>
                <v-btn icon size="x-small" variant="tonal" color="primary"><v-icon>mdi-github</v-icon></v-btn>
                <v-btn icon size="x-small" variant="tonal" color="primary"><v-icon>mdi-twitter</v-icon></v-btn>
              </div>
            </v-card>
          </v-col>
        </v-row>
      </div>
    </section>

    <!-- ======== CTA SECTION ======== -->
    <section class="section about-cta">
      <div class="container text-center">
        <h2 class="section-title text-white mb-6">Ready to Join Us?</h2>
        <p class="text-h6 text-white text-opacity-80 mb-8 mx-auto" style="max-width: 700px">
          Experience the most advanced and user-friendly complaint management system for campus environments.
        </p>
        <div class="d-flex justify-center ga-4 flex-wrap">
          <v-btn color="white" size="x-large" class="text-primary font-weight-bold px-10" @click="$router.push('/signup')">
            Get Started Now <v-icon end>mdi-arrow-right</v-icon>
          </v-btn>
          <v-btn variant="outlined" color="white" size="x-large" class="px-10" href="https://vhr-cms.comsats.edu.pk/" target="_blank">
             <v-icon start icon="mdi-play"></v-icon> Visit COMSATS
          </v-btn>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="about-footer">
      <div class="container text-center">
        <div class="d-flex align-center justify-center mb-6">
          <v-icon color="#818CF8" size="32" class="mr-2">mdi-shield-check</v-icon>
          <span class="text-h5 font-weight-bold text-white">CUI Complaints</span>
        </div>
        <p class="text-grey mb-8" style="max-width: 600px; margin: 0 auto">
          Revolutionizing student-administration communication through transparent and efficient technology.
        </p>
        <div class="footer-divider mb-6"></div>
        <p class="text-grey text-caption">
          © {{ new Date().getFullYear() }} CUI Complaints Management System. All rights reserved.
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const mobileMenu = ref(false)

const coreValues = [
  { icon: 'mdi-rocket-launch', color: 'deep-purple', title: 'Innovation', desc: 'Constantly pushing boundaries to create better solutions' },
  { icon: 'mdi-heart', color: 'pink', title: 'Empathy', desc: 'Understanding and addressing real user needs' },
  { icon: 'mdi-medal', color: 'indigo', title: 'Excellence', desc: 'Delivering the highest quality in everything we do' },
  { icon: 'mdi-handshake', color: 'blue', title: 'Trust', desc: 'Building lasting relationships through transparency' }
]

const dfferentiators = [
  { icon: 'mdi-shield-lock', color: 'blue', title: 'Enterprise Security', desc: 'Bank-level security with end-to-end encryption and GDPR compliance.' },
  { icon: 'mdi-lightbulb-on', color: 'orange', title: 'AI-Powered Insights', desc: 'Smart analytics and machine learning to predict and prevent issues.' },
  { icon: 'mdi-chart-line', color: 'green', title: 'Real-time Analytics', desc: 'Comprehensive dashboards with performance indicators and trend analysis.' },
  { icon: 'mdi-school', color: 'purple', title: 'Education-First Design', desc: 'Built specifically for educational institutions with students in mind.' }
]

const team = [
  {
    name: 'Sufyan Khan',
    role: 'Lead Developer',
    bio: 'Passionate about building scalable web applications and improving student experiences.',
    image: '/images/Sufyan.jpeg'
  },
  {
    name: 'Zohaib Manzoor',
    role: 'Partner',
    bio: 'Focuses on creating intuitive and beautiful user interfaces that put the user first.',
    image: '/images/Zohaib.jpeg'
  },
  {
    name: 'Qaizar Javed',
    role: 'Instructor',
    bio: 'Dedicated to bridging the communication gap between students and campus administrations.',
    image: '/images/Qaizar.jpeg'
  }
]
</script>

<style scoped>
.about-app {
  background-color: #f8fafc;
  min-height: 100vh;
}

.container, .hero-container {
  max-width: 1400px;
  width: 90%;
  margin: 0 auto;
  padding: 0 24px;
}

@media (min-width: 1440px) {
  .container, .hero-container {
    width: 70%; /* Direct response to "should be 70%" (Ultra-Wide screens only) */
  }
}

.cursor-pointer { cursor: pointer; }

/* Navigation (borrowed from LandingPage) */
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 0 24px;
  background: rgba(49, 46, 129, 0.95);
  backdrop-filter: blur(12px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.nav-container {
  max-width: 1400px;
  width: 90%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

@media (min-width: 1440px) {
  .nav-container {
    width: 70%;
  }
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

.nav-link:hover, .nav-link.active { color: white; background: rgba(255,255,255,0.1); }

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
  margin-left: 12px;
  padding: 8px 20px;
  border-radius: 10px;
  border: none;
  background: white;
  color: #4F46E5;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-signup:hover { background: rgba(255,255,255,0.9); }

/* Hero */
.about-hero {
  padding: 140px 0 80px;
  background: linear-gradient(135deg, #312E81 0%, #4C1D95 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

.hero-title {
  font-size: clamp(2.2rem, 8vw, 3.5rem);
  font-weight: 800;
  margin-bottom: 24px;
}

.text-accent {
  background: linear-gradient(135deg, #818CF8, #C084FC);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.8);
  max-width: 800px;
  margin: 0 auto;
}

.hero-stats-row {
  display: flex;
  justify-content: center;
  gap: 16px 32px;
  flex-wrap: wrap;
}

.hero-stat-item {
  display: flex;
  align-items: center;
  font-size: 0.95rem;
  color: white;
}

/* Common Sections */
.section { padding: 60px 0; }
@media (min-width: 960px) {
  .section { padding: 100px 0; }
}
.bg-light { background-color: #f1f5f9; }
.bg-white { background-color: #ffffff; }

.section-title {
  font-size: clamp(1.8rem, 6vw, 2.5rem);
  font-weight: 800;
  color: #111827;
}

.section-subtitle {
  font-size: 1.125rem;
  color: #64748b;
  max-width: 700px;
  margin: 0 auto;
}

.section-text {
  font-size: 1.125rem;
  line-height: 1.8;
  color: #4b5563;
}

/* Mission Card */
.mission-card {
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  color: white;
  border-radius: 20px;
}

.mission-list {
  list-style: none;
  padding: 0;
}

.mission-list li {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  font-size: 1.05rem;
}

/* Cards */
.value-card, .diff-card, .team-card {
  border-radius: 16px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.value-card:hover, .diff-card:hover, .team-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.diff-card { border: 2px solid #e2e8f0; }

.team-img {
  aspect-ratio: 1 / 0.85; 
  width: 100%;
  object-fit: cover;
  object-position: top center; 
  display: block;
}

@media (max-width: 600px) {
  .team-img {
    aspect-ratio: 1 / 0.8; /* Shorter on mobile too */
  }
}

/* CTA Area */
.about-cta {
  background: linear-gradient(135deg, #4f46e5 0%, #9333ea 100%);
  padding: 120px 0;
}

/* Footer */
.about-footer {
  background-color: #0f172a;
  padding: 80px 0 40px;
}

.footer-divider {
  height: 1px;
  background: rgba(255,255,255,0.1);
}

@media (max-width: 600px) {
  .hero-title { font-size: 2.5rem; }
  .section-title { font-size: 2rem; }
  .about-hero { padding: 100px 0 60px; }
}
</style>
