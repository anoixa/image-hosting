import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { getCurrentUser, type UserInfo } from '@/api/auth/login'

interface UserState {
  token: string | null;
  userId: string | null;
  userName: string | null;
  userEmail: string | null;
  // userRole: string | null;
}

export const useUserStore = defineStore('user', () => {
  // 用户令牌
  const token = ref<string | null>(localStorage.getItem('token'))

  // 用户信息
  const userInfo = ref<UserInfo | null>(null)

  // 是否已加载用户信息
  const userInfoLoaded = ref(false)

  // 计算属性：是否已登录
  const isLoggedIn = computed(() => !!token.value)

  // 设置登录信息
  function setLoginInfo({ token: newToken }: { token: string }) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 加载用户信息
  async function loadUserInfo() {
    if (!token.value) return false

    try {
      const info = await getCurrentUser()
      userInfo.value = info
      userInfoLoaded.value = true
      return true
    } catch (error) {
      console.error('加载用户信息失败:', error)
      return false
    }
  }

  // 清除登录状态
  function clearLoginState() {
    token.value = null
    userInfo.value = null
    userInfoLoaded.value = false
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    userInfoLoaded,
    isLoggedIn,
    setLoginInfo,
    loadUserInfo,
    clearLoginState
  }
},
{
  persist: true
}
)
