<template>
  <AdminLayout>
    <el-card><template #header><span>学员管理</span></template>
      <el-form :inline="true" style="margin-bottom:16px">
        <el-form-item><el-input v-model="keyword" placeholder="昵称搜索" clearable /></el-form-item>
        <el-button type="primary" @click="load">搜索</el-button>
      </el-form>
      <el-table :data="students" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="lastLoginAt" label="最后登录" width="160" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button size="small" type="warning" @click="toggleStatus(row)">启禁用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination layout="prev,pager,next" :total="total" style="margin-top:16px" />
    </el-card>
  </AdminLayout>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'
const students = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const keyword = ref('')
async function load() {
  loading.value = true
  const res = await api.get('/api/admin/students', { params: { keyword: keyword.value } })
  if (res.data.code === 0) { students.value = res.data.data?.items || []; total.value = res.data.data?.total || 0 }
  loading.value = false
}
async function toggleStatus(s: any) {
  const newStatus = s.status === 1 ? 0 : 1
  const res = await api.patch(`/api/admin/students/${s.id}/status`, { status: newStatus })
  if (res.data.code === 0) { ElMessage.success('操作成功'); load() }
}
function viewDetail(s: any) { console.log('detail', s) }
onMounted(load)
</script>
