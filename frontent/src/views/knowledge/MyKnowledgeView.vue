<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { DocumentTextIcon, LockClosedIcon } from '@heroicons/vue/24/outline'
import { getPurchasedKnowledge, getMyKnowledge, type KnowledgeItem } from '@/API/knowledge/knowledgeAPI'
import router from "@/router";

const purchasedKnowledge = ref<KnowledgeItem[]>([])
const myKnowledge = ref<KnowledgeItem[]>([])
const isLoading = ref(true)

// 获取已购知识
const fetchPurchasedKnowledge = async () => {
  try {
    purchasedKnowledge.value = await getPurchasedKnowledge()
  } catch (error) {
    console.error('获取已购知识失败:', error)
  }
}

// 获取我分享的知识
const fetchMyKnowledge = async () => {
  try {
    myKnowledge.value = await getMyKnowledge()
  } catch (error) {
    console.error('获取分享知识失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 查看知识详情
const viewKnowledge = (id: number) => {
  router.push({ name: 'knowledgeDetail', params: { id } })
}

// 返回知识广场
const goBack = () => {
  router.push({ name: 'knowledgeSquare' })
}

onMounted(() => {
  fetchPurchasedKnowledge()
  fetchMyKnowledge()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- 返回按钮 -->
    <button @click="goBack" class="btn btn-ghost btn-sm mb-6 flex items-center">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-4 w-4 mr-1"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M10 19l-7-7m0 0l7-7m-7 7h18"
        />
      </svg>
      返回知识广场
    </button>

    <h1 class="text-3xl font-bold text-gray-900 mb-8">我的知识库</h1>

    <div class="mb-12">
      <h2 class="text-xl font-semibold mb-4 flex items-center">
        <LockClosedIcon class="h-5 w-5 mr-2 text-primary" />
        已购知识
      </h2>

      <div v-if="purchasedKnowledge.length === 0" class="text-center py-12 border-2 border-dashed rounded-lg">
        <p class="text-gray-500">您尚未购买任何知识</p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="item in purchasedKnowledge"
          :key="item.id"
          class="card bg-base-100 shadow hover:shadow-md transition-shadow"
        >
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="text-gray-500">作者: {{ item.authorName }}</p>
            <p class="text-sm text-gray-400 mt-2">购买日期: {{ item.purchasedAt }}</p>
            <div class="card-actions justify-end mt-4">
              <button
                class="btn btn-ghost btn-sm"
                @click="viewKnowledge(item.id)"
              >
                查看
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div>
      <h2 class="text-xl font-semibold mb-4 flex items-center">
        <DocumentTextIcon class="h-5 w-5 mr-2 text-green-500" />
        我分享的知识
      </h2>

      <div v-if="myKnowledge.length === 0" class="text-center py-12 border-2 border-dashed rounded-lg">
        <p class="text-gray-500">您尚未分享任何知识</p>
        <router-link
          to="/knowledge/upload"
          class="btn btn-primary mt-4"
        >
          去分享
        </router-link>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="item in myKnowledge"
          :key="item.id"
          class="card bg-base-100 shadow hover:shadow-md transition-shadow"
        >
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="text-gray-500">作者: {{ item.authorName }}</p>
            <p class="text-sm text-gray-400 mt-2">创建日期: {{ item.createdAt }}</p>
            <div class="card-actions justify-end mt-4">
              <button
                class="btn btn-ghost btn-sm"
                @click="viewKnowledge(item.id)"
              >
                查看
              </button>
              <button class="btn btn-ghost btn-sm">编辑</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
