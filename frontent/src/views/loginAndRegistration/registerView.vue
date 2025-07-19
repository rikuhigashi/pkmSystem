<script setup lang="ts">
// ------------------- components -------------------
import AuthLayout from '@/components/loginAndRegistration/authLayout.vue'
import AuthButton from '@/components/loginAndRegistration/authButton.vue'
import InputField from '@/components/loginAndRegistration/InputField.vue'
import Alert from '@/components/appAlert.vue'
// ------------------- components -------------------

// ------------------- vue -------------------
import { computed, ref, watch } from 'vue'
// ------------------- vue -------------------

// ------------------- api -------------------
import { register, sendVerificationCode } from '@/API/login/loginAPI'
// ------------------- api -------------------

// ------------------- store -------------------
import { useAlertStore } from '@/stores/alert'
import { useAuthForm } from '@/views/loginAndRegistration/configs/useAuthForm'
import { useRouter } from 'vue-router'
import type { AxiosError } from 'axios'

const alertStore = useAlertStore()
const { registerFormData, isLoading } = useAuthForm()
// ------------------- store -------------------

const router = useRouter()
const countdown = ref(0)
const emailError = ref(false)
const codeError = ref(false)
const showVerificationField = ref(false)

// 邮箱验证正则
const emailRegex = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/

// 密码强度提示
const passwordHints = computed(() => {
  if (!registerFormData.value.password) return []
  return [
    { text: '至少8个字符', valid: registerFormData.value.password.length >= 8 },
    { text: '包含大写字母', valid: /[A-Z]/.test(registerFormData.value.password) },
    {
      text: '包含特殊字符（!@#$%^&*?.）',
      valid: /[!@#$%^&*?.]/.test(registerFormData.value.password),
    },
  ]
})

// 实时邮箱验证
watch(
  () => registerFormData.value.email,
  (email) => {
    emailError.value = !emailRegex.test(email)
  },
)

// 发送验证码
const handleSendCode = async () => {
  if (emailError.value || !registerFormData.value.email.trim()) {
    alertStore.showAlert('请输入有效的邮箱地址', 'error')
    return
  }

  alertStore.showLoading('验证码发送中...请稍后...')

  try {
    const res = await sendVerificationCode(registerFormData.value.email)
    if (res.success) {
      showVerificationField.value = true
      startCountdown()
      alertStore.showAlert('验证码已发送', 'success')
    }
  } catch (error) {
    const axiosError = error as AxiosError<{ message?: string }>
    alertStore.showAlert(axiosError.response?.data?.message || '发送失败，请稍后重试', 'error')
  }
}

const startCountdown = () => {
  countdown.value = 60
  const timer = setInterval(() => {
    if (countdown.value <= 0) clearInterval(timer)
    else countdown.value--
  }, 1000)
}

// 提交注册
const handleSubmit = async () => {
  const errors = {
    email: !emailRegex.test(registerFormData.value.email),
    code: !registerFormData.value.code,
    password: !passwordHints.value.every((hint) => hint.valid),
    confirm: registerFormData.value.password !== registerFormData.value.confirmPassword,
  }

  if (Object.values(errors).some(Boolean)) {
    alertStore.showAlert('请检查表单填写是否正确', 'error')
    return
  }

  try {
    isLoading.value = true
    const res = await register({
      email: registerFormData.value.email,
      password: registerFormData.value.password,
      username: registerFormData.value.username,
      code: registerFormData.value.code,
    })

    if (res.success) {
      alertStore.showAlert('注册成功，请登录', 'success')
      await router.push('/')
    }
  } catch (error) {
    console.error('注册失败:', error)
    alertStore.showAlert('注册失败，请检查验证码和信息', 'error')
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <auth-layout>
    <template #header>
      <h2 class="text-center text-2xl font-bold text-base-content">注册账号</h2>
    </template>

    <form class="space-y-4" @submit.prevent="handleSubmit">
      <!-- 邮箱输入 -->
      <div class="validator flex">
        <input
          type="email"
          v-model="registerFormData.email"
          class="input input-bordered flex-1"
          :class="{ 'input-error': emailError }"
          placeholder="example@domain.com"
          required
          @input="emailError = !emailRegex.test(registerFormData.email)"
        />
        <button
          type="button"
          class="btn btn-primary border-l-0 rounded-l-none w-32"
          @click="handleSendCode"
          :disabled="countdown > 0"
        >
          {{ countdown > 0 ? `${countdown}秒` : '获取验证码' }}
        </button>
      </div>
      <div v-if="emailError" class="validator-hint text-error text-xs mt-1">
        请输入有效的邮箱地址
      </div>

      <!-- 验证码 -->
      <InputField

        label="验证码"
        type="text"
        v-model="registerFormData.code"
        required
        placeholder="请输入6位验证码"
        :error="codeError"
        error-message="验证码不能为空"
      />

      <!-- 用户名 -->
      <InputField
        label="用户名"
        type="text"
        v-model="registerFormData.username"
        required
        placeholder="请输入用户名"
        autocomplete="username"
      />

      <!-- 密码 -->
      <InputField
        label="密码"
        type="password"
        v-model="registerFormData.password"
        required
        placeholder="••••••••"
        :hints="passwordHints"
        :error="!passwordHints.every((hint) => hint.valid)"
      />

      <!-- 确认密码 -->
      <InputField
        label="确认密码"
        type="password"
        v-model="registerFormData.confirmPassword"
        required
        placeholder="••••••••"
        :error="registerFormData.password !== registerFormData.confirmPassword"
        error-message="两次输入密码不一致"
      />

      <AuthButton
        :type="'submit'"
        :disabled="isLoading || !passwordHints.every((hint) => hint.valid)"
        class="w-full"
      >
        <template #buttonName>
          <span v-if="isLoading" class="loading loading-spinner"></span>
          {{ isLoading ? '注册中...' : '立即注册' }}
        </template>
      </AuthButton>
    </form>

    <div class="mt-6 text-center text-sm">
      <router-link to="/" class="link link-primary">已有账号？立即登录</router-link>
    </div>
    <Alert />
  </auth-layout>
</template>
