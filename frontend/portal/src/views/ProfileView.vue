<template>
  <div class="container">
    <el-card>
      <template #header><span>个人中心</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ profile.userId }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ profile.phone }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ profile.nickname }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ profile.gender || '保密' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ profile.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ profile.joinedAt }}</el-descriptions-item>
        <el-descriptions-item label="最后登录" :span="2">{{ profile.lastLoginAt || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <h4 style="margin-bottom:16px">修改资料</h4>
      <el-form :model="editForm" label-width="80px" style="max-width:400px">
        <el-form-item label="昵称"><el-input v-model="editForm.nickname" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
            <el-radio value="保密">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const profile = ref<any>({ userId: 0, phone: '', nickname: '', gender: '保密', email: null, joinedAt: '', lastLoginAt: '' })
const editForm = ref({ nickname: '', gender: '保密', email: '' })

async function load() {
  const res = await api.get('/api/portal/profile')
  if (res.data.code === 0 && res.data.data) {
    profile.value = res.data.data
    editForm.value = { nickname: res.data.data.nickname || '', gender: res.data.data.gender || '保密', email: res.data.data.email || '' }
  }
}

async function saveProfile() {
  const res = await api.put('/api/portal/profile', editForm.value)
  if (res.data.code === 0) { ElMessage.success('保存成功'); load() }
  else ElMessage.error(res.data.message || '保存失败')
}

onMounted(load)
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; }
</style>
