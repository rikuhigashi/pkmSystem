import apiClient from '@/API/axios'

export const getMainAllData = async () => {
  try {
    const res = await apiClient.get('mainData/all')
    return res.data
  } catch (error) {
    throw error
  }
}

export const getMainDataById = async (id: number) => {
  try {
    const res = await apiClient.get(`mainData/${id}`)
    return res.data
  } catch (error) {
    throw error
  }
}

export const saveMainData = async (data: Record<string, unknown>,id:number) => {
  try {
    const payLoad = { tiptapJson: data }
    const res = await apiClient.post(`mainData/save/${id}`, payLoad)


    console.log(res.data)
    // console.log('Request data:', JSON.stringify(payLoad, null, 2))
    return res.data
  } catch (error) {
    throw error
  }
}
