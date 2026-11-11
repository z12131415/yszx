<template>
  <div class="container">
    <el-page-header @back="router.push('/ai-interview')" content="面试报告" class="page-header" />
    <el-card v-loading="loading" class="report-card">
      <template #header><span>🎯 AI 面试报告</span></template>
      <div class="score-overview">
        <div class="total-score">
          <div class="score-num">{{ report?.totalScore ?? '--' }}</div>
          <div class="score-label">综合评分</div>
        </div>
        <div class="duration">{{ formatDuration(report?.durationSec) }}</div>
      </div>

      <h4>各维度得分</h4>
      <el-row :gutter="16" v-if="report?.dimensionScores">
        <el-col :span="8" v-for="(val, key) in report.dimensionScores" :key="key">
          <el-progress type="circle" :percentage="Math.round(val * 10)" :width="80" :color="getScoreColor(val)" />
          <p>{{ dimName(key) }}</p>
        </el-col>
      </el-row>

      <div v-if="report?.strengths" class="sections">
        <div class="section strengths"><h4>💪 优势</h4><ul><li v-for="s in report.strengths">{{ s }}</li></ul></div>
        <div class="section weaknesses"><h4>⚠️ 待提升</h4><ul><li v-for="w in report.weaknesses">{{ w }}</li></ul></div>
      </div>

      <div v-if="report?.improvementSuggestions" class="sections">
        <div class="section suggestions"><h4>📌 改进建议</h4><ul><li v-for="s in report.improvementSuggestions">{{ s }}</li></ul></div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/api'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const report = ref<any>(null)

async function load() {
  loading.value = true
  const res = await api.get(`/api/portal/interviews/${route.params.id}/report`)
  if (res.data.code === 0) report.value = res.data.data
  loading.value = false
}

function formatDuration(sec?: number) {
  if (!sec) return '-'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}分${s}秒`
}

function getScoreColor(score: number) {
  if (score >= 7) return '#67c23a'
  if (score >= 5) return '#e6a23c'
  return '#f56c6c'
}

function dimName(key: string) {
  const map: Record<string, string> = { knowledgeDepth: '知识深度', systemDesign: '系统设计', problemSolving: '问题解决', communication: '沟通能力', practicalExperience: '实战经验', englishAbility: '英语能力' }
  return map[key] || key
}

onMounted(load)
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 20px; }
.page-header { margin-bottom: 16px; }
.report-card { }
.score-overview { display: flex; align-items: center; gap: 40px; margin-bottom: 24px; padding: 20px; background: #f5f7fa; border-radius: 8px; }
.total-score { text-align: center; }
.score-num { font-size: 48px; font-weight: bold; color: #409eff; }
.score-label { font-size: 14px; color: #909399; }
.duration { font-size: 18px; color: #606266; }
.sections { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-top: 16px; }
.section h4 { margin-bottom: 8px; }
.section ul { padding-left: 20px; }
.section li { margin-bottom: 4px; font-size: 14px; }
</style>
