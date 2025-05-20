import apiClient from '@/API/axios'
import type {AxiosError} from 'axios'

// 获取全部数据信息
export const getAllUsersWithSideData = async () => {
  try {
    const res = await apiClient.get('/admin/users')
    return {success: true, data: res.data}
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '获取用户列表失败，请检查网络连接',
    }
  }
}

// 获取所有待审核数据
export const getPendingData = async (params?: {
  keyword?: string
  page: number
  size: number
  status?: number
}) => {
  try {
    const res = await apiClient.get('/admin/sidedata/pending', {
      params: {
        ...params,
        page: params?.page ? params.page - 1 : 0,
      },
    })
    return {success: true, data: res.data}
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '获取待审核数据失败',
    }
  }
}



// 管理员批准数据
export const approveData = async (id: number) => {
  try {
    const res = await apiClient.put(`/admin/sidedata/${id}/approve`)
    return {success: true, data: res.data}
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '批准操作失败',
    }
  }
}

// 管理员拒绝数据
export const rejectData = async (id: number, reason: string) => {
  try {
    const res = await apiClient.put(`/admin/sidedata/${id}/reject`, { reason })
    return {success: true, data: res.data}
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '拒绝操作失败',
    }
  }
}


// 获取通知
export const getNotifications = async () => {
  try {
    const res = await apiClient.get('/admin/user/notifications')
    return { success: true, data: res.data }
  } catch (error) {
    const axiosError = error as AxiosError<{ message: string }>
    return {
      success: false,
      error: axiosError.response?.data || '获取通知失败',
    }
  }
}
