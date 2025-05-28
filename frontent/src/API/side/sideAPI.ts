import apiClient from '@/API/axios'
import type { sideListItem } from '@/views/side/types/sideTypes'
import { AxiosError } from 'axios'
// 获取全部数据
export const getSideAllData = async () => {
  try {
    const res = await apiClient.get(`/sideData/all`)
    return res.data
  } catch (error) {
    throw error
  }
}

export const getFullData = async (id: number) => {
  try {
    const res = await apiClient.get(`/sideData/full/${id}`)
    return res.data
  } catch (error) {
    throw error
  }
}

// 添加新数据
export const addSideData = async (data: unknown) => {
  try {
    const res = await apiClient.post('/sideData/add', data)
    return res.data
  } catch (error) {
    throw error
  }
}

// 更新侧边栏数据
export const upDataSideData = async (id: string | number, data: sideListItem) => {
  try {
    // console.log("editInputValue:",editInputValue)
    // console.log("Request data:", JSON.stringify(editInputValue,null,2));
    const res = await apiClient.patch(`/sideData/${id}`, data)
    return res.data
  } catch (error) {
    throw error
  }
}

// 更新主数据
export const upDataMainData = async (id: number, data: Record<string, unknown>) => {
  try {
    const payLoad = { tiptapJson: data }
    const res = await apiClient.patch(`sideData/${id}`, payLoad)

    return { success: true, data: res.data }
  } catch (error) {
    console.log(error)
  }
}

// 复制数据
export const apiCopySideData = async (id: string | number) => {
  try {
    const res = await apiClient.post(`/sideData/${id}/copy`)
    return res.data
  } catch (error) {
    throw error
  }
}

// 删除数据
export const deleteSideData = async (id: string | number) => {
  try {
    const res = await apiClient.delete(`/sideData/${id}`)
    return res.data
  } catch (error) {
    throw error
  }
}

// 上传图片
export const uploadImage = async (file: File) => {
  try {
    const formData = new FormData()
    formData.append('image', file)

    const res = await apiClient.post('/upload/image', formData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'multipart/form-data',
      },
      timeout: 15000, // 设置超时时间为15秒
    })

    return res.data.url
  } catch (error: unknown) {
    if (error instanceof AxiosError) {
      console.error('服务器响应错误:', {
        status: error.response?.status,
        data: error.response?.data,
      })
    } else if (error instanceof Error) {
      console.error('请求配置错误:', error.message)
    } else {
      console.error('未知错误:', error)
    }
    throw new Error('图片上传失败: ' + (error instanceof Error ? error.message : '未知错误'))
  }
}

// 删除图片
export const deleteImage = async (fileKey: string) => {
  try {
    const res = await apiClient.delete(`/upload/deleteImage`, {
      params: { fileKey } // 改为查询参数
    })
    return res.data
  } catch (error) {
    throw error
  }
}
