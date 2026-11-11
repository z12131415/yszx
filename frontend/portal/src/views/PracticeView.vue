<template>
  <div class="practice-container">
    <el-row :gutter="20">
      <!-- 左侧题目列表 -->
      <el-col :span="6">
        <el-card shadow="never" class="question-nav">
          <template #header><span>题目导航 ({{ currentIndex + 1 }}/{{ total }})</span></template>
          <div class="nav-grid">
            <div
              v-for="(q, i) in questions"
              :key="q.id"
              :class="['nav-item', { active: i === currentIndex, answered: answers[i], correct: answers[i]?.isCorrect }]"
              @click="goToQuestion(i)"
            >
              {{ i + 1 }}
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧答题区 -->
      <el-col :span="18">
        <el-card shadow="never" class="answer-card" v-loading="loading">
          <template #header>
            <div class="answer-header">
              <span>第 {{ currentIndex + 1 }} 题 / 共 {{ total }} 题</span>
              <el-tag>{{ currentQ?.difficulty }}</el-tag>
            </div>
          </template>

          <div class="question-text">{{ currentQ?.content }}</div>

          <!-- 选择题选项 -->
          <div v-if="currentQ?.options" class="options">
            <el-radio-group v-model="selectedOption" class="option-group">
              <el-radio v-for="opt in currentQ.options" :key="opt.label" :value="opt.label" size="large">
                <span class="opt-label">{{ opt.label }}</span>
                <span class="opt-content">{{ opt.content }}</span>
              </el-radio>
            </el-radio-group>
          </div>

          <!-- 简答题 -->
          <div v-else-if="currentQ?.type === 'essay'" class="essay-box">
            <el-input v-model="essayAnswer" type="textarea" :rows="5" placeholder="请输入你的答案..." />
          </div>

          <div class="answer-actions">
            <el-button :disabled="currentIndex === 0" @click="prevQuestion">上一题</el-button>
            <el-button type="primary" @click="submitAnswer" :loading="submitting">提交答案</el-button>
            <el-button type="success" :disabled="currentIndex >= total - 1" @click="nextQuestion">下一题</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { usePracticeStore } from '@/stores/usePracticeStore'

const route = useRoute()
const practiceStore = usePracticeStore()
const sessionId = route.params.sessionId as string

const loading = ref(false)
const submitting = ref(false)
const currentIndex = ref(0)
const selectedOption = ref('')
const essayAnswer = ref('')
const questions = ref<any[]>([])
const answers = ref<Record<number, any>>({})

const total = computed(() => questions.value.length)
const currentQ = computed(() => questions.value[currentIndex.value])

async function loadSession() {
  loading.value = true
  try {
    const res = await api.get(`/api/portal/practice/${sessionId}`)
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      questions.value = data.questions || []
      currentIndex.value = data.currentQuestionIndex ?? 0
      Object.keys(data.answers || {}).forEach(k => {
        answers.value[parseInt(k)] = data.answers[k]
      })
    }
  } catch {
    ElMessage.error('加载练习会话失败')
  } finally {
    loading.value = false
  }
}

async function submitAnswer() {
  if (!currentQ.value) return
  const qId = currentQ.value.id
  if (answers.value[qId]) { ElMessage.warning('该题已作答'); return }

  submitting.value = true
  try {
    const payload = currentQ.value.type === 'essay'
      ? { questionId: qId, answer: essayAnswer.value }
      : { questionId: qId, selectedOptions: [selectedOption.value] }

    const url = currentQ.value.type === 'essay'
      ? `/api/portal/practice/${sessionId}/score`
      : `/api/portal/practice/${sessionId}/answer`

    const method = currentQ.value.type === 'essay' ? api.post : api.put
    const res = await method(url, payload)
    if (res.data.code === 0) {
      answers.value[qId] = res.data.data
      ElMessage.success(res.data.data.isCorrect ? '答对了！' : '答错了，继续努力')
      if (!res.data.data.hasNext) {
        ElMessage.success('练习已完成！')
      }
    }
  } finally {
    submitting.value = false
  }
}

function prevQuestion() { if (currentIndex.value > 0) currentIndex.value-- }
function nextQuestion() { if (currentIndex.value < total.value - 1) currentIndex.value++ }
function goToQuestion(i: number) { currentIndex.value = i }

onMounted(loadSession)
</script>

<style scoped>
.practice-container { max-width: 1200px; margin: 0 auto; padding: 20px; }
.question-nav .nav-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; }
.nav-item {
  width: 36px; height: 36px; display: flex; align-items: center; justify-content: center;
  border-radius: 50%; cursor: pointer; font-size: 13px; border: 1px solid #dcdfe6;
}
.nav-item.active { background: #409eff; color: #fff; border-color: #409eff; }
.nav-item.answered { background: #e1f3d8; border-color: #67c23a; }
.nav-item.correct { background: #67c23a; color: #fff; }
.answer-header { display: flex; justify-content: space-between; align-items: center; }
.question-text { font-size: 16px; line-height: 1.8; margin-bottom: 20px; }
.option-group { display: flex; flex-direction: column; gap: 12px; }
.opt-label { font-weight: bold; margin-right: 8px; color: #409eff; }
.essay-box { margin-bottom: 20px; }
.answer-actions { display: flex; gap: 12px; margin-top: 20px; }
</style>
