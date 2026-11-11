<template>
  <div class="container">
    <el-page-header @back="router.push('/ai-interview')" content="面试历史" />
    <el-table :data="records" stripe v-loading="loading">
      <el-table-column prop="direction" label="方向" width="120" />
      <el-table-column prop="difficulty" label="难度" width="80" />
      <el-table-column prop="questionCount" label="题数" width="60" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column prop="durationSec" label="用时" width="100">
        <template #default="{ row }">{{ formatDuration(row.durationSec) }}</template>
      </el-table-column>
      <el-table-column prop="completedAt" label="完成时间" width="170" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <router-link :to="`/interview-report/${row.id}`"><el-button size="small">查看报告</el-button></router-link>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination layout="prev,pager,next" :total="total" style="margin-top:16px" @current-change="load" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const records = ref<any[]>([])
const loading = ref(false)
const total = ref(0)

async function load(page = 1) {
  loading.value = true
  const res = await api.get('/api/portal/interviews', { params: { page } })
  if (res.data.code === 0) { records.value = res.data.data?.items || []; total.value = res.data.data?.total || 0 }
  loading.value = false
}

function formatDuration(sec: number) {
  return `${Math.floor(sec / 60)}分${sec % 60}秒`
}

onMounted(() => load())
</script>

<style scoped>
.container { max-width: 1000px; margin: 0 auto; padding: 20px; }
</style>
