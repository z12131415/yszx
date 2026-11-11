import { defineStore } from 'pinia'
import { ref } from 'vue'

interface PracticeState {
  sessionId: string | null
  currentQuestionIndex: number
  totalQuestions: number
  mode: string
  answers: Record<string, { selected: string[]; scored: boolean; passed: boolean }>
  elapsedSeconds: number
}

export const usePracticeStore = defineStore('practice', () => {
  const state = ref<PracticeState>({
    sessionId: null,
    currentQuestionIndex: 0,
    totalQuestions: 0,
    mode: 'sequence',
    answers: {},
    elapsedSeconds: 0,
  })

  const currentQuestion = computed(() => state.value.currentQuestionIndex)
  const isFinished = computed(() => state.value.currentQuestionIndex >= state.value.totalQuestions)

  function setSession(sessionId: string, totalQuestions: number, mode: string) {
    state.value.sessionId = sessionId
    state.value.totalQuestions = totalQuestions
    state.value.mode = mode
    state.value.currentQuestionIndex = 0
    state.value.answers = {}
    state.value.elapsedSeconds = 0
  }

  function addAnswer(questionId: string, answer: { selected: string[]; scored: boolean; passed: boolean }) {
    state.value.answers[questionId] = answer
    state.value.currentQuestionIndex++
  }

  function tickElapsed(seconds: number) {
    state.value.elapsedSeconds += seconds
  }

  function reset() {
    state.value = { sessionId: null, currentQuestionIndex: 0, totalQuestions: 0, mode: 'sequence', answers: {}, elapsedSeconds: 0 }
  }

  return { state, currentQuestion, isFinished, setSession, addAnswer, tickElapsed, reset }
})
