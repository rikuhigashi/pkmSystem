<!-- NotificationPanel.vue -->
<script setup lang="ts">
import { BellIcon, ArrowPathIcon } from '@heroicons/vue/24/outline'
import { getNotifications } from '@/API/admin/adminAPI'
import { ref, onMounted, onUnmounted } from 'vue'

interface Notification {
  id: number
  content: string
  createdAt: string
  relatedDataId?: number
}

const notifications = ref<Notification[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)
let refreshTimer: number | null = null

// 获取通知数据
const fetchNotifications = async () => {
  try {
    isLoading.value = true
    error.value = null
    const res = await getNotifications()
    if (res.success) {
      notifications.value = res.data
    }
  } catch {
    error.value = '无法加载通知，请稍后再试'
  } finally {
    isLoading.value = false
  }
}

// 格式化时间显示
const formatTime = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60 * 1000) return '刚刚'
  if (diff < 3600 * 1000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400 * 1000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString()
}

// 初始化数据和定时器
onMounted(() => {
  fetchNotifications()
  refreshTimer = setInterval(fetchNotifications, 30000) as unknown as number
})
// 清理定时器
onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
})
</script>

<template>
  <div class="dropdown dropdown-end">
    <label tabindex="0" class="btn btn-ghost btn-circle hover:bg-gray-100">
      <BellIcon class="w-6 h-6" />
      <span
        v-if="notifications.length"
        class="badge badge-xs badge-error absolute -top-1 -right-1 animate-pulse"
      ></span>
    </label>

    <div tabindex="0" class="dropdown-content w-80 sm:w-96">
      <div class="card bg-base-100 shadow-xl">
        <div class="card-body p-4 max-h-[70vh]">
          <div class="flex items-center justify-between mb-2 px-2">
            <h3 class="text-lg font-semibold">通知中心</h3>
            <button
              @click.stop="fetchNotifications"
              class="btn btn-circle btn-sm btn-ghost"
              :disabled="isLoading"
            >
              <ArrowPathIcon class="w-4 h-4" :class="{ 'animate-spin': isLoading }" />
            </button>
          </div>

          <!-- 加载状态 -->
          <div v-if="isLoading" class="space-y-3 px-2">
            <div v-for="i in 3" :key="i" class="flex items-center space-x-3">
              <div class="skeleton h-12 w-12 rounded-full"></div>
              <div class="flex-1 space-y-2">
                <div class="skeleton h-4 w-3/4"></div>
                <div class="skeleton h-3 w-1/2"></div>
              </div>
            </div>
          </div>

          <!-- 通知项布局 -->
          <div v-else class="divide-y divide-gray-200">
            <div
              v-for="n in notifications"
              :key="n.id"
              class="p-3 hover:bg-gray-50 active:bg-gray-100 transition-colors cursor-pointer"
            >
              <div class="text-sm line-clamp-2">{{ n.content }}</div>
              <div class="text-xs text-gray-500 mt-1">{{ formatTime(n.createdAt) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


