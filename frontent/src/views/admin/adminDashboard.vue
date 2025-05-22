<template>
  <div class="container mx-auto p-6 space-y-6">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div class="space-y-2">
        <h1 class="text-2xl font-bold">用户管理</h1>
        <button
          class="btn btn-ghost text-sm text-gray-500 hover:bg-gray-100"
          @click="logout"
        >
          退出登录
        </button>
      </div>
      <router-link
        to="/AdminPendingReview"
        class="btn btn-primary gap-2"
      >
        <span>前往审核管理</span>
        <svg xmlns="http://www.w3.org/2000/svg" class="size-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </router-link>
    </div>

    <div class="overflow-x-auto rounded-lg border">
      <table class="table table-zebra">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-4 py-3 font-medium">邮箱</th>
          <th class="px-4 py-3 font-medium">用户名</th>
          <th class="px-4 py-3 font-medium">文档数</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in users" :key="user.email">
          <td class="px-4 py-3">{{ user.email }}</td>
          <td class="px-4 py-3">{{ user.username }}</td>
          <td class="px-4 py-3">{{ user.tiptapJsons?.length || 0 }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {

  getAllUsersWithSideData,

} from '@/API/admin/adminAPI'
import { userAuthStore } from '@/stores/auth'

const authStore = userAuthStore()

interface UserData {
  email: string
  username: string
  tiptapJsons: Record<string, unknown>
}



const logout = () => {
  authStore.logout()
  console.log('退出登录')
}

const users = ref<UserData[]>([])


onMounted(async () => {
  try {


    const response = await getAllUsersWithSideData()

    users.value = response.data.map((user: UserData) => ({
      email: user.email,
      username: user.username,
      tiptapJsons: user.tiptapJsons,
    }))

    console.log(response.data)
  } catch (error) {
    console.error('获取用户数据失败:', error)
  }
})
</script>
