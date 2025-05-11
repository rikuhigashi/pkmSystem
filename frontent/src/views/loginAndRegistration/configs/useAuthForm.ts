import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userAuthStore } from '@/stores/auth'

interface AuthForm {
  email: string
  password: string
}

interface RegisterForm extends AuthForm {
  username: string
  confirmPassword: string
  code: string
}

interface resetPasswordForm extends AuthForm {
  newPassword: string
  confirmPassword: string
  code: string
}

export function useAuthForm() {
  const router = useRouter()
  const authStore = userAuthStore()


  // 表单数据
  const formData = ref<AuthForm>({ email: '', password: '' })
  const registerFormData = ref<RegisterForm>({
    email: '',
    password: '',
    username: '',
    confirmPassword: '',
    code: '',
  })
  const resetPasswordFormData = ref<resetPasswordForm>({
    email: '',
    password: '',
    newPassword: '',
    confirmPassword: '',
    code: '',
  })


  // UI状态
  const isLoading = ref(false)
  const errorMessage = ref<string>('')

  // -------------------- 基础表单操作 --------------------

  // const handleBasicAuth = async (url: '/login' | '/register') => {
  //   isLoading.value = true
  //
  //   try {
  //     // 登录
  //     if (url === '/login') {
  //       const res = await login(formData.value)
  //
  //       authStore.isLoggedIn = true;
  //       authStore.userInfo = { username: res.data.username }
  //
  //
  //       // await router.replace('/dashboard')
  //     }
  //     //   注册
  //     else {
  //       await register(registerFormData.value)
  //     }
  //   } catch (error) {
  //     if (error instanceof Error) {
  //       errorMessage.value = error.message
  //     } else if (typeof error === 'string') {
  //       errorMessage.value = error
  //     } else {
  //       errorMessage.value = '认证失败，请重试'
  //     }
  //     throw error
  //   } finally {
  //     isLoading.value = false
  //   }
  // }

  // -------------------- 第三方登录 --------------------
  // const initOAuthFlow = async (provider: 'google' | 'github') => {
  //   const width = 500
  //   const height = 600
  //   const left = (screen.width - width) / 2
  //   const top = (screen.height - height) / 2
  //
  //   window.open(
  //     `${import.meta.env.VITE_API_BASE}/auth/${provider}`,
  //     'OAuthPopup',
  //     `width=${width},height=${height},top=${top},left=${left}`,
  //   )
  //
  //   // 监听弹出窗口的消息
  //   window.addEventListener('message', (event) => {
  //     if (event.origin !== import.meta.env.VITE_API_BASE) return
  //     if (event.data.token) {
  //       localStorage.setItem('authToken', event.data.token)
  //       router.push('/dashboard')
  //     }
  //   })
  // }

  // -------------------- 统一登出 --------------------
  // const handleLogout = async () => {
  //   await apiClient.post('/api/auth/logout');
  //   authStore.isLoggedIn = false
  //   await router.replace({ name: 'login' })
  // }

  return {
    formData,
    registerFormData,
    resetPasswordFormData,
    isLoading,
    errorMessage,
    authStore
  }
}
