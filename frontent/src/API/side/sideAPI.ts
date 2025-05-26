import apiClient from '@/API/axios'
import type { sideListItem } from '@/views/side/types/sideTypes'

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


