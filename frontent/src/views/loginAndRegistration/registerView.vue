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
import { sendVerificationCode } from '@/API/login/loginAPI'
// ------------------- api -------------------

// ------------------- store -------------------
import { useAlertStore } from '@/stores/alert'
import { useAuthForm } from '@/views/loginAndRegistration/configs/useAuthForm'

const alertStore = useAlertStore()
const { registerFormData, authStore, isLoading } = useAuthForm()
// ------------------- store -------------------

const passwordError = ref(false) // 密码错误标志

// 注册提交
const handleSubmit = async () => {
  if (registerFormData.value.password !== registerFormData.value.confirmPassword) {
    alertStore.showAlert('密码不一致', 'error')
    return
  }

  try {
    isLoading.value = true
    await authStore.registerUser({
      email: registerFormData.value.email,
      username: registerFormData.value.username,
      password: registerFormData.value.password,
      confirmPassword: registerFormData.value.confirmPassword,
      code: registerFormData.value.code,
    })
    alertStore.showAlert('注册成功', 'success')
  } catch (error) {
    alertStore.showAlert('注册失败', 'error')
    console.error(error)
  } finally {
    isLoading.value = false
  }

  // // 密码一致性检查
  // if (registerFormData.value.password !== registerFormData.value.confirmPassword) {
  //   passwordError.value = true
  //   return
  // }
  //
  // // 验证码必填检查
  // if (!registerFormData.value.code) {
  //   alertStore.showAlert('请输入验证码', 'error');
  //   return;
  // }
  //
  // alertStore.showAlert('注册成功，请登录', 'success')
  //
  // await handleBasicAuth('/register')
  // await router.replace('/')
}

const countdown = ref(0) // 倒计时
const isSending = ref(false) // 是否正在发送验证码

const showVerificationField = ref(false) // 显示验证码输入框

const handleSendCode = async () => {
  // if (!registerFormData.value.email) {
  //   alertStore.showAlert('请输入邮箱地址', 'error');
  //   return;
  // }

  // // 邮箱格式验证
  // const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  // if (!emailRegex.test(registerFormData.value.email)) {
  //   alertStore.showAlert('邮箱格式不正确', 'error');
  //   return;
  // }
  showVerificationField.value = false
  try {
    isSending.value = true
    const response = await sendVerificationCode(registerFormData.value.email)

    showVerificationField.value = true

    if (response.success) {
      alertStore.showAlert('验证码已发送', 'success')
      showVerificationField.value = true

      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
          isSending.value = false
          countdown.value = 0
        }
      }, 1000)
    } else {
      showVerificationField.value = false

      alertStore.showAlert('发送失败', 'error')
    }
  } catch (error) {
    alertStore.showAlert('网络错误，请重试', 'error')
    console.error(error)
  } finally {
    isSending.value = false
  }
}
</script>

<template>
  <auth-layout>
    <template #header>
      <h2 class="mt-6 text-center text-2xl/9 font-bold tracking-tight text-gray-900">注册账号</h2>
    </template>

    <form class="space-y-6" @submit.prevent="handleSubmit">
      <InputFieldButton
        id="email"
        type="email"
        label="电子邮件"
        :button-value="countdown > 0 ? `${countdown}秒后重试` : '获取验证码'"
        autocomplete="email"
        required
        placeholder="请输入您的电子邮件地址"
        v-model="registerFormData.email"
        @send-code="handleSendCode"
      />

      <InputField
        v-if="showVerificationField"
        id="verificationCode"
        label="验证码"
        type="text"
        required
        placeholder="请输入6位验证码"
        v-model="registerFormData.code"
      />

      <InputField
        id="user"
        label="用户名"
        type="text"
        autocomplete="username"
        required
        placeholder="请输入您的用户名"
        v-model="registerFormData.username"
      />
      <InputField
        id="password"
        label="密码"
        type="password"
        autocomplete="current-password"
        required
        placeholder="请输入密码"
        v-model="registerFormData.password"
        :class="[
          passwordError ? 'input-error tooltip-open tooltip tooltip-bottom w-full text-left ' : '',
        ]"
        data-tip="两处的密码不一致"
      />
      <InputField
        id="confirm-password"
        label="再次输入密码"
        type="password"
        autocomplete="current-password"
        required
        placeholder="请输入再次密码"
        v-model="registerFormData.confirmPassword"
        :class="[
          passwordError ? 'input-error tooltip-open tooltip tooltip-bottom w-full text-left' : '',
        ]"
        data-tip="两处的密码不一致"
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
