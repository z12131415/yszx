<template>
  <el-container class="portal-layout">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="logo">云枢智学</div>
      <el-menu mode="horizontal" :default-active="activeMenu" router class="nav-menu">
        <el-menu-item index="/dashboard">首页</el-menu-item>
        <el-menu-item index="/questions">题库中心</el-menu-item>
        <el-menu-item index="/wrong-book">错题本</el-menu-item>
        <el-menu-item index="/routes">学习路线</el-menu-item>
        <el-menu-item index="/ai-interview">AI面试</el-menu-item>
      </el-menu>
      <div class="user-area">
        <el-dropdown>
          <span class="user-info">
            <el-avatar :size="32" :src="userStore.avatarUrl || undefined">{{ (userStore.nickname || '?')[0] }}</el-avatar>
            <span>{{ userStore.nickname || '学员' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/useUserStore'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref(router.currentRoute.value.path)

router.afterEach((to) => {
  activeMenu.value = to.path
})

onMounted(() => {
  userStore.fetchMe()
})

async function handleLogout() {
  try { await api.post('/api/portal/auth/logout') } catch {}
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.portal-layout { min-height: 100vh; }
.header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.logo { font-size: 20px; font-weight: bold; color: #667eea; margin-right: 40px; white-space: nowrap; }
.nav-menu { flex: 1; border-bottom: none; }
.user-area { margin-left: auto; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.main { background: #f5f7fa; padding: 24px; }
</style>
