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
      if (res.data) {
        isLoggedIn.value = true
        userInfo.value = {
          id: res.data.id,
          username: res.data.username,
          email: res.data.email,
          vipActive: res.data.vipActive,
          role: res.data.role
        }

        // 根据角色跳转
        if (res.data.role === 'ADMIN') {
          await router.replace({ name: 'adminDashboard' })
        } else {
          await router.replace({ name: 'home' })
        }
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
      localStorage.removeItem('authToken')
      isLoggedIn.value = false
      userInfo.value = null
      await router.replace({ name: 'login' })
    } catch (error) {
      console.error('登出失败:', error)
      // 强制清除本地状态
      localStorage.removeItem('authToken')
      isLoggedIn.value = false
      userInfo.value = null
    }
  }

  // 检查会话状态
  const checkSession = async () => {
    try {
      const token = localStorage.getItem('authToken')
      if (!token) {
        isLoggedIn.value = false
        userInfo.value = null
        return
      }

      const res = await apiClient.get('/api/auth/checkSession')
      if (res.data.isAuthenticated) {
        isLoggedIn.value = true
        userInfo.value = {
          id: res.data.userInfo.id,
          username: res.data.userInfo.username,
          email: res.data.userInfo.email,
          vipActive: res.data.userInfo.vipActive,
        }
      } else {
        localStorage.removeItem('authToken')
        isLoggedIn.value = false
        userInfo.value = null
      }
    } catch (error) {
      localStorage.removeItem('authToken')
      isLoggedIn.value = false
      userInfo.value = null
      console.error('会话检查失败:', error)
    }
  }

  //更新user数据
  const updateUserInfo = async (payload: Partial<UserInfo>) => {
    try {
      const userId = userInfo.value?.id
      if (!userId) throw new Error('用户未登录')

      const response = await apiClient.put(`/user/${userId}`, payload)
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

  // 更新用户余额
  const updateBalance = (newBalance: number) => {
    if (userInfo.value) {
      userInfo.value.balance = newBalance
    }
  }

  // 从后端获取用户信息
  const fetchUserInfo = async () => {
    try {
      const res = await apiClient.get('/user/info');
      userInfo.value = {
        id: res.data.id,
        username: res.data.username,
        email: res.data.email,
        vipActive: res.data.vipActive,
        balance: res.data.balance,
        vipExpireDate: res.data.vipExpireDate,
        role: res.data.role
      };
    } catch (error) {
      console.error('获取用户信息失败:', error);
    }
  };


  return { isLoggedIn, userInfo, loginUser, registerUser, logout, checkSession, updateUserInfo,updateBalance,fetchUserInfo }
})
