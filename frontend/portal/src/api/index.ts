import axios from 'axios'

const api = axios.create({
  baseURL: '',
  timeout: 30000,
  withCredentials: true,
})

// 请求拦截器：自动带上认证信息
api.interceptors.request.use(
  (config) => config,
  (error) => Promise.reject(error)
)

// 响应拦截器：统一错误处理
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        window.location.href = '/login'
      } else if (status === 429) {
        const msg = data?.message || '操作过于频繁，请稍后再试'
        console.warn(msg)
      }
    }
    return Promise.reject(error)
  }
)

export default api
