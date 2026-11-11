import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface AdminInfo {
  userId: number
  username: string
  roleCode: string
  roleName: string
  permissions: string[]
  avatarUrl?: string
  needChangePassword: boolean
}

export const useAdminStore = defineStore('admin', () => {
  const info = ref<AdminInfo | null>(null)
  const isLoggedIn = ref(false)

  function setInfo(adminInfo: AdminInfo) {
    info.value = adminInfo
    isLoggedIn.value = true
  }

  function hasPermission(permission: string): boolean {
    if (!info.value) return false
    if (info.value.permissions.includes('*')) return true
    return info.value.permissions.includes(permission)
  }

  function logout() {
    info.value = null
    isLoggedIn.value = false
  }

  return { info, isLoggedIn, setInfo, hasPermission, logout }
})
