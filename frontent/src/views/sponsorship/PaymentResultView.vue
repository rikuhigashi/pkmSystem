<script setup lang="ts">
import {onMounted, onUnmounted, ref} from 'vue'
import { checkPaymentStatus } from '@/API/payment/paymentAPI'
import { useAlertStore } from '@/stores/alert'
import { userAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import IconVip from "@/assets/icons/VIP/iconVip.vue";
import IconVipAct from "@/assets/icons/VIP/iconVipAct.vue";

const router = useRouter()
const alertStore = useAlertStore()
const authStore = userAuthStore()
const orderNo = ref('')
const paymentStatus = ref<'pending' | 'success' | 'failed'>('pending')
const pollInterval = ref<number>()

// 轮询查询支付状态
const pollPaymentStatus = async () => {
  const res = await checkPaymentStatus(orderNo.value)

  if (res.success) {
    if (res.data.status === 'SUCCESS') {
      paymentStatus.value = 'success'
      clearInterval(pollInterval.value)

      // 更新用户VIP状态
      await authStore.checkSession()

      setTimeout(() => {
        router.push('/home')
      }, 3000)
    } else if (res.data.status === 'EXPIRED') {
      paymentStatus.value = 'failed'
      clearInterval(pollInterval.value)
    }
  }
}

onMounted(async () => {
  orderNo.value = router.currentRoute.value.query.orderNo as string

  if (!orderNo.value) {
    alertStore.showAlert('无效的订单号', 'error')
    await router.push('/home')
    return
  }

  // 初始查询
  await pollPaymentStatus()

  // 设置轮询（每5秒查询一次）
  pollInterval.value = setInterval(pollPaymentStatus, 5000) as unknown as number

  // 15分钟后自动停止
  setTimeout(() => {
    clearInterval(pollInterval.value)
    if (paymentStatus.value === 'pending') {
      paymentStatus.value = 'failed'
    }
  }, 900000)
})

onUnmounted(() => {
  clearInterval(pollInterval.value)
})
</script>

<template>
  <div class="min-h-screen bg-base-200 flex items-center justify-center p-4">
    <div class="card bg-base-100 shadow-xl w-full max-w-md">
      <div class="card-body items-center text-center">
        <template v-if="paymentStatus === 'success'">
          <icon-vip-act class="w-20 h-20 text-success mb-4" />
          <h2 class="card-title text-2xl mb-2">支付成功！</h2>
          <p class="text-lg">
            <span class="font-bold text-success">VIP 已激活</span><br>
            有效期至：{{ authStore.userInfo?.vipExpireDate }}
          </p>
          <div class="mt-4 animate-pulse">3秒后自动返回首页...</div>
        </template>

        <template v-else-if="paymentStatus === 'failed'">
          <icon-vip class="w-20 h-20 text-error mb-4" />
          <h2 class="card-title text-2xl mb-2">支付失败</h2>
          <p class="text-error">订单已过期或支付未完成</p>
          <button class="btn btn-primary mt-4" @click="router.push('/sponsorshipView')">
            重新支付
          </button>
        </template>

        <template v-else>
          <span class="loading loading-infinity loading-lg text-primary"></span>
          <h2 class="card-title text-2xl mt-4">支付处理中...</h2>
          <p class="text-gray-500">请勿关闭页面，正在验证支付结果</p>
        </template>
      </div>
    </div>
  </div>
</template>
