// src/API/knowledgeAPI.ts
import apiClient from '@/API/axios'
import type { AxiosError } from 'axios'

// 知识详情接口
export interface KnowledgeDetail {
  id: number
  title: string
  authorId: number
  authorName: string
  createdAt: string
  price: number
  encrypted: boolean
  purchased: boolean
  tags: string[]
  content: any
}

// 知识列表项接口
export interface KnowledgeItem {
  id: number
  title: string
  authorId: number
  authorName: string
  price: number
  encrypted: boolean
  purchased: boolean
  tags: string[]
  createdAt: string       // 创建时间字段
  purchasedAt?: string    // 购买时间字段
}

// 创建知识请求参数
export interface CreateKnowledgeRequest {
  title: string
  content: string
  encrypted: boolean
  price: number
  tags: string[]
}

interface ErrorResponse {
  message: string;
}

/**
 * 搜索知识列表
 * @param query 搜索关键词
 * @param page 页码（从0开始）
 * @param size 每页数量
 * @param sortField
 * @param sortDirection
 */
export const searchKnowledge = async (
  query?: string,
  page: number = 0,
  size: number = 10,
  sortField: string = 'createdAt',
  sortDirection: string = 'DESC'
) => {
  try {
    const params = {
      query,
      page,
      size,
      sortField,
      sortDirection
    }

    const res = await apiClient.get('/api/knowledge/search', { params })
    return res.data
  } catch (error) {
    const axiosError = error as AxiosError<ErrorResponse>
    throw new Error(axiosError.response?.data?.message || '搜索知识失败')
  }
}

/**
 * 获取知识详情
 * @param id 知识ID
 */
export const getKnowledgeDetail = async (id: number): Promise<KnowledgeDetail> => {
  try {
    const res = await apiClient.get(`/api/knowledge/${id}`)
    return res.data
  } catch (error) {
    const axiosError = error as AxiosError<ErrorResponse>
    throw new Error(axiosError.response?.data?.message || '获取知识详情失败')
  }
}

/**
 * 购买知识
 * @param id 知识ID
 */
export const purchaseKnowledge = async (id: number) => {
  try {
    const res = await apiClient.post(`/api/knowledge/${id}/purchase`)
    return res.data.newBalance
  } catch (error) {
    const axiosError = error as AxiosError<ErrorResponse>
    throw new Error(axiosError.response?.data?.message || '购买知识失败')
  }
}

/**
 * 获取已购知识列表
 */
export const getPurchasedKnowledge = async (): Promise<KnowledgeItem[]> => {
  try {
    const res = await apiClient.get('/api/knowledge/purchased')
    return res.data.content
  } catch (error) {
    const axiosError = error as AxiosError<ErrorResponse>
    throw new Error(axiosError.response?.data?.message || '获取已购知识失败')
  }
}

/**
 * 获取我分享的知识列表
 */
export const getMyKnowledge = async (): Promise<KnowledgeItem[]> => {
  try {
    const res = await apiClient.get('/api/knowledge/my')
    return res.data.content
  } catch (error) {
    const axiosError = error as AxiosError<ErrorResponse>
    throw new Error(axiosError.response?.data?.message || '获取分享知识失败')
  }
}

/**
 * 创建知识
 * @param data 知识数据
 */
export const createKnowledge = async (data: CreateKnowledgeRequest) => {
  try {
    const res = await apiClient.post('/api/knowledge', data)
    return res.data
  } catch (error) {
    const axiosError = error as AxiosError<ErrorResponse>
    throw new Error(axiosError.response?.data?.message || '创建知识失败')
  }
}


// 获取用户知识权限ID列表
export const getUserKnowledgeIds = async (): Promise<number[]> => {
  try {
    const res = await apiClient.get('/api/knowledge/user-knowledge-ids')
    return res.data
  } catch (error) {
    console.error('获取用户知识ID失败:', error)
    return []
  }
}
