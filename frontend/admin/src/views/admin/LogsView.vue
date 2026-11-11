<template>
  <AdminLayout>
    <el-card>
      <template #header><span>操作日志</span></template>
      <el-form :inline="true" style="margin-bottom:16px">
        <el-form-item label="操作类型">
          <el-select v-model="filters.actionType" placeholder="全部" clearable>
            <el-option label="登录" value="LOGIN" />
            <el-option label="创建" value="CREATE" />
            <el-option label="更新" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="审核" value="REVIEW" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标类型">
          <el-select v-model="filters.targetType" placeholder="全部" clearable>
            <el-option label="学科" value="SUBJECT" />
            <el-option label="题目" value="QUESTION" />
            <el-option label="课程" value="COURSE" />
            <el-option label="学员" value="STUDENT" />
            <el-option label="用户" value="USER" />
            <el-option label="角色" value="ROLE" />
          </el-select>
        </el-form-item>
        <el-form-item><el-input v-model="filters.keyword" placeholder="搜索详情..." clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="exportLogs">导出CSV</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="logs" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="actionType" label="操作类型" width="100" />
        <el-table-column prop="targetType" label="目标类型" width="100" />
        <el-table-column prop="detail" label="详情" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP" width="130" />
        <el-table-column prop="createdAt" label="操作时间" width="170" />
      </el-table>
      <el-pagination layout="prev,pager,next" :total="total" style="margin-top:16px" @current-change="load" />
    </el-card>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'

const logs = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const filters = ref({ actionType: '', targetType: '', keyword: '' })

async function load(page = 1) {
  loading.value = true
  const res = await api.get('/api/admin/logs', { params: { ...filters.value, page } })
  if (res.data.code === 0) { logs.value = res.data.data?.items || []; total.value = res.data.data?.total || 0 }
  loading.value = false
}

function exportLogs() { window.open('/api/admin/logs/export') }

onMounted(() => load())
</script>
