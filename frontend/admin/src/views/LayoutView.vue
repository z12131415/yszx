<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse">云枢智学</span>
        <span v-else>🎓</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :router="true"
        background-color="#1a1a2e"
        text-color="#a0aec0"
        active-text-color="#fff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <template #title>数据看板</template>
        </el-menu-item>
        <el-sub-menu index="content">
          <template #title><el-icon><Folder /></el-icon><span>内容管理</span></template>
          <el-menu-item index="/subjects">学科管理</el-menu-item>
          <el-menu-item index="/courses">B站课程库</el-menu-item>
          <el-menu-item index="/questions">题库管理</el-menu-item>
          <el-menu-item index="/interview-questions">面试题库</el-menu-item>
          <el-menu-item index="/routes">学习路线</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/students">
          <el-icon><User /></el-icon>
          <template #title>学员管理</template>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title><el-icon><Setting /></el-icon><span>系统管理</span></template>
          <el-menu-item index="/users">用户管理</el-menu-item>
          <el-menu-item index="/roles">角色管理</el-menu-item>
          <el-menu-item index="/logs">操作日志</el-menu-item>
          <el-menu-item index="/settings">系统设置</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶栏 -->
      <el-header class="header">
        <el-icon class="collapse-btn" @click="isCollapse = !isCollapse"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="adminStore.info?.avatarUrl || undefined">{{ (adminStore.info?.username || '?')[0] }}</el-avatar>
              <span>{{ adminStore.info?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/settings')">系统设置</el-dropdown-item>
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
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/useAdminStore'
import api from '@/api'

const router = useRouter()
const adminStore = useAdminStore()
const isCollapse = ref(false)
const activeMenu = ref(router.currentRoute.value.path)

router.afterEach((to) => {
  activeMenu.value = to.path
})

async function handleLogout() {
  try { await api.post('/api/admin/auth/logout') } catch {}
  adminStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { height: 100vh; }
.sidebar { background: #1a1a2e; transition: width 0.3s; overflow: hidden; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; border-bottom: 1px solid rgba(255,255,255,0.1); }
.el-menu { border: none; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e4e7ed; padding: 0 20px; }
.collapse-btn { cursor: pointer; font-size: 20px; color: #606266; }
.header-right { display: flex; align-items: center; gap: 16px; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.main { background: #f5f7fa; padding: 20px; }
</style>
