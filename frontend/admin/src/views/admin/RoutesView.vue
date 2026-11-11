<template>
  <AdminLayout>
    <el-card><template #header><span>学习路线</span></template>
      <el-button type="primary" style="margin-bottom:16px">新建路线</el-button>
      <el-table :data="routes" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="subjectName" label="所属学科" width="120" />
        <el-table-column prop="courseCount" label="课程数" width="80" />
        <el-table-column label="操作"><el-button size="small">编辑</el-button></el-table-column>
      </el-table>
    </el-card>
  </AdminLayout>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'
const routes = ref<any[]>([])
const loading = ref(false)
async function load() {
  loading.value = true
  const res = await api.get('/api/admin/routes')
  if (res.data.code === 0) routes.value = res.data.data || []
  loading.value = false
}
onMounted(load)
</script>
