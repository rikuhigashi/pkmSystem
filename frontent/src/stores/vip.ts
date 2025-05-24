import { defineStore } from 'pinia'
import { userAuthStore } from '@/stores/auth'
import {computed} from "vue";

export const userVipStore = defineStore('vip', () => {
  const authStore = userAuthStore()

  // VIP状态
  const isVipActive = computed(() => authStore.userInfo?.vipActive || false)
  // VIP过期时间
  const expireDate = computed(() => authStore.userInfo?.vipExpireDate || '')
  // VIP剩余天数
  const remainingDays = computed(() => {
    if (!expireDate.value) return 0
    const diff = new Date(expireDate.value).getTime() - Date.now()
    return Math.ceil(diff / (1000 * 60 * 60 * 24))
  })

  return { isVipActive, expireDate, remainingDays }
})
