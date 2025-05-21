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
    // 移除所有正则处理，直接使用原始Token
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
// apiClient.interceptors.request.use((config) => {
//   const token = localStorage.getItem('authToken')
//   if (token) {
//     // 清理可能存在的特殊字符并标准化Bearer格式
//     const cleanedToken = token.replace(/^"(.*)"$/, '$1') // 处理可能存在的JSON字符串引号
//     config.headers.Authorization = `Bearer ${cleanedToken.trim()}`
//   }
//   return config
// }, (error) => {
//   return Promise.reject(error)
// })



// apiClient.interceptors.request.use((config) => {
//   const token = localStorage.getItem('authToken')
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config
// })




export default apiClient
