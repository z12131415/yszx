<template>
  <div class="home">
    <el-row :gutter="20" style="margin-bottom:24px">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon">{{ card.icon }}</div>
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header><span>近7天练习量</span></template>
          <div ref="chartRef" style="height:280px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span>我的错题本</span></template>
          <div class="wrongbook-summary">
            <div class="wb-item"><span class="wb-num pending">{{ pendingCount }}</span><span>待复习</span></div>
            <div class="wb-item"><span class="wb-num mastered">{{ masteredCount }}</span><span>已掌握</span></div>
            <div class="wb-item"><span class="wb-num total">{{ totalCount }}</span><span>总错题数</span></div>
          </div>
          <router-link to="/wrong-book"><el-button type="primary" style="width:100%;margin-top:16px">进入错题本</el-button></router-link>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top:20px">
      <template #header><span>快速开始</span></template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="item in quickItems" :key="item.title">
          <router-link :to="item.path" class="quick-link">
            <el-card shadow="hover">
              <div class="quick-icon">{{ item.icon }}</div>
              <div class="quick-title">{{ item.title }}</div>
              <div class="quick-desc">{{ item.desc }}</div>
            </el-card>
          </router-link>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import api from '@/api'

const chartRef = ref<HTMLElement>()

const statCards = ref([
  { icon: '📝', label: '总答题数', value: 0 },
  { icon: '✅', label: '正确率', value: '0%' },
  { icon: '❌', label: '错题数', value: 0 },
  { icon: '🔥', label: '连续打卡', value: 0 + '天' },
])

const quickItems = [
  { icon: '📚', title: '刷题练习', desc: '海量题库任你刷', path: '/questions' },
  { icon: '📖', title: '错题本', desc: '巩固薄弱知识点', path: '/wrong-book' },
  { icon: '🎯', title: 'AI面试', desc: '模拟真实面试场景', path: '/ai-interview' },
  { icon: '🗺️', title: '学习路线', desc: '系统化学习计划', path: '/routes' },
]

onMounted(() => {
  if (chartRef.value) {
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['11-05','11-06','11-07','11-08','11-09','11-10','11-11'] },
      yAxis: { type: 'value' },
      series: [{ data: [12, 8, 15, 0, 22, 18, 10], type: 'bar', itemStyle: { color: '#667eea' } }],
    })
  }
})
</script>

<style scoped>
.home { max-width: 1100px; margin: 0 auto; padding: 20px; }
.stat-card { text-align: center; padding: 16px; }
.stat-icon { font-size: 32px; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.wrongbook-summary { display: flex; justify-content: space-around; padding: 20px 0; }
.wb-item { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.wb-num { font-size: 28px; font-weight: bold; }
.wb-num.pending { color: #e6a23c; }
.wb-num.mastered { color: #67c23a; }
.wb-num.total { color: #409eff; }
.quick-link { text-decoration: none; }
.quick-link :deep(.el-card) { cursor: pointer; transition: transform 0.2s; height: 100%; }
.quick-link:hover :deep(.el-card) { transform: translateY(-4px); }
.quick-icon { font-size: 32px; margin-bottom: 8px; }
.quick-title { font-size: 15px; font-weight: bold; color: #303133; }
.quick-desc { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
