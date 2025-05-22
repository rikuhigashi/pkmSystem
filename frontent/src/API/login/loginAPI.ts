import apiClient from '@/API/axios'
import type { AxiosError } from 'axios'

// 登录
export const login = async (data: { email: string; password: string }) => {
  try {
    const res = await apiClient.post('/api/auth/login', data)
    localStorage.setItem('authToken', res.data.token)
    return {
      success: true,
      data: {
        ...res.data,
        role: res.data.role || 'USER' // 确保默认角色
      }
    }
  } catch (error) {
    const axiosError = error as AxiosError<{ message?: string }>
    return {
      success: false,
      error: axiosError.response?.data?.message || '登录失败，请检查凭证',
    }
  }
}

// ================= 注册 ==================
// 注册
export const register = async (data: {
  email: string
  password: string
  username: string
  code: string
}) => {
  try {
    const res = await apiClient.post('/api/auth/register', data)
    return { success: true, data: res.data }
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '注册失败，请检查网络连接',
    }
  }
}

// 发送验证码
export const sendVerificationCode = async (email: string) => {
  try {
    const res = await apiClient.post('/api/auth/send-verification', { email })

    return { success: true, data: res.data }
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '发送验证码失败，请检查网络连接',
    }
  }
}
// ================= 注册 ==================

// ================= 重置密码 ==================
// 发送重置密码的验证码
export const sendResetPasswordCode = async (data: { email: string }) => {
  try {
    const res = await apiClient.post('/api/auth/forgot-password', data)

    return { success: true, data: res.data }
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '发送验证码失败，请检查网络连接',
    }
  }
}

//    验证重置的验证码并生成令牌
export const verifyResetCode = async (data: { email: string; code: string }) => {
  try {
    const res = await apiClient.post('/api/auth/verify-reset-code', data)
    return { success: true, data: res.data }
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '验证码验证失败，请检查网络连接',
    }
  }
}

// 重置密码
export const resetPassword = async (data: { newPassword: string }) => {
  try {
    const token = localStorage.getItem("resetToken")
    const res = await apiClient.post('/api/auth/reset-password', data, {
      headers: {
        "Reset-Token": token
      }
    })

    // 清除 token
    localStorage.removeItem("resetToken")
    return { success: true, data: res.data }
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '验证码验证失败，请检查网络连接',
    }
  }
}

// ================= 重置密码 ==================

export const logoutAPI = async () => {
  try {
    localStorage.removeItem('authToken')
    await apiClient.post('/api/auth/logout')
  } catch (e) {
    console.error('退出错误:', e)
  }
}



