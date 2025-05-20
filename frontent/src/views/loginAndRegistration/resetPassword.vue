<script setup lang="ts">
// ------------------- components -------------------
import AuthLayout from '@/components/loginAndRegistration/authLayout.vue'
import AuthButton from '@/components/loginAndRegistration/authButton.vue'
import InputField from '@/components/loginAndRegistration/InputField.vue'
import { useAuthForm } from '@/views/loginAndRegistration/configs/useAuthForm'
import { ref } from 'vue'
// ------------------- components -------------------

// ------------------- vue -------------------

// ------------------- vue -------------------

// ------------------- api -------------------

// ------------------- api -------------------

// ------------------- store -------------------
import { useAlertStore } from '@/stores/alert'
import {resetPassword} from "@/API/login/loginAPI";
import router from "@/router";
const {  isLoading } = useAuthForm()

const alertStore = useAlertStore()
const { resetPasswordFormData } = useAuthForm()
// ------------------- store -------------------

const passwordError = ref(false) // 密码错误标志

const handleSubmit = async () => {

  const MIN_PASSWORD_LENGTH = 6;

  // 判断两处密码是否相同
  if (resetPasswordFormData.value.newPassword !== resetPasswordFormData.value.confirmPassword) {
    passwordError.value = true
    alertStore.showAlert('密码不一致', 'error')
    return
  }

  if (resetPasswordFormData.value.newPassword.length < MIN_PASSWORD_LENGTH) {
      alertStore.showAlert('密码至少需要6个字符', 'error');
      return;
  }


  try {
    isLoading.value = true
    const res = await resetPassword({
      newPassword: resetPasswordFormData.value.newPassword
    })
    if (res.success) {
      alertStore.showAlert('密码重置成功，请登录', 'success')
      await router.replace('/')
    }
  } catch (error) {
    alertStore.showAlert('重置失败，请重试', 'error')
    console.error(error)
  } finally {
    isLoading.value = false
  }

}
</script>

<template>
  <auth-layout>
    <template #header>
      <h2 class="mt-6 text-center text-2xl/9 font-bold tracking-tight text-gray-900">重置密码</h2>
    </template>

    <form class="space-y-6" @submit.prevent="handleSubmit">
      <InputField
        id="verificationCode"
        label="新密码"
        type="password"
        required
        minlength="6"
        placeholder="请输入新密码"
        v-model="resetPasswordFormData.newPassword"
        :class="[
          passwordError ? 'input-error tooltip-open tooltip tooltip-bottom w-full text-left ' : '',
        ]"
        data-tip="两处的密码不一致"
      />

      <InputField
        id="resetVerificationCode"
        label="再次输入新密码"
        type="password"
        required
        minlength="6"
        placeholder="请再次输入新密码"
        v-model="resetPasswordFormData.confirmPassword"
        :class="[
          passwordError ? 'input-error tooltip-open tooltip tooltip-bottom w-full text-left ' : '',
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
