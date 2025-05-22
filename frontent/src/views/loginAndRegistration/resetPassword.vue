<script setup lang="ts">
// ------------------- components -------------------
import AuthLayout from '@/components/loginAndRegistration/authLayout.vue'
import AuthButton from '@/components/loginAndRegistration/authButton.vue'
import InputField from '@/components/loginAndRegistration/InputField.vue'
import {useAuthForm} from '@/views/loginAndRegistration/configs/useAuthForm'
import {computed, ref, watch} from 'vue'
import Alert from '@/components/appAlert.vue'
import type { AxiosError } from 'axios'
// ------------------- components -------------------

// ------------------- store -------------------
import {useAlertStore} from '@/stores/alert'
import {resetPassword} from '@/API/login/loginAPI'
import router from '@/router'

const {isLoading} = useAuthForm()

const alertStore = useAlertStore()
const {resetPasswordFormData} = useAuthForm()
// ------------------- store -------------------

const newPasswordError = ref(false)
const confirmPasswordError = ref(false)
const isFormValid = ref(false)

// 实时密码强度验证
const passwordHints = computed(() => {
  if (!resetPasswordFormData.value.newPassword) return []
  return [
    {text: '至少8个字符', valid: resetPasswordFormData.value.newPassword.length >= 8},
    {text: '包含大写字母', valid: /[A-Z]/.test(resetPasswordFormData.value.newPassword)},
    {text: '包含特殊字符（!@#$%^&*?.）', valid: /[!@#$%^&*?.]/.test(resetPasswordFormData.value.newPassword)}
  ]
})

// 实时密码匹配验证
watch(
  () => [
    resetPasswordFormData.value.newPassword,
    resetPasswordFormData.value.confirmPassword
  ],
  ([newPass, confirmPass]) => {
    newPasswordError.value = !passwordHints.value.every(hint => hint.valid)
    confirmPasswordError.value = newPass !== confirmPass && confirmPass.length > 0
    isFormValid.value = !newPasswordError.value && !confirmPasswordError.value
  }
)

const handleSubmit = async () => {
  if (!isFormValid.value) return

  try {
    isLoading.value = true
    const res = await resetPassword({
      newPassword: resetPasswordFormData.value.newPassword,

    })
    if (res.success) {
      alertStore.showAlert('密码重置成功，请登录', 'success')
      await router.replace('/')
    }
  } catch (error) {
    const axiosError = error as AxiosError<{ message?: string }>
    alertStore.showAlert('重置失败，请重试', 'error')
    return {
      success: false,
      error:axiosError.response?.data?.message || '重置失败',
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <auth-layout>
    <template #header>
      <h2 class="text-center text-2xl font-bold text-base-content">重置密码</h2>
    </template>

    <form class="space-y-4" @submit.prevent="handleSubmit">
      <InputField
        label="新密码"
        type="password"
        v-model="resetPasswordFormData.newPassword"
        required
        placeholder="••••••••"
        :hints="passwordHints"
        :error="newPasswordError"
        error-message="密码强度不足"
      />

      <InputField
        label="确认密码"
        type="password"
        v-model="resetPasswordFormData.confirmPassword"
        required
        placeholder="••••••••"
        :error="confirmPasswordError"
        error-message="两次输入密码不一致"
      />

      <AuthButton :type="'submit'" :disabled="!isFormValid">
        <template #buttonName>重置密码</template>
      </AuthButton>
    </form>

    <Alert />
  </auth-layout>
</template>
