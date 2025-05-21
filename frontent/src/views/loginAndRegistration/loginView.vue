<template>
  <AuthLayout>
    <!--  header  -->
    <template #header>
      <!--      <img class="mx-auto h-10 w-auto" src="/plus-assets/img/logos/mark.svg?color=indigo&shade=600" alt="Your Company" />-->
      <h2 class="mt-6 text-center text-2xl/9 font-bold tracking-tight text-gray-900">账号登录</h2>
    </template>
    <!--  main  -->

    <form class="space-y-6" @submit.prevent="handleSubmit">
      <InputField
        id="email"
        label="电子邮件"
        type="email"
        autocomplete="email"
        required
        placeholder="电子邮箱"
        v-model="formData.email"
      />
      <InputField
        id="password"
        label="密码"
        type="password"
        autocomplete="current-password"
        required
        placeholder="密码"
        v-model="formData.password"
      />

      <div class="flex items-center justify-between">
        <div class="form-control">
          <label class="label cursor-pointer">
            <!--            checked="checked"-->
            <input type="checkbox" class="checkbox checkbox-primary" />
            <span class="label-text ml-2">记住我</span>
          </label>
        </div>

        <div class="text-sm/6">
          <router-link
            to="/forgotPassword"
            class="font-semibold text-indigo-600 hover:text-indigo-500"
            >忘记密码
          </router-link>
          <!--          <a href="#" class="font-semibold text-indigo-600 hover:text-indigo-500">忘记密码</a>-->
        </div>
      </div>

      <AuthButton :type="'submit'">
        <template #buttonName>{{ isLoading ? '登录中...' : '登录' }}</template>
        <!--        <template #buttonName>登录</template>-->
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
        <!--        <a href="#" class="font-semibold text-indigo-600 hover:text-indigo-500">立刻注册</a>-->
      </p>
    </template>

    <AppAlert />
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

const alertStore = useAlertStore()
const { formData, authStore, isLoading, errorMessage } = useAuthForm()

// --------------------  组件引入 --------------------

// --------------------  登录逻辑 --------------------

const handleSubmit = async () => {

  try {
    isLoading.value = true
   const res =  await authStore.loginUser(formData.value.email, formData.value.password)
    if (res.success){
      alertStore.showAlert("登录成功", "success")

    }else {
      alertStore.showAlert('用户不存在', 'error')
      errorMessage.value = '登录失败'
    }

  } catch (error) {
    alertStore.showAlert('用户名或密码错误', 'error')
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
