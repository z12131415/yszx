import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/useAdminStore'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/admin/LoginView.vue'), meta: { requiresAuth: false } },
  {
    path: '/',
    component: () => import('@/views/LayoutView.vue'),
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/admin/DashboardView.vue'), meta: { requiresAuth: true } },
      { path: 'subjects', name: 'Subjects', component: () => import('@/views/admin/SubjectsView.vue'), meta: { requiresAuth: true } },
      { path: 'courses', name: 'Courses', component: () => import('@/views/admin/CoursesView.vue'), meta: { requiresAuth: true } },
      { path: 'questions', name: 'Questions', component: () => import('@/views/admin/QuestionsView.vue'), meta: { requiresAuth: true } },
      { path: 'interview-questions', name: 'InterviewQuestions', component: () => import('@/views/admin/InterviewQuestionsView.vue'), meta: { requiresAuth: true } },
      { path: 'routes', name: 'Routes', component: () => import('@/views/admin/RoutesView.vue'), meta: { requiresAuth: true } },
      { path: 'students', name: 'Students', component: () => import('@/views/admin/StudentsView.vue'), meta: { requiresAuth: true } },
      { path: 'users', name: 'Users', component: () => import('@/views/admin/UsersView.vue'), meta: { requiresAuth: true } },
      { path: 'roles', name: 'Roles', component: () => import('@/views/admin/RolesView.vue'), meta: { requiresAuth: true } },
      { path: 'logs', name: 'Logs', component: () => import('@/views/admin/LogsView.vue'), meta: { requiresAuth: true } },
      { path: 'settings', name: 'Settings', component: () => import('@/views/admin/SettingsView.vue'), meta: { requiresAuth: true } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const adminStore = useAdminStore()
  if (to.meta.requiresAuth !== false && !adminStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
