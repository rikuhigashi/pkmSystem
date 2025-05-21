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
    <label tabindex="0" class="btn btn-ghost btn-circle">
      <BellIcon class="w-6 h-6" />
    </label>

    <div tabindex="0" class="mt-3 z-1 card card-compact dropdown-content w-96 bg-base-100 shadow">
      <div class="card-body max-h-[480px] overflow-y-auto">
        <div class="flex items-center justify-between mb-2">
          <h3 class="card-title">通知中心</h3>
          <button
            @click.stop="fetchNotifications"
            class="btn btn-xs btn-ghost"
            :class="{ loading: isLoading }"
            :disabled="isLoading"
          >
            <ArrowPathIcon class="w-4 h-4" />
          </button>
        </div>

        <!-- 加载状态 -->
        <div v-if="isLoading" class="space-y-2">
          <div v-for="i in 3" :key="i" class="animate-pulse flex space-x-4">
            <div class="flex-1 space-y-2 py-1">
              <div class="h-3 bg-gray-200 rounded"></div>
              <div class="h-2 bg-gray-200 rounded w-1/2"></div>
            </div>
          </div>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="text-center py-4 text-red-500 text-sm">
          {{ error }}
        </div>

        <!-- 空状态 -->
        <div v-else-if="!notifications.length" class="text-gray-500 text-center py-4">
          暂无新通知
        </div>

        <!-- 通知列表 -->
        <div v-else class="divide-y">
          <div
            v-for="n in notifications"
            :key="n.id"
            class="p-3 hover:bg-gray-50 transition-colors"
          >
            <div class="text-sm font-medium">
              {{ n.content }}
            </div>
            <div class="text-xs text-gray-500 mt-1">
              {{ formatTime(n.createdAt) }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
