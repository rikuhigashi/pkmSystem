<template>
  <div class="container mx-auto p-6 space-y-8">
    <p @click="logout">退出登录</p>
    <h1 class="text-2xl font-bold mb-4">用户管理</h1>
    <router-link
      to="/AdminPendingReview"
      class="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600"
    >
      前往审核数据管理
    </router-link>
    <section>
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
          <tr>
            <th class="px-6 py-3 bg-gray-50 text-left">邮箱</th>
            <th class="px-6 py-3 bg-gray-50 text-left">用户名</th>
            <th class="px-6 py-3 bg-gray-50 text-left">文档数</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.email">
            <td class="px-6 py-4">{{ user.email }}</td>
            <td class="px-6 py-4">{{ user.username }}</td>
            <td class="px-6 py-4">{{ user.tiptapJsons?.length || 0 }}</td>
          </tr>
        </tbody>
      </table>
    </section>


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
