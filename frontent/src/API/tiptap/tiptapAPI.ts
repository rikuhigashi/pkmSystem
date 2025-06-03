import apiClient from '@/API/axios'

export const createTiptapToken = async () => {
  try {
    const res = await apiClient.post('/api/tiptap/generate-token')
    return res.data
  } catch (error) {
    throw error
  }
}
