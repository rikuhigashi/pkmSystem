import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logoutAPI, register } from '@/API/login/loginAPI'
import apiClient from '@/API/axios'
import { useRouter } from 'vue-router'
import type { UserInfo } from '@/views/user/types/auth'
import {getUserKnowledgeIds} from "@/API/knowledge/knowledgeAPI";

// auth store
export const userAuthStore = defineStore('auth', () => {
  const router = useRouter()

  const isLoggedIn = ref(false)

  const userInfo = ref<UserInfo | null>(null)

  const purchasedKnowledges = ref<number[]>([])

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
          role: res.data.role,
          balance: res.data.balance,
        }

        // 获取用户知识权限ID
        await fetchUserKnowledgeIds()

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

  // 获取用户知识权限ID
  const fetchUserKnowledgeIds = async () => {
    try {
      const ids = await getUserKnowledgeIds()
      purchasedKnowledges.value = ids
    } catch (error) {
      console.error('获取用户知识ID失败:', error)
      purchasedKnowledges.value = []
    }
  }

  // const loginUser = async (email: string, password: string) => {
  //   try {
  //     const res = await login({ email, password })
  //     if (res.data) {
  //       isLoggedIn.value = true
  //       userInfo.value = {
  //         id: res.data.id,
  //         username: res.data.username,
  //         email: res.data.email,
  //         vipActive: res.data.vipActive,
  //         role: res.data.role,
  //         balance: res.data.balance,
  //       }
  //
  //       // 新增：获取用户已购买的知识ID
  //       await fetchPurchasedKnowledges()
  //
  //       // 根据角色跳转
  //       if (res.data.role === 'ADMIN') {
  //         await router.replace({ name: 'adminDashboard' })
  //       } else {
  //         await router.replace({ name: 'home' })
  //       }
  //     }
  //     return res
  //   } catch (error) {
  //     console.error(error)
  //
  //     throw new Error('登录失败')
  //   }
  // }

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
  // const registerUser = async (data: {
  //   email: string
  //   password: string
  //   username: string
  //   confirmPassword: string
  //   code: string
  // }) => {
  //   try {
  //     const res = await register(data)
  //     if (res.success) {
  //       await router.replace('/')
  //     }
  //     return res
  //   } catch (error) {
  //     console.error(error)
  //     throw new Error('注册失败')
  //   }
  // }

 // 登出方法
  const logout = async () => {
    try {
      await logoutAPI()
      localStorage.removeItem('authToken')
      isLoggedIn.value = false
      userInfo.value = null
      purchasedKnowledges.value = [] // 清空购买记录
      await router.replace({ name: 'login' })
    } catch (error) {
      console.error('登出失败:', error)
      // 强制清除本地状态
      localStorage.removeItem('authToken')
      isLoggedIn.value = false
      userInfo.value = null
    }
  }
  // const logout = async () => {
  //   try {
  //     await logoutAPI()
  //     localStorage.removeItem('authToken')
  //     isLoggedIn.value = false
  //     userInfo.value = null
  //     purchasedKnowledges.value = [] // 清空购买记录
  //     await router.replace({ name: 'login' })
  //   } catch (error) {
  //     console.error('登出失败:', error)
  //     // 强制清除本地状态
  //     localStorage.removeItem('authToken')
  //     isLoggedIn.value = false
  //     userInfo.value = null
  //   }
  // }

  // 检查会话状态
  const checkSession = async () => {
    try {
      const token = localStorage.getItem('authToken')
      if (!token) {
        resetAuthState()
        return
      }

      const res = await apiClient.get('/api/auth/checkSession')
      if (res.data.isAuthenticated) {
        isLoggedIn.value = true
        await fetchUserInfo()
      } else {
        resetAuthState()
      }
    } catch (error) {
      resetAuthState()
      console.error('会话检查失败:', error)
    }
  }
  // const checkSession = async () => {
  //   try {
  //     const token = localStorage.getItem('authToken')
  //     if (!token) {
  //       // isLoggedIn.value = false
  //       // userInfo.value = null
  //       // purchasedKnowledges.value = []
  //       resetAuthState()
  //       return
  //     }
  //
  //     const res = await apiClient.get('/api/auth/checkSession')
  //     if (res.data.isAuthenticated) {
  //       isLoggedIn.value = true
  //       // userInfo.value = {
  //       //   id: res.data.userInfo.id,
  //       //   username: res.data.userInfo.username,
  //       //   email: res.data.userInfo.email,
  //       //   vipActive: res.data.userInfo.vipActive,
  //       //   balance: res.data.userInfo.balance,
  //       // }
  //       await fetchPurchasedKnowledges()
  //     } else {
  //       // localStorage.removeItem('authToken')
  //       // isLoggedIn.value = false
  //       // userInfo.value = null
  //       // purchasedKnowledges.value = []
  //       resetAuthState()
  //     }
  //   } catch (error) {
  //     localStorage.removeItem('authToken')
  //     isLoggedIn.value = false
  //     userInfo.value = null
  //     purchasedKnowledges.value = []
  //     console.error('会话检查失败:', error)
  //   }
  // }

  // 重置认证状态
  const resetAuthState = () => {
    localStorage.removeItem('authToken')
    isLoggedIn.value = false
    userInfo.value = null
    purchasedKnowledges.value = []
  }

  // 获取用户已购买的知识ID列表
  const fetchPurchasedKnowledges = async () => {
    try {
      if (!isLoggedIn.value) return

      const res = await apiClient.get('/user/info')
      userInfo.value = res.data

      const purchasedRes = await apiClient.get('/user/purchased-knowledge-ids')
      purchasedKnowledges.value = purchasedRes.data
    } catch (error) {
      console.error('获取已购知识ID失败:', error)
    }
  }

  // 检查知识购买状态
  const hasPurchasedKnowledge = (knowledgeId: number) => {
    return purchasedKnowledges.value.includes(knowledgeId)
  }

  // 添加已购知识
  const addPurchasedKnowledge = (knowledgeId: number) => {
    if (!purchasedKnowledges.value.includes(knowledgeId)) {
      purchasedKnowledges.value.push(knowledgeId)
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
      const res = await apiClient.get('/user/info')
      userInfo.value = {
        id: res.data.id,
        username: res.data.username,
        email: res.data.email,
        vipActive: res.data.vipActive,
        balance: res.data.balance,
        vipExpireDate: res.data.vipExpireDate,
        role: res.data.role,
      }

      // 同时获取知识权限ID
      await fetchUserKnowledgeIds()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  // const fetchUserInfo = async () => {
  //   try {
  //     const res = await apiClient.get('/user/info')
  //     userInfo.value = res.data
  //
  //     // 获取用户知识权限ID列表
  //     purchasedKnowledges.value = await getUserKnowledgeIds()
  //   } catch (error) {
  //     console.error('获取用户信息失败:', error)
  //   }
  // }
  // const fetchUserInfo = async () => {
  //   try {
  //     const res = await apiClient.get('/user/info')
  //     userInfo.value = res.data
  //     // userInfo.value = {
  //     //   id: res.data.id,
  //     //   username: res.data.username,
  //     //   email: res.data.email,
  //     //   vipActive: res.data.vipActive,
  //     //   balance: res.data.balance,
  //     //   vipExpireDate: res.data.vipExpireDate,
  //     //   role: res.data.role,
  //     // }
  //
  //     // 同时获取已购知识ID
  //     await fetchPurchasedKnowledges()
  //   } catch (error) {
  //     console.error('获取用户信息失败:', error)
  //   }
  // }

  return {
    isLoggedIn,
    userInfo,
    purchasedKnowledges,
    loginUser,
    registerUser,
    logout,
    checkSession,
    fetchUserInfo,
    updateBalance,
    fetchPurchasedKnowledges,
    addPurchasedKnowledge,
    hasPurchasedKnowledge,
    updateUserInfo,
  }
})
