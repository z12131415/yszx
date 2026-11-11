<template>
  <div class="register-container">
    <div class="register-card">
      <h1 class="register-title">注册账号</h1>
      <p class="register-subtitle">开始你的智能面试训练之旅</p>

      <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" prefix-icon="Phone" size="large" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（8-32位，含字母+数字）" prefix-icon="Lock" size="large" show-password />
        </el-form-item>

        <el-form-item prop="passwordConfirm">
          <el-input v-model="form.passwordConfirm" type="password" placeholder="确认密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称（2-20字符）" prefix-icon="User" size="large" />
        </el-form-item>

        <el-form-item prop="agreedTerms">
          <el-checkbox v-model="form.agreedTerms">我已阅读并同意《用户协议》和《隐私政策》</el-checkbox>
        </el-form-item>

        <el-button type="primary" size="large" :loading="loading" class="register-btn" @click="handleRegister">
          注 册
        </el-button>

        <div class="register-links">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: '',
  passwordConfirm: '',
  nickname: '',
  agreedTerms: false,
  smsCode: '',
})

const validatePasswordConfirm = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 32, message: '密码长度为8-32位', trigger: 'blur' },
  ],
  passwordConfirm: [{ validator: validatePasswordConfirm, trigger: 'blur' }],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度2-20个字符', trigger: 'blur' },
  ],
  agreedTerms: [{ required: true, type: 'boolean', message: '请阅读并同意用户协议', trigger: 'change' }],
}

async function handleRegister() {
  try { await formRef.value.validate() } catch { return }
  loading.value = true
  try {
    const res = await api.post('/api/portal/auth/register', form)
    if (res.data.code === 0) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.data.message || '注册失败')
    }
  } catch (err: any) {
    ElMessage.error(err.response?.data?.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container { display: flex; align-items: center; justify-content: center; min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.register-card { width: 420px; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 8px 32px rgba(0,0,0,0.15); }
.register-title { text-align: center; font-size: 24px; font-weight: bold; color: #303133; margin-bottom: 8px; }
.register-subtitle { text-align: center; font-size: 14px; color: #909399; margin-bottom: 32px; }
.register-form { margin-top: 24px; }
.register-btn { width: 100%; margin-top: 8px; }
.register-links { text-align: center; margin-top: 16px; font-size: 13px; }
.register-links a { color: #409eff; text-decoration: none; }
</style>
