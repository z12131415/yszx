<template>
  <div class="forgot-container">
    <div class="forgot-card">
      <h1 class="forgot-title">找回密码</h1>
      <el-steps :active="step" finish-status="success" align-center style="margin: 24px 0">
        <el-step title="验证身份" />
        <el-step title="重置密码" />
        <el-step title="完成" />
      </el-steps>

      <!-- 步骤一：验证手机号+验证码 -->
      <el-form v-if="step === 0" ref="form0Ref" :model="step0" :rules="rules0" class="form">
        <el-form-item prop="phone"><el-input v-model="step0.phone" placeholder="手机号" prefix-icon="Phone" size="large" /></el-form-item>
        <el-form-item prop="code">
          <div style="display:flex;gap:8px;width:100%">
            <el-input v-model="step0.code" placeholder="验证码" style="flex:1" />
            <el-button :disabled="countdown > 0" @click="sendCode" style="white-space:nowrap">{{ countdown > 0 ? `${countdown}s` : '获取验证码' }}</el-button>
          </div>
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" class="next-btn" @click="nextStep(0)">下一步</el-button>
      </el-form>

      <!-- 步骤二：设置新密码 -->
      <el-form v-if="step === 1" ref="form1Ref" :model="step1" :rules="rules1" class="form">
        <el-form-item prop="newPassword"><el-input v-model="step1.newPassword" type="password" placeholder="新密码（8-32位）" prefix-icon="Lock" size="large" show-password /></el-form-item>
        <el-form-item prop="confirmPassword"><el-input v-model="step1.confirmPassword" type="password" placeholder="确认新密码" prefix-icon="Lock" size="large" show-password /></el-form-item>
        <el-button size="large" @click="step--" style="margin-right:8px">上一步</el-button>
        <el-button type="primary" size="large" :loading="loading" class="next-btn" @click="nextStep(1)">确认重置</el-button>
      </el-form>

      <!-- 步骤三：完成 -->
      <div v-if="step === 2" class="done">
        <el-icon size="48" color="#67c23a"><CircleCheckFilled /></el-icon>
        <p>密码重置成功！</p>
        <router-link to="/login"><el-button type="primary" size="large">返回登录</el-button></router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()
const step = ref(0)
const loading = ref(false)
const countdown = ref(0)

const step0 = reactive({ phone: '', code: '' })
const step1 = reactive({ newPassword: '', confirmPassword: '' })

const rules0 = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}
const rules1 = {
  newPassword: [{ required: true, min: 8, max: 32, message: '密码长度8-32位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }],
}

async function sendCode() {
  try { await Promise.resolve(); countdown.value = 60; const t = setInterval(() => { countdown.value--; if (countdown.value <= 0) clearInterval(t) }, 1000) } catch {}
}

async function nextStep(current: number) {
  if (current === 0) {
    // 发送验证码逻辑 — 后续接入真实接口
    step.value = 1
  } else if (current === 1) {
    loading.value = true
    try {
      // TODO: 调用重置密码接口
      step.value = 2
      ElMessage.success('密码已重置')
    } finally { loading.value = false }
  }
}
</script>

<style scoped>
.forgot-container { display: flex; align-items: center; justify-content: center; min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.forgot-card { width: 480px; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 8px 32px rgba(0,0,0,0.15); }
.forgot-title { text-align: center; font-size: 22px; font-weight: bold; color: #303133; margin-bottom: 8px; }
.form { margin-top: 20px; }
.next-btn { width: 100%; margin-top: 8px; }
.done { text-align: center; padding: 20px 0; }
.done p { margin: 16px 0 24px; color: #67c23a; font-size: 16px; }
</style>
