import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, register } from '@/API/login/loginAPI'
import apiClient from '@/API/axios'
import { useRouter } from 'vue-router'

// auth store
export const userAuthStore = defineStore('auth', () => {
  const router = useRouter()

  const isLoggedIn = ref(false)

  const userInfo = ref<{ username?: string }>({})

  // 登录方法
  const loginUser = async (email: string, password: string) => {
    try {
      const res = await login({ email, password })

      if (res.data.role == 'ADMIN') {
        isLoggedIn.value = true
        userInfo.value = res.data.username
        await router.replace('/adminDashboard')
      } else {
        isLoggedIn.value = true
        userInfo.value = res.data.username
        // 不是管理员，重定向到普通用户页面
        await router.replace('/home')
      }

      // if (res.success ) {
      //   isLoggedIn.value = true
      //   userInfo.value =  res.data.username
      //   await router.replace('/home')
      // }

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
      await apiClient.post('/api/auth/logout')
      isLoggedIn.value = false
      userInfo.value = {}
      await router.replace('/')
    } catch (error) {
      console.error('登出失败:', error)
    }
  }

  // 检查会话状态
  const checkSession = async () => {
    try {
      const res = await apiClient.get('/api/auth/checkSession')
      isLoggedIn.value = res.data.isAuthenticated
      userInfo.value = res.data.userInfo || {}
    } catch (error) {
      isLoggedIn.value = false
      console.error(error)
    }
  }

  // 初始化时从 localStorage 恢复用户信息
  // const storedUser = localStorage.getItem('userInfo')
  // if (storedUser) {
  //   try {
  //     userInfo.value = JSON.parse(storedUser)
  //   } catch (e) {
  //     console.error('解析 userInfo 失败:', e)
  //     localStorage.removeItem('userInfo')  // 清理无效数据
  //   }
  // }

  return { isLoggedIn, userInfo, loginUser, registerUser, logout, checkSession }
})
