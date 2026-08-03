import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginVO } from '../api/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<LoginVO | null>(null)
  const token = ref<string>('')

  const setLogin = (info: LoginVO) => {
    userInfo.value = info
    token.value = info.token
    localStorage.setItem('token', info.token)
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const initFromStorage = () => {
    const t = localStorage.getItem('token')
    const info = localStorage.getItem('userInfo')
    if (!t || !info) return
    try {
      token.value = t
      userInfo.value = JSON.parse(info) as LoginVO
    } catch {
      // localStorage 中 userInfo 已损坏，清除会话避免影响路由守卫
      logout()
    }
  }

  const logout = () => {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const isAdmin = () => userInfo.value?.role === '管理员'
  const isUser = () => userInfo.value?.role === '用户'

  return { userInfo, token, setLogin, initFromStorage, logout, isAdmin, isUser }
})
