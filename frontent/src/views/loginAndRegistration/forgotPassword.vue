<script setup lang="ts">
// ------------------- components -------------------
import AuthLayout from '@/components/loginAndRegistration/authLayout.vue'
import AuthButton from '@/components/loginAndRegistration/authButton.vue'
import InputField from '@/components/loginAndRegistration/InputField.vue'
import InputFieldButton from '@/components/loginAndRegistration/InputFieldButton.vue'
// ------------------- components -------------------

// ------------------- vue -------------------
import { ref } from 'vue'

// ------------------- vue -------------------

// ------------------- api -------------------
import { sendResetPasswordCode, verifyResetCode } from '@/API/login/loginAPI'

// ------------------- api -------------------

// ------------------- store -------------------
import { useAuthForm } from '@/views/loginAndRegistration/configs/useAuthForm'
import { useAlertStore } from '@/stores/alert'
import router from '@/router'

const { resetPasswordFormData } = useAuthForm()
const alertStore = useAlertStore()

// ------------------- store -------------------

const countdown = ref(0) // 倒计时
const isSending = ref(false) // 是否正在发送验证码

// 点击获取验证码按钮
const handleSendCode = async () => {
  try {
    isSending.value = true

    const response = await sendResetPasswordCode({ email: resetPasswordFormData.value.email })

    if (response.success) {
      alertStore.showAlert('验证码已发送', 'success')

      countdown.value = 60 // 设置倒计时为60秒

      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
          isSending.value = false
          countdown.value = 0
        }
      }, 1000)
    } else {
      alertStore.showAlert('用户不存在', 'error')
    }
  } catch (error) {
    alertStore.showAlert('网络错误，请重试', 'error')
    console.error(error)
  } finally {
    isSending.value = false
  }
}

const handleSubmit = async () => {
  try {
    const res = await verifyResetCode({
      email: resetPasswordFormData.value.email,
      code: resetPasswordFormData.value.code,
    })

    if (res.success) {
      await router.replace('/resetPassword')
    } else {
      alertStore.showAlert('验证码错误或信息未填写完全', 'error')
    }
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <auth-layout>
    <template #header>
      <h2 class="mt-6 text-center text-2xl/9 font-bold tracking-tight text-gray-900">忘记密码</h2>
    </template>

    <form class="space-y-6" @submit.prevent="handleSubmit">
      <InputFieldButton
        id="email"
        type="email"
        label="电子邮件"
        :button-value="countdown > 0 ? `${countdown}秒后重试` : '获取验证码'"
        @send-code="handleSendCode"
        v-model="resetPasswordFormData.email"
        autocomplete="email"
        required
        placeholder="请输入您的电子邮件地址"
      />

      <InputField
        id="verificationCode"
        label="验证码"
        type="text"
        v-model="resetPasswordFormData.code"
        required
        placeholder="请输入6位验证码"
      />

      <AuthButton :type="'submit'">
        <template #buttonName>下一步</template>
      </AuthButton>
    </form>

    <router-link
      to="/"
      class="font-semibold text-indigo-600 hover:text-indigo-500 flex justify-center mt-5"
      >返回登录
    </router-link>

    <AppAlert />
  </auth-layout>
</template>
