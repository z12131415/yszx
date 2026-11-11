<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="login-title">云枢智学</h1>
      <p class="login-subtitle">AI赋能的智能面试训练平台</p>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-button
          type="primary"
          size="large"
          :loading="loading"
          class="login-btn"
          @click="handleLogin"
        >
          登 录
        </el-button>

        <div class="login-links">
          <router-link to="/register">立即注册</router-link>
          <router-link to="/forgot-password">忘记密码？</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/useUserStore'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: '',
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 32, message: '密码长度为8-32位', trigger: 'blur' },
  ],
}

async function handleLogin() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    const res = await api.post('/api/portal/auth/login', form)
    if (res.data.code === 0) {
      const { userId, nickname, avatarUrl } = res.data.data
      userStore.login(userId, nickname, avatarUrl)
      ElMessage.success('登录成功')
      router.push('/questions')
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
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}
.login-title {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}
.login-subtitle {
  text-align: center;
  font-size: 14px;
  color: #909399;
  margin-bottom: 32px;
}
.login-form {
  margin-top: 24px;
}
.login-btn {
  width: 100%;
  margin-top: 8px;
}
.login-links {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
  font-size: 13px;
}
.login-links a {
  color: #409eff;
  text-decoration: none;
}
.login-links a:hover {
  text-decoration: underline;
}
</style>
