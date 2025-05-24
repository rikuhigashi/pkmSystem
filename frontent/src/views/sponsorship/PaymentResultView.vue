<template>
  <div class="min-h-screen bg-gray-50 grid place-items-center p-4">
    <div class="bg-white p-8 rounded-xl shadow-xl max-w-md w-full transition-all">
      <div class="text-center space-y-4">
        <CheckCircleIcon class="mx-auto h-16 w-16 text-green-500 animate-pop-in" />
        <h2 class="text-2xl font-bold text-gray-900">支付成功</h2>
        <p class="text-gray-600 break-all">{{ orderNo }}</p>
        <div class="mt-6">
          <button
            @click="returnHome"
            class="btn btn-primary w-full"
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
