import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const userId = ref<number | null>(null)
  const nickname = ref<string>('')
  const avatarUrl = ref<string>('')
  const isLoggedIn = ref<boolean>(false)

  const userInfo = computed(() => ({
    userId: userId.value,
    nickname: nickname.value,
    avatarUrl: avatarUrl.value,
    isLoggedIn: isLoggedIn.value,
  }))

  async function fetchMe() {
    try {
      const res = await axios.get('/api/portal/auth/me')
      if (res.data.code === 0 && res.data.data) {
        userId.value = res.data.data.userId
        nickname.value = res.data.data.nickname || ''
        avatarUrl.value = res.data.data.avatarUrl || ''
        isLoggedIn.value = true
      }
    } catch {
      logout()
    }
  }

  function login(userIdVal: number, nicknameVal: string, avatarUrlVal?: string) {
    userId.value = userIdVal
    nickname.value = nicknameVal
    avatarUrl.value = avatarUrlVal || ''
    isLoggedIn.value = true
  }

  function logout() {
    userId.value = null
    nickname.value = ''
    avatarUrl.value = ''
    isLoggedIn.value = false
  }

  return { userId, nickname, avatarUrl, isLoggedIn, userInfo, fetchMe, login, logout }
})
