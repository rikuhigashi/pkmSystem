<template>
  <AuthLayout>
    <!--  header  -->
    <template #header>
      <h2 class="mt-6 text-center text-2xl/9 font-bold tracking-tight text-gray-900">账号登录</h2>
    </template>

    <form class="space-y-6" @submit.prevent="handleSubmit">
      <div class="form-control w-full">
        <label for="email" class="label">
          <span class="label-text">电子邮件</span>
        </label>
        <input
          id="email"
          type="email"
          v-model="formData.email"
          class="input input-bordered w-full validator"
          :class="{ 'input-error': emailError }"
          placeholder="example@domain.com"
          required
          @input="validateEmail"
        />

        <div v-if="emailError" class="validator-hint text-error text-xs mt-1">
          {{ emailErrorMessage }}
        </div>
      </div>

      <InputField
        id="password"
        label="密码"
        type="password"
        v-model="formData.password"
        :error="passwordError"
        :error-message="passwordErrorMessage"
        :hints="passwordHints"
        placeholder="••••••••"
        autocomplete="current-password"
        required
      />

      <div class="flex items-center justify-between">
        <div class="form-control">
          <label class="label cursor-pointer">
            <input type="checkbox" class="checkbox" />
            <span class="label-text ml-2">记住我</span>
          </label>
        </div>

        <div class="text-sm/6">
          <router-link
            to="/forgotPassword"
            class="font-semibold text-indigo-600 hover:text-indigo-500"
            >忘记密码
          </router-link>
        </div>
      </div>

      <AuthButton :type="'submit'">
        <template #buttonName>{{ isLoading ? '登录中...' : '登录' }}</template>
      </AuthButton>
    </form>

    <div>
      <div class="relative mt-10">
        <div class="absolute inset-0 flex items-center" aria-hidden="true">
          <div class="w-full border-t border-gray-200" />
        </div>
        <div class="relative flex justify-center text-sm/6 font-medium">
          <span class="bg-white px-6 text-gray-900">其他登录方式</span>
        </div>
      </div>

      <div class="mt-6 grid grid-cols-2 gap-4">
        <SocialAuthButton text="Google" @click="handleGoogleLogin">
          <template #icon>
            <AuthIcons type="google" />
          </template>
        </SocialAuthButton>

        <SocialAuthButton text="GitHub" @click="handleGithubLogin">
          <template #icon>
            <AuthIcons type="github" />
          </template>
        </SocialAuthButton>
      </div>
    </div>

    <!--   footer   -->

    <template #footer>
      <p class="mt-10 text-center text-sm/6 text-gray-500">
        没有账号?
        {{ ' ' }}
        <router-link to="/registerView" class="font-semibold text-indigo-600 hover:text-indigo-500"
          >立刻注册
        </router-link>
      </p>
    </template>

    <Alert />
  </AuthLayout>
</template>

<script setup lang="ts">
// --------------------  组件引入 --------------------
import AuthLayout from '@/components/loginAndRegistration/authLayout.vue'
import InputField from '@/components/loginAndRegistration/InputField.vue'
import AuthButton from '@/components/loginAndRegistration/authButton.vue'
import SocialAuthButton from '@/components/loginAndRegistration/socialAuthButton.vue'
import AuthIcons from '@/assets/icons/authIcon/AuthIcons.vue'

import { useAuthForm } from '@/views/loginAndRegistration/configs/useAuthForm'
import { useAlertStore } from '@/stores/alert'
import { computed, ref } from 'vue'
import Alert from '@/components/appAlert.vue'

const alertStore = useAlertStore()
const { formData, authStore, isLoading, errorMessage } = useAuthForm()

// --------------------  组件引入 --------------------

const emailError = ref(false)
const emailErrorMessage = ref('')
const passwordError = ref(false)
const passwordErrorMessage = ref('')

const validateEmail = () => {
  const emailValid = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(formData.value.email)
  emailError.value = !emailValid
  emailErrorMessage.value = emailValid ? '' : '请输入有效的电子邮件地址'
}

const passwordHints = computed(() => {
  // 仅在密码输入后显示提示
  if (formData.value.password.length === 0) return []

  return [
    {
      text: '至少8个字符',
      valid: formData.value.password.length >= 8,
    },
    {
      text: '包含大写字母',
      valid: /[A-Z]/.test(formData.value.password),
    },
    {
      text: '包含特殊字符（!@#$%^&*?.）',
      valid: /[!@#$%^&*?.]/.test(formData.value.password),
    },
  ]
})

const validateForm = () => {
  let valid = true

  // 邮箱验证
  const emailValid = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(formData.value.email)
  emailError.value = !emailValid
  emailErrorMessage.value = emailValid ? '' : '请输入有效的电子邮件地址'
  valid = valid && emailValid

  // 密码验证
  if (formData.value.password.length > 0) {
    const lengthValid = formData.value.password.length >= 8
    const uppercaseValid = /[A-Z]/.test(formData.value.password)
    const symbolValid = /[!@#$%^&*?.]/.test(formData.value.password)

    passwordError.value = !(lengthValid && uppercaseValid && symbolValid)
    valid = valid && lengthValid && uppercaseValid && symbolValid
  } else {
    passwordError.value = false
  }

  return valid
}

// --------------------  登录逻辑 --------------------

const handleSubmit = async () => {
  if (!validateForm()) return

  alertStore.showLoading('正在登录中...请稍后...')

  try {
    isLoading.value = true
    const res = await authStore.loginUser(formData.value.email, formData.value.password)
    if (res.success) {
      alertStore.showAlert('登录成功', 'success')
    } else {
      alertStore.showAlert('账号或密码错误', 'error')
      errorMessage.value = '登录失败'
    }
  } catch (error) {
    alertStore.showAlert('用户不存在', 'error')
    errorMessage.value = '登录失败'
    console.error(error)
  } finally {
    isLoading.value = false
  }

  // try {
  //   await handleSubmit('/login') // 触发统一登录逻辑
  //   alertStore.showAlert("登录成功", "success")
  //   await router.replace({ name: 'home' })
  // } catch (error) {
  //   alertStore.showAlert('用户名或密码错误', 'error')
  //   console.error(error)
  // }
}

// Google 登录逻辑
const handleGoogleLogin = () => {
  console.log('Google 登录逻辑')
}
// GitHub 登录逻辑
const handleGithubLogin = () => {
  console.log('GitHub 登录逻辑')
}
// --------------------  登录逻辑 --------------------
</script>
