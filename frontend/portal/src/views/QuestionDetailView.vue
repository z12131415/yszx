<template>
  <div class="container">
    <el-page-header @back="router.push('/')" content="题目详情" class="page-header" />
    <el-card v-loading="loading" class="detail-card">
      <template #header>
        <div class="header-row">
          <el-tag :type="difficultyType(q?.difficulty)">{{ q?.difficulty }}</el-tag>
          <el-tag type="info">{{ q?.type }}</el-tag>
          <span class="catalog-info">{{ q?.subjectName }} / {{ q?.catalogName }}</span>
        </div>
      </template>

      <div class="question-content" v-if="q">{{ q.content }}</div>

      <!-- 选项（选择题/多选题/判断题） -->
      <div v-if="(q.type === 'single_choice' || q.type === 'multiple_choice' || q.type === 'judge') && q.options" class="options">
        <el-radio-group v-model="selectedOptions" class="option-list" :disabled="answered">
          <el-radio v-for="opt in q.options" :key="opt.label" :value="opt.label" :label="opt.label" class="option-item">
            <span class="opt-label">{{ opt.label }}.</span>
            <span class="opt-content">{{ opt.content }}</span>
            <el-tag v-if="showAnswer && opt.isCorrect" type="success" size="small">正确答案</el-tag>
          </el-radio>
        </el-radio-group>
      </div>

      <!-- 简答题 -->
      <div v-else-if="q.type === 'essay'" class="essay-area">
        <el-input v-model="userAnswer" type="textarea" :rows="6" placeholder="请输入你的答案..." :disabled="answered" />
      </div>

      <div class="actions" v-if="!answered">
        <el-button type="primary" size="large" :loading="submitting" @click="submitAnswer">提交答案</el-button>
      </div>

      <!-- 解析 -->
      <el-divider v-if="q.analysis || showAnswer" />
      <div v-if="showAnswer && q.answer" class="answer-section">
        <h4>正确答案</h4>
        <p>{{ q.answer }}</p>
      </div>
      <div v-if="q.analysis" class="analysis-section">
        <h4>解析</h4>
        <p>{{ q.analysis }}</p>
      </div>

      <div class="stats" v-if="q">
        <span>正确率：{{ q.correctRate ? (q.correctRate * 100).toFixed(0) + '%' : '-' }}</span>
        <span>作答人数：{{ q.answerCount || 0 }}</span>
        <span v-if="q.hasAnswered" style="color: var(--success-color)">✓ 已作答</span>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const q = ref<any>(null)
const selectedOptions = ref<string[]>([])
const userAnswer = ref('')
const answered = ref(false)
const showAnswer = ref(false)
const submitting = ref(false)

async function load() {
  loading.value = true
  try {
    const res = await api.get(`/api/portal/questions/${route.params.id}`)
    if (res.data.code === 0) {
      q.value = res.data.data
      if (q.value?.hasAnswered) {
        answered.value = true
        showAnswer.value = true
      }
    }
  } catch {
    ElMessage.error('加载题目失败')
  } finally {
    loading.value = false
  }
}

async function submitAnswer() {
  if (!selectedOptions.value.length && !userAnswer.value.trim()) {
    ElMessage.warning('请先选择或输入答案')
    return
  }
  submitting.value = true
  try {
    // TODO: 调用答题接口
    answered.value = true
    showAnswer.value = true
    ElMessage.success('答案已提交')
  } finally {
    submitting.value = false
  }
}

function difficultyType(d: string) {
  return { easy: 'success', medium: '', hard: 'danger' }[d] || ''
}

load()
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 20px; }
.page-header { margin-bottom: 16px; }
.detail-card { }
.header-row { display: flex; align-items: center; gap: 8px; }
.catalog-info { margin-left: auto; color: #909399; font-size: 13px; }
.question-content { font-size: 16px; line-height: 1.8; color: #303133; margin-bottom: 20px; }
.options { margin-bottom: 20px; }
.option-list { display: flex; flex-direction: column; gap: 12px; }
.option-item { display: flex; align-items: flex-start; gap: 8px; }
.opt-label { font-weight: bold; color: #409eff; min-width: 20px; }
.essay-area { margin-bottom: 20px; }
.actions { margin-bottom: 20px; }
.answer-section, .analysis-section { margin-top: 12px; }
.answer-section h4, .analysis-section h4 { margin-bottom: 8px; color: #409eff; }
.stats { display: flex; gap: 24px; color: #909399; font-size: 13px; margin-top: 16px; }
</style>
