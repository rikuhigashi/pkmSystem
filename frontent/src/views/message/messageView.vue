<script setup lang="ts">
import { BellIcon } from '@heroicons/vue/24/outline'
import { getNotifications } from '@/API/admin/adminAPI'
import { ref, onMounted } from 'vue'

interface Notification {
  id: number
  content: string
  read: boolean
  createdAt: string
  relatedDataId?: number
}

const notifications = ref<Notification[]>([])
const unreadCount = ref(0)

const fetchNotifications = async () => {
  const res = await getNotifications()
  if (res.success) {
    notifications.value = res.data
    unreadCount.value = notifications.value.filter(n => !n.read).length
  }
}

const markAsRead = (id: number) => {
  const index = notifications.value.findIndex(n => n.id === id)
  if (index !== -1) {
    notifications.value[index].read = true
    unreadCount.value--
  }
}

onMounted(fetchNotifications)
</script>

<template>
  <div class="dropdown dropdown-end">
    <label tabindex="0" class="btn btn-ghost btn-circle">
      <div class="indicator">
        <BellIcon class="w-6 h-6" />
        <span v-if="unreadCount" class="badge badge-sm indicator-item bg-red-500 text-white">
          {{ unreadCount }}
        </span>
      </div>
    </label>
    <div tabindex="0" class="mt-3 z-[1] card card-compact dropdown-content w-96 bg-base-100 shadow">
      <div class="card-body max-h-96 overflow-y-auto">
        <h3 class="card-title">通知</h3>
        <div v-for="n in notifications" :key="n.id"
             class="p-2 hover:bg-gray-50 cursor-pointer border-b"
             :class="{ 'bg-blue-50': !n.read }"
             @click="markAsRead(n.id)">
          <div class="text-sm">{{ n.content }}</div>
          <div class="text-xs text-gray-500">{{ new Date(n.createdAt).toLocaleString() }}</div>
        </div>
        <div v-if="!notifications.length" class="text-gray-500 text-center py-4">暂无新通知</div>
      </div>
    </div>
  </div>
</template>
