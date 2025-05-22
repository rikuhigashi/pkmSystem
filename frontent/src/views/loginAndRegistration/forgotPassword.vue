<script setup lang="ts">
// ------------------- components -------------------
import AuthLayout from '@/components/loginAndRegistration/authLayout.vue'
import AuthButton from '@/components/loginAndRegistration/authButton.vue'
import InputField from '@/components/loginAndRegistration/InputField.vue'
import Alert from '@/components/appAlert.vue'
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
const emailError = ref(false)
const emailErrorMessage = ref('')
const codeError = ref(false)

const validateEmail = () => {
  const emailValid = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(
    resetPasswordFormData.value.email,
  )
  emailError.value = !emailValid
  emailErrorMessage.value = emailValid ? '' : '邮箱格式不正确'
}

// 点击获取验证码按钮
const handleSendCode = async () => {
  if (emailError.value) return

  const email = resetPasswordFormData.value.email.trim()

  try {
    isSending.value = true

    const response = await sendResetPasswordCode({ email })

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
      alertStore.showAlert('发送验证码失败', 'error')
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

    if (res.success && res.data?.resetToken) {
      localStorage.setItem("resetToken", res.data.resetToken)
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
      <h2 class="text-center text-2xl font-bold text-gray-900">忘记密码</h2>
    </template>

    <form class="space-y-4" @submit.prevent="handleSubmit">
      <!-- 邮箱输入 -->

      <div class="form-control w-full">
        <label class="label">
          <span class="label-text">电子邮件</span>
        </label>

        <div class="validator flex">
          <input
            type="email"
            v-model="resetPasswordFormData.email"
            class="input input-bordered flex-1"
            :class="{ 'input-error': emailError }"
            placeholder="example@domain.com"
            required
            @input="validateEmail"
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
          {{ emailErrorMessage }}
        </div>

      </div>

      <!-- 验证码输入 -->
      <InputField
        id="verificationCode"
        label="验证码"
        type="text"
        v-model="resetPasswordFormData.code"
        :error="codeError"
        error-message="验证码错误"
        required
        placeholder="请输入6位验证码"
      />
      <AuthButton :type="'submit'">
        <template #buttonName>下一步</template>
      </AuthButton>
    </form>

    <router-link to="/" class="link link-primary flex justify-center mt-4"> 返回登录</router-link>

    <Alert />
  </auth-layout>
</template>
