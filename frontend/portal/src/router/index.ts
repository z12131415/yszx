import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/useUserStore'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/LoginView.vue'), meta: { requiresAuth: false } },
  { path: '/register', name: 'Register', component: () => import('@/views/RegisterView.vue'), meta: { requiresAuth: false } },
  { path: '/forgot-password', name: 'ForgotPassword', component: () => import('@/views/ForgotPasswordView.vue'), meta: { requiresAuth: false } },
  {
    path: '/',
    component: () => import('@/views/LayoutView.vue'),
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/HomeView.vue'), meta: { requiresAuth: true } },
      { path: 'questions', name: 'Questions', component: () => import('@/views/QuestionsView.vue'), meta: { requiresAuth: true } },
      { path: 'questions/:id', name: 'QuestionDetail', component: () => import('@/views/QuestionDetailView.vue'), meta: { requiresAuth: false } },
      { path: 'practice/:sessionId', name: 'Practice', component: () => import('@/views/PracticeView.vue'), meta: { requiresAuth: true } },
      { path: 'wrong-book', name: 'WrongBook', component: () => import('@/views/WrongBookView.vue'), meta: { requiresAuth: true } },
      { path: 'routes', name: 'Routes', component: () => import('@/views/RoutesView.vue'), meta: { requiresAuth: true } },
      { path: 'ai-interview', name: 'AiInterview', component: () => import('@/views/AiInterviewView.vue'), meta: { requiresAuth: true } },
      { path: 'interview-report/:id', name: 'InterviewReport', component: () => import('@/views/InterviewReportView.vue'), meta: { requiresAuth: true } },
      { path: 'interview-history', name: 'InterviewHistory', component: () => import('@/views/InterviewHistoryView.vue'), meta: { requiresAuth: true } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/ProfileView.vue'), meta: { requiresAuth: true } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
