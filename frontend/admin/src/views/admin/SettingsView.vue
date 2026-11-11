<template>
  <AdminLayout>
    <el-card>
      <template #header><span>系统设置</span></template>
      <el-form :model="settings" label-width="160px" style="max-width: 600px">
        <el-form-item label="平台名称"><el-input v-model="settings.platformName" /></el-form-item>
        <el-form-item label="平台描述"><el-input v-model="settings.platformDescription" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="Logo URL"><el-input v-model="settings.logoUrl" /></el-form-item>
        <el-form-item label="默认头像"><el-input v-model="settings.defaultAvatarUrl" /></el-form-item>
        <el-form-item label="AI模型">
          <el-select v-model="settings.aiModel" style="width: 100%">
            <el-option label="Claude Sonnet 4" value="claude-sonnet-4-20250514" />
            <el-option label="GPT-4o" value="gpt-4o" />
          </el-select>
        </el-form-item>
        <el-form-item label="每日面试配额">
          <el-input-number v-model="settings.dailyInterviewQuota" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="开放注册">
          <el-switch v-model="settings.registrationEnabled" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'

const settings = ref({
  platformName: '云枢智学',
  platformDescription: 'AI赋能的智能面试训练平台',
  logoUrl: '',
  defaultAvatarUrl: '',
  aiModel: 'claude-sonnet-4-20250514',
  dailyInterviewQuota: 5,
  registrationEnabled: true,
})

async function loadSettings() {
  const res = await api.get('/api/admin/settings')
  if (res.data.code === 0 && res.data.data) Object.assign(settings.value, res.data.data)
}

async function saveSettings() {
  const res = await api.put('/api/admin/settings', settings.value)
  if (res.data.code === 0) ElMessage.success('保存成功')
  else ElMessage.error(res.data.message || '保存失败')
}

onMounted(loadSettings)
</script>
