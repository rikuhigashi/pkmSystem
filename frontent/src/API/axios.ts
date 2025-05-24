import axios from 'axios'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // 动态读取 baseURL

  // withCredentials: true,
  timeout: 5000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  },
})
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('authToken')
  if (token) {

    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default apiClient
