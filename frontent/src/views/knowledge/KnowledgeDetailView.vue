<script lang="ts" setup>
import {ref, onMounted, computed} from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { renderTiptapContent } from '@/utils/tiptapRenderer'
import { getKnowledgeDetail, purchaseKnowledge, type KnowledgeDetail } from '@/API/knowledge/knowledgeAPI'
import { userAuthStore } from "@/stores/auth"

const authStore = userAuthStore()
const route = useRoute()
const router = useRouter()
const knowledgeId = ref(parseInt(route.params.id as string))

const knowledge = ref<KnowledgeDetail | null>(null)
const isLoading = ref(true)
const isPurchasing = ref(false)

// 获取用户余额
const userBalance = computed(() => authStore.userInfo?.balance || 0)

// 获取知识详情
const fetchKnowledgeDetail = async () => {
  try {
    isLoading.value = true
    knowledge.value = await getKnowledgeDetail(knowledgeId.value)
  } catch (error) {
    console.error('加载知识详情失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 购买知识
const handlePurchase = async () => {
  if (!knowledge.value) return

  try {
    isPurchasing.value = true
    // 调用购买API并获取新余额
    const newBalance = await purchaseKnowledge(knowledge.value.id)

    // 更新本地余额
    authStore.updateBalance(newBalance)

    // 标记知识为已购买
    knowledge.value.purchased = true
  } catch (error) {
    console.error('购买失败:', error)
    alert('购买失败，请稍后再试')
  } finally {
    isPurchasing.value = false
  }
}

onMounted(() => {
  fetchKnowledgeDetail()
})
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <button
      @click="router.back()"
      class="btn btn-ghost btn-sm mb-6 flex items-center"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
      </svg>
      返回知识广场
    </button>

    <!-- 余额显示 -->
    <div class="bg-blue-50 px-4 py-2 rounded-full mb-6">
      <span class="text-blue-700 font-medium">我的余额: {{ userBalance }}元</span>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="text-center py-12 flex flex-col items-center">
      <span class="loading loading-spinner loading-lg text-primary"></span>
      <p class="mt-4 text-gray-500">加载知识详情中...</p>
    </div>

    <!-- 知识详情 -->
    <div v-else-if="knowledge" class="card bg-base-100 shadow-lg overflow-hidden">
      <div class="card-body p-6 sm:p-8">
        <div class="flex flex-col sm:flex-row justify-between items-start gap-4 mb-6">
          <h1 class="card-title text-2xl sm:text-3xl font-bold text-gray-900 break-words">
            {{ knowledge.title }}
          </h1>

          <div v-if="knowledge.encrypted"
               :class="['badge', knowledge.purchased ? 'badge-success' : 'badge-primary', 'gap-1']">
            <span>{{ knowledge.price }}元</span>
            <span v-if="knowledge.purchased" class="ml-1">✓</span>
          </div>
        </div>

        <!-- 标签 -->
        <div class="flex flex-wrap gap-2 mb-6">
          <span
            v-for="(tag, index) in knowledge.tags"
            :key="index"
            class="badge badge-outline text-gray-600 border-gray-300"
          >
            {{ tag }}
          </span>
        </div>

        <!-- 作者信息 -->
        <div class="flex items-center text-gray-500 mb-6">
          <div class="avatar placeholder">
            <div class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center">
              <span class="text-gray-600 font-medium">{{ knowledge.authorName.charAt(0) }}</span>
            </div>
          </div>
          <span class="ml-2">{{ knowledge.authorName }}</span>
          <span class="mx-2">•</span>
          <span>发布于 {{ knowledge.createdAt }}</span>
        </div>

        <!-- 加密内容提示 -->
        <div
          v-if="knowledge.encrypted && !knowledge.purchased"
          class="bg-yellow-50 border border-yellow-200 rounded-lg p-6 text-center mb-6"
        >
          <div class="flex justify-center mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <h2 class="text-xl font-semibold text-gray-800 mb-2">此知识已加密</h2>
          <p class="text-gray-600 mb-4">
            购买后可查看完整内容。购买后永久有效。
          </p>
          <button
            @click="handlePurchase"
            class="btn btn-primary w-full sm:w-auto"
            :disabled="isPurchasing || userBalance < knowledge.price"
          >
            <span v-if="isPurchasing" class="loading loading-spinner"></span>
            {{ isPurchasing ? '购买中...' : `购买查看 (¥${knowledge.price})` }}
            <span v-if="userBalance < knowledge.price" class="ml-2 text-xs text-red-500">余额不足</span>
          </button>
        </div>

        <!-- 知识内容 -->
        <div
          v-if="!knowledge.encrypted || knowledge.purchased"
          class="prose prose-sm max-w-none"
        >
          <component :is="renderTiptapContent(knowledge.content)" />
        </div>
      </div>
    </div>

    <!-- 知识不存在 -->
    <div v-else class="text-center py-12 bg-base-100 rounded-lg shadow">
      <div class="text-2xl font-bold text-gray-500 mb-4">知识不存在或已被删除</div>
      <button
        @click="router.push({ name: 'knowledgeSquare' })"
        class="btn btn-primary"
      >
        返回知识广场
      </button>
    </div>
  </div>
</template>
