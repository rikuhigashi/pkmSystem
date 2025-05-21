<!-- AccountDetailView.vue -->
<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { userAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import type { UserInfo } from '../user/types/auth'
import AppAlert from "@/components/appAlert.vue";
import { useAlertStore } from '@/stores/alert'

const authStore = userAuthStore()
const router = useRouter()

const alertStore = useAlertStore()


// 用户信息响应式对象
const userForm = ref<Partial<UserInfo>>({
  username: '',
  email: ''
})

// 初始化用户数据
onMounted(() => {


  if (authStore.userInfo) {
    userForm.value = {
      username: authStore.userInfo.username,
      email: authStore.userInfo.email
    }
  }
})

// 表单验证状态
const validation = ref({
  username: {
    min: 3,
    max: 20,
    valid: true,
    message: ''
  }
})

// 提交状态
const isSubmitting = ref(false)
const submitError = ref<string | null>(null)

// 用户名验证
const validateUsername = () => {
  const username = userForm.value.username?.trim() || ''
  validation.value.username.valid = true

  if (username.length < validation.value.username.min) {
    validation.value.username = {
      ...validation.value.username,
      valid: false,
      message: `用户名至少需要${validation.value.username.min}个字符`
    }
    return false
  }

  if (username.length > validation.value.username.max) {
    validation.value.username = {
      ...validation.value.username,
      valid: false,
      message: `用户名不能超过${validation.value.username.max}个字符`
    }
    return false
  }

  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!validateUsername()) return

  isSubmitting.value = true
  submitError.value = null

  try {
    await authStore.updateUserInfo({
      username: userForm.value.username
    })

    alertStore.showAlert('用户信息更新成功', 'success')
   await router.push({
      path: '/home',
      query: { updated: 'true' }
    })
  } catch (error: any) {
    submitError.value = error.message || '更新用户信息失败'
  } finally {
    isSubmitting.value = false
  }
}


</script>

<template>
  <div class="max-w-2xl mx-auto p-6">
    <h1 class="text-3xl font-bold text-gray-800 mb-8" >账号详情</h1>

    <div class="card bg-base-100 shadow-xl">
      <div class="card-body">
        <!-- 错误提示 -->
        <div v-if="submitError" class="alert alert-error mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>{{ submitError }}</span>
        </div>

        <!-- 用户名 -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">用户名</span>
          </label>
          <input
            type="text"
            v-model="userForm.username"
            :class="[
              'input input-bordered',
              { 'input-error': !validation.username.valid }
            ]"
            @input="validateUsername"
          />
          <label class="label" v-if="!validation.username.valid">
            <span class="label-text-alt text-error">
              {{ validation.username.message }}
            </span>
          </label>
        </div>

        <!-- 邮箱 -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">电子邮箱</span>
          </label>
          <input
            type="email"
            :value="authStore.userInfo?.email"
            class="input input-bordered"
            readonly
            disabled
          />
        </div>

        <!-- 提交按钮 -->
        <div class="form-control mt-6">
          <button
            class="btn btn-primary"
            :class="{ 'loading': isSubmitting }"
            :disabled="isSubmitting || !validation.username.valid"
            @click="handleSubmit"
          >
            {{ isSubmitting ? '保存中...' : '保存更改' }}
          </button>
        </div>
      </div>
    </div>

    <AppAlert />

  </div>
</template>
