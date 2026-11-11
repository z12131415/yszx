<template>
  <div class="dashboard">
    <h2 style="margin-bottom:20px">数据看板</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in statsCards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>用户增长趋势</template>
          <div ref="chartRef" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>最近操作记录</template>
          <el-timeline>
            <el-timeline-item
              v-for="log in recentLogs"
              :key="log.id"
              :timestamp="log.createdAt"
              placement="top"
            >
              {{ log.username }} {{ log.actionType }} {{ log.targetType }}
            </el-timeline-item>
          </el-timeline>
          <el-empty v-if="recentLogs.length === 0" description="暂无记录" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import api from '@/api'

const statsCards = ref([
  { label: '总学科数', value: 8 },
  { label: '总题目数', value: 0 },
  { label: '总学员数', value: 0 },
  { label: '今日新增学员', value: 0 },
])
const chartRef = ref<HTMLElement>()
const recentLogs = ref<any[]>([])

onMounted(() => {
  if (chartRef.value) {
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['11-05','11-06','11-07','11-08','11-09','11-10','11-11'] },
      yAxis: { type: 'value' },
      series: [{ data: [120, 132, 101, 134, 190, 230, 210], type: 'line', smooth: true, areaStyle: {} }],
    })
  }
})
</script>

<style scoped>
.dashboard { }
.stat-card { text-align: center; padding: 10px; }
.stat-value { font-size: 32px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 14px; color: #909399; margin-top: 8px; }
</style>
