<template>
  <div class="container">
    <el-page-header @back="router.push('/questions')" content="题库中心" class="page-header" />

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="学科">
          <el-select v-model="filters.subjectId" placeholder="全部学科" clearable @change="loadSubjects">
            <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段">
          <el-select v-model="filters.catalogId" placeholder="全部阶段" clearable>
            <el-option v-for="c in catalogs" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="filters.type" placeholder="全部题型" clearable>
            <el-option label="单选题" value="single_choice" />
            <el-option label="多选题" value="multiple_choice" />
            <el-option label="判断题" value="judge" />
            <el-option label="简答题" value="essay" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="filters.difficulty" placeholder="全部难度" clearable>
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.keyword" placeholder="搜索题干..." clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 题目列表 -->
    <el-card shadow="never" class="question-list">
      <div v-loading="loading" class="question-grid">
        <el-empty v-if="!loading && questions.length === 0" description="暂无题目" />
        <el-card
          v-for="q in questions"
          :key="q.id"
          shadow="hover"
          class="question-card"
          @click="goToQuestion(q.id)"
        >
          <div class="q-header">
            <el-tag :type="difficultyType(q.difficulty)" size="small">{{ q.difficulty }}</el-tag>
            <el-tag type="info" size="small">{{ q.type }}</el-tag>
            <el-tag v-if="q.hasAnswered" type="success" size="small">已答</el-tag>
          </div>
          <p class="q-content">{{ truncate(q.content, 80) }}</p>
          <div class="q-footer">
            <span v-if="q.correctRate !== undefined" class="rate">正确率 {{ (q.correctRate * 100).toFixed(0) }}%</span>
            <span v-if="q.answerCount" class="count">作答 {{ q.answerCount }}人</span>
            <div class="tags">
              <el-tag v-for="tag in q.tags" :key="tag" size="small" effect="plain">{{ tag }}</el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <el-pagination
        v-if="totalPages > 1"
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        class="pagination"
        @current-change="loadQuestions"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'
import type { QuestionItem } from '@/api/types'

const router = useRouter()
const loading = ref(false)
const questions = ref<QuestionItem[]>([])
const subjects = ref<any[]>([])
const catalogs = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = 20
const totalPages = ref(1)

const filters = ref({
  subjectId: null as number | null,
  catalogId: null as number | null,
  type: '' as string,
  difficulty: '' as string,
  keyword: '' as string,
})

async function loadSubjects() {
  // TODO: 从服务端加载学科列表
}

async function loadQuestions() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: page.value, pageSize }
    if (filters.value.subjectId) params.subjectId = filters.value.subjectId
    if (filters.value.catalogId) params.catalogId = filters.value.catalogId
    if (filters.value.type) params.type = filters.value.type
    if (filters.value.difficulty) params.difficulty = filters.value.difficulty
    if (filters.value.keyword) params.keyword = filters.value.keyword

    const res = await api.get('/api/portal/questions', { params })
    if (res.data.code === 0) {
      questions.value = res.data.data.items || []
      total.value = res.data.data.total || 0
      totalPages.value = res.data.data.totalPages || 1
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  loadQuestions()
}

function resetFilters() {
  filters.value = { subjectId: null, catalogId: null, type: '', difficulty: '', keyword: '' }
  handleSearch()
}

function goToQuestion(id: number) {
  router.push(`/questions/${id}`)
}

function truncate(text: string, len: number) {
  return text.length > len ? text.slice(0, len) + '...' : text
}

function difficultyType(d: string) {
  const map: Record<string, string> = { easy: 'success', medium: '', hard: 'danger' }
  return map[d] || ''
}

onMounted(loadQuestions)
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 20px; }
.page-header { margin-bottom: 16px; }
.filter-card { margin-bottom: 16px; }
.question-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }
.question-card { cursor: pointer; transition: transform 0.2s; }
.question-card:hover { transform: translateY(-2px); }
.q-header { display: flex; gap: 8px; margin-bottom: 8px; }
.q-content { font-size: 14px; color: #303133; line-height: 1.6; margin-bottom: 8px; }
.q-footer { display: flex; align-items: center; gap: 12px; font-size: 12px; color: #909399; }
.q-footer .rate, .q-footer .count { margin-right: auto; }
.tags { display: flex; gap: 4px; flex-wrap: wrap; }
.pagination { margin-top: 24px; text-align: center; }
</style>
