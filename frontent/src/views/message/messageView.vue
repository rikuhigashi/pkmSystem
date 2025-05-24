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
    unreadCount.value = notifications.value.filter((n) => !n.read).length
  }
}

const markAsRead = (id: number) => {
  const index = notifications.value.findIndex((n) => n.id === id)
  if (index !== -1) {
    notifications.value[index].read = true
    unreadCount.value--
  }
}

onMounted(fetchNotifications)
</script>

<template>
  <div class="dropdown dropdown-end">
    <label tabindex="0" class="btn btn-ghost btn-circle hover:bg-gray-100">
      <div class="indicator">
        <BellIcon class="w-6 h-6" />
        <span v-if="unreadCount" class="badge badge-sm badge-error indicator-item animate-pulse">{{
          unreadCount
        }}</span>
      </div>
    </label>

    <div tabindex="0" class="dropdown-content w-80 sm:w-96">
      <div class="card bg-base-100 shadow-xl">
        <div class="card-body p-4 max-h-[70vh] overflow-y-auto">
          <h3 class="text-lg font-semibold mb-2">通知</h3>

          <div class="space-y-2">
            <div
              v-for="n in notifications"
              :key="n.id"
              class="group relative p-3 rounded-lg transition-colors duration-200"
              :class="{
                'bg-blue-50/50 hover:bg-blue-100': !n.read,
                'hover:bg-gray-50': n.read,
              }"
              @click="markAsRead(n.id)"
            >
              <!-- 删除按钮容器 -->
              <div
                class="absolute right-2 top-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity z-20"
              >
                <button
                  @click.stop="deleteNotification(n.id)"
                  class="p-1 rounded-full hover:bg-gray-200/80 transition-colors duration-150"
                  title="删除通知"
                >
                  <TrashIcon class="w-4 h-4 text-gray-500 hover:text-error" />
                </button>
              </div>

              <!-- 通知内容 -->
              <div class="text-sm line-clamp-2 pr-8">{{ n.content }}</div>
              <div class="text-xs text-gray-500 mt-1">
                {{ new Date(n.createdAt).toLocaleDateString() }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
