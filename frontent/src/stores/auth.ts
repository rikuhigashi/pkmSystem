import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logoutAPI, register } from '@/API/login/loginAPI'
import apiClient from '@/API/axios'
import { useRouter } from 'vue-router'
import type { UserInfo } from '@/views/user/types/auth'

// auth store
export const userAuthStore = defineStore('auth', () => {
  const router = useRouter()

  const isLoggedIn = ref(false)

  // const userInfo = ref<{ username?: string }>({})

  const userInfo = ref<UserInfo | null>(null)

  // 登录方法
  const loginUser = async (email: string, password: string) => {
    try {
      const res = await login({ email, password })

      if (res.data.role == 'ADMIN') {
        isLoggedIn.value = true
        // userInfo.value = res.data.username
        userInfo.value = {
          id: res.data.id,
          username: res.data.username,
          email: res.data.email,
        }
        await router.replace('/adminDashboard')
      } else {
        isLoggedIn.value = true
        // userInfo.value = res.data.username
        userInfo.value = {
          id: res.data.id,
          username: res.data.username,
          email: res.data.email,
        }

        // 不是管理员，重定向到普通用户页面
        await router.replace('/home')
      }
      return res
    } catch (error) {
      console.error(error)

      throw new Error('登录失败')
    }
  }

  // 注册方法
  const registerUser = async (data: {
    email: string
    password: string
    username: string
    confirmPassword: string
    code: string
  }) => {
    try {
      const res = await register(data)
      if (res.success) {
        await router.replace('/')
      }
      return res
    } catch (error) {
      console.error(error)
      throw new Error('注册失败')
    }
  }

  // 登出方法
  const logout = async () => {
    try {
      await logoutAPI()
      // await apiClient.post('/api/auth/logout')
      isLoggedIn.value = false
      userInfo.value = null
      // localStorage.removeItem('token')
      // sessionStorage.clear()
      await router.replace({ name: 'login' })
    } catch (error) {
      console.error('登出失败:', error)
    }
  }

  // 检查会话状态
  const checkSession = async () => {
    try {
      const res = await apiClient.get('/api/auth/checkSession')
      isLoggedIn.value = res.data.isAuthenticated
      // userInfo.value = res.data.userInfo || {}
      userInfo.value = res.data.userInfo
        ? {
            id: res.data.userInfo.id,
            username: res.data.userInfo.username,
            email: res.data.userInfo.email,
          }
        : null
    } catch (error) {
      isLoggedIn.value = false
      console.error(error)
    }
  }

  //更新user数据
  const updateUserInfo = async (payload: Partial<UserInfo>) => {
    try {
      const userId = userInfo.value?.id;
      if (!userId) throw new Error('用户未登录');

      const response = await apiClient.put(`/user/${userId}`, payload);
      userInfo.value = {
        ...userInfo.value!,
        ...response.data,
      }
      return response.data
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw new Error('更新用户信息失败')
    }
  }

  return { isLoggedIn, userInfo, loginUser, registerUser, logout, checkSession, updateUserInfo }
})
