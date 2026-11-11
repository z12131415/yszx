<template>
  <AdminLayout>
    <el-card>
      <template #header><span>题库管理</span></template>
      <div style="margin-bottom:16px">
        <el-button type="primary">新增题目</el-button>
        <el-button type="success">导入Excel</el-button>
        <el-button @click="exportQuestions">导出</el-button>
      </div>
      <el-table :data="questions" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="content" label="题干" show-overflow-tooltip />
        <el-table-column prop="type" label="题型" width="80" />
        <el-table-column prop="difficulty" label="难度" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button size="small">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination layout="prev,pager,next" :total="total" style="margin-top:16px" />
    </el-card>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'
const questions = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const statusType = (s: string) => ({ draft: '', pending_review: 'warning', published: 'success', offline: 'info', rejected: 'danger' }[s] || '')
async function load() {
  loading.value = true
  const res = await api.get('/api/admin/questions')
  if (res.data.code === 0) { questions.value = res.data.data?.items || []; total.value = res.data.data?.total || 0 }
  loading.value = false
}
function exportQuestions() { window.open('/api/admin/questions/export') }
onMounted(load)
</script>
