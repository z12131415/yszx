<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="login-title">云枢智学 · 管理后台</h1>
      <p class="login-subtitle">系统管理平台</p>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock"
            size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>

        <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '@/stores/useAdminStore'
import api from '@/api'

const router = useRouter()
const adminStore = useAdminStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  try { await formRef.value.validate() } catch { return }
  loading.value = true
  try {
    const res = await api.post('/api/admin/auth/login', form)
    if (res.data.code === 0) {
      adminStore.setInfo(res.data.data)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (err: any) {
    ElMessage.error(err.response?.data?.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container { display: flex; align-items: center; justify-content: center; min-height: 100vh; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); }
.login-card { width: 400px; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 8px 32px rgba(0,0,0,0.3); }
.login-title { text-align: center; font-size: 22px; color: #1a1a2e; margin-bottom: 8px; }
.login-subtitle { text-align: center; font-size: 14px; color: #888; margin-bottom: 32px; }
.login-form { margin-top: 24px; }
.login-btn { width: 100%; margin-top: 8px; background: #1a1a2e; border-color: #1a1a2e; }
</style>
