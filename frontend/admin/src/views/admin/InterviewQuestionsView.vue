<template>
  <AdminLayout>
    <el-card><template #header><span>面试题库</span></template>
      <el-button type="primary" style="margin-bottom:16px">手工录入</el-button>
      <el-button type="success" style="margin-bottom:16px">Excel导入</el-button>
      <el-table :data="questions" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="content" label="题干" show-overflow-tooltip />
        <el-table-column prop="topic" label="主题" width="120" />
        <el-table-column prop="difficulty" label="难度" width="80" />
        <el-table-column label="操作"><el-button size="small">编辑</el-button></el-table-column>
      </el-table>
    </el-card>
  </AdminLayout>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'
const questions = ref<any[]>([])
const loading = ref(false)
async function load() {
  loading.value = true
  const res = await api.get('/api/admin/interview-questions')
  if (res.data.code === 0) questions.value = res.data.data?.items || []
  loading.value = false
}
onMounted(load)
</script>
