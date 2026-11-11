<template>
  <AdminLayout>
    <el-card>
      <template #header><span>B站课程库</span></template>
      <div style="margin-bottom:16px">
        <el-button type="primary" @click="showImport">Excel导入</el-button>
        <el-button @click="exportCourses">导出</el-button>
      </div>
      <el-table :data="courses" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="课程名称" />
        <el-table-column prop="bvId" label="BV号" width="120" />
        <el-table-column prop="duration" label="时长" width="80" />
        <el-table-column prop="source" label="来源" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default><el-button size="small">编辑</el-button></template>
        </el-table-column>
      </el-table>
      <el-dialog v-model="importVisible" title="Excel导入课程" width="400px">
        <el-upload drag action="/api/admin/courses/import" :show-file-list="false" accept=".xlsx,.xls">
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">拖拽文件到此处或<em>点击上传</em></div>
        </el-upload>
      </el-dialog>
    </el-card>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'
const courses = ref<any[]>([])
const loading = ref(false)
const importVisible = ref(false)
async function load() {
  loading.value = true
  const res = await api.get('/api/admin/courses')
  if (res.data.code === 0) courses.value = res.data.data || []
  loading.value = false
}
function showImport() { importVisible.value = true }
function exportCourses() { window.open('/api/admin/courses/export') }
onMounted(load)
</script>
