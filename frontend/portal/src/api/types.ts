export interface User {
  userId: number
  phone: string
  nickname: string
  gender?: string
  avatarUrl?: string
  status: number
  createdAt: string
}

export interface QuestionItem {
  id: number
  content: string
  type: 'single_choice' | 'multiple_choice' | 'judge' | 'essay'
  difficulty: 'easy' | 'medium' | 'hard'
  tags: string[]
  correctRate?: number
  answerCount?: number
  hasAnswered?: boolean
}

export interface QuestionDetail extends QuestionItem {
  catalogId: number
  catalogName: string
  subjectName: string
  options?: Array<{ label: string; content: string; isCorrect: boolean }>
  answer: string
  analysis?: string
}

export interface PageResult<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T | null
  fieldErrors?: Array<{ field: string; message: string }>
}

export interface LoginRequest {
  phone: string
  password: string
}

export interface RegisterRequest {
  phone: string
  email?: string
  nickname: string
  password: string
  passwordConfirm: string
  agreedTerms: boolean
  smsCode?: string
}
