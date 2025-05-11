import apiClient from '@/API/axios'
import type { AxiosError } from 'axios'

// 登录
export const login = async (data: { email: string; password: string }) => {
  try {
    const res = await apiClient.post('/api/auth/login', {
      email: data.email,
      password: data.password,
    })

    return { success: true, data: res.data }
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '登录失败，请检查网络连接',
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
    const res = await apiClient.post('/api/auth/reset-password', data)



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


