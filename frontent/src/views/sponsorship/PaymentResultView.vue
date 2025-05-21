<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
      <div class="text-center">
        <CheckCircleIcon class="mx-auto h-16 w-16 text-green-500" />
        <h2 class="mt-4 text-2xl font-bold text-gray-900">支付成功</h2>
        <p class="mt-2 text-gray-600">订单号：{{ orderNo }}</p>
        <div class="mt-6">
          <button
            @click="returnHome"
            class="inline-flex items-center px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-500"
          >
            返回首页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { CheckCircleIcon } from '@heroicons/vue/24/outline'
import { useRoute, useRouter } from 'vue-router'
import { userAuthStore } from '@/stores/auth'
import { onMounted } from 'vue'

const route = useRoute()
const router = useRouter()
const orderNo = route.query.orderNo
const authStore = userAuthStore()

const refreshUserStatus = async () => {
  await authStore.checkSession()
}
onMounted(async () => {
  await refreshUserStatus()
  setTimeout(() => {
    router.push('/home')
  }, 3000)
})

const returnHome = () => {
  router.replace('/home')
}

// 自动跳转
setTimeout(() => {
  router.replace('/home')
}, 5000)
</script>
