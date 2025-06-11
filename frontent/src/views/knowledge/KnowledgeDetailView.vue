<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { renderTiptapContent } from '@/utils/tiptapRenderer'
import {
  getKnowledgeDetail,
  type KnowledgeDetail,
  purchaseKnowledge,
} from '@/API/knowledge/knowledgeAPI'
import { userAuthStore } from '@/stores/auth'
import { useAlertStore } from '@/stores/alert'
import type { JSONContent } from '@tiptap/core'
import { formatDate } from '@/utils/dateUtils';

const authStore = userAuthStore()
const alertStore = useAlertStore()
const route = useRoute()
const router = useRouter()

const knowledgeId = ref(parseInt(route.params.id as string))
const knowledge = ref<KnowledgeDetail | null>(null)
const isLoading = ref(true)
const isPurchasing = ref(false)
const paymentStatus = ref<'idle' | 'pending' | 'success' | 'failed'>('idle')
const insufficientBalance = ref(false)

// 用户购买状态
const isPurchased = computed(() => {
  return authStore.hasPurchasedKnowledge(knowledgeId.value)
})

// 是否为知识作者
const isAuthor = computed(() => {
  return knowledge.value?.authorId === authStore.userInfo?.id
})

const userBalance = computed(() => authStore.userInfo?.balance || 0)

// 内容解析函数
const parseContent = (content: any): JSONContent => {
  if (typeof content === 'string') {
    try {
      return JSON.parse(content)
    } catch {
      return {
        type: 'doc',
        content: [
          {
            type: 'paragraph',
            content: [
              {
                type: 'text',
                text: content,
              },
            ],
          },
        ],
      }
    }
  }
  return content
}

const fetchKnowledgeDetail = async () => {
  try {
    isLoading.value = true
    const data = await getKnowledgeDetail(knowledgeId.value)

    // 内容解析处理
    if (typeof data.content === 'string') {
      try {
        data.content = JSON.parse(data.content)
      } catch {
        data.content = {
          type: 'doc',
          content: [{ type: 'paragraph', content: [{ type: 'text', text: data.content }] }],
        }
      }
    }

    data.content = parseContent(data.content)
    knowledge.value = data
  } catch (error) {
    console.error('加载知识详情失败:', error)
    alertStore.showAlert('加载知识详情失败，请稍后再试', 'error')
  } finally {
    isLoading.value = false
  }
}

const fetchUserInfo = async () => {
  try {
    await authStore.fetchUserInfo()
  } catch (error) {
    console.error('获取用户信息失败:', error)
    alertStore.showAlert('获取用户信息失败，请稍后再试', 'error')
  }
}

const purchaseWithBalance = async () => {
  if (!knowledge.value) return

  try {
    isPurchasing.value = true
    paymentStatus.value = 'pending'

    const newBalance = await purchaseKnowledge(knowledgeId.value)

    // 更新状态
    authStore.updateBalance(newBalance)
    authStore.addPurchasedKnowledge(knowledgeId.value)

    // 立即刷新知识详情
    await fetchKnowledgeDetail()

    paymentStatus.value = 'success'
    alertStore.showAlert('购买成功！您现在可以查看完整内容', 'success', 3000)
  } catch (error) {
    console.error('余额支付失败:', error)
    paymentStatus.value = 'failed'
    let errorMessage = '余额支付失败，请稍后再试'
    if (error instanceof Error) {
      errorMessage = error.message
    }
    alertStore.showAlert(errorMessage, 'error')
  } finally {
    isPurchasing.value = false
  }
}

const handlePurchase = async () => {
  if (!knowledge.value) return

  if (userBalance.value >= knowledge.value.price) {
    await purchaseWithBalance()
  } else {
    insufficientBalance.value = true
    paymentStatus.value = 'failed'
    alertStore.showAlert(
      `余额不足！需要 ${knowledge.value.price} 元，当前余额 ${userBalance.value} 元`,
      'error'
    )
  }
}

const goToRecharge = () => {
  insufficientBalance.value = false
  alertStore.hideAlert()
  router.push({
    name: 'knowledgeSquare',
    query: { recharge: 'true' },
  })
}

const refreshBalance = async () => {
  try {
    await fetchUserInfo()
    alertStore.showAlert('余额已刷新', 'success', 2000)
  } catch (error) {
    console.error('刷新余额失败:', error)
    alertStore.showAlert('刷新余额失败', 'error')
  }
}

// 监听路由变化
watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      knowledgeId.value = parseInt(newId as string)
      fetchKnowledgeDetail()
    }
  }
)

onMounted(() => {
  fetchKnowledgeDetail()
  fetchUserInfo()
})
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- 返回按钮 -->
    <button @click="router.back()" class="btn btn-ghost btn-sm mb-6 flex items-center">
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

    <!-- 余额显示 -->
    <div class="bg-blue-50 px-4 py-2 rounded-full mb-6 flex items-center justify-between">
      <div class="flex items-center">
        <span class="text-blue-700 font-medium">我的余额: {{ userBalance }}元</span>
        <button
          @click="refreshBalance"
          class="ml-3 p-1 rounded-full hover:bg-blue-100 transition-colors"
          title="刷新余额"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4 text-blue-600"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
            />
          </svg>
        </button>
      </div>
      <button @click="goToRecharge" class="btn btn-xs btn-link text-blue-700">去充值</button>
    </div>

    <!-- 支付状态提示 -->
    <div v-if="paymentStatus === 'success'" class="alert alert-success mb-6">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="stroke-current shrink-0 h-6 w-6"
        fill="none"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
        />
      </svg>
      <span>购买成功！您现在可以查看完整内容</span>
      <button @click="paymentStatus = 'idle'" class="btn btn-sm btn-ghost">关闭</button>
    </div>

    <div v-else-if="paymentStatus === 'failed'" class="alert alert-error mb-6">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="stroke-current shrink-0 h-6 w-6"
        fill="none"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
        />
      </svg>
      <span>购买失败，请重试或联系客服</span>
      <button @click="paymentStatus = 'idle'" class="btn btn-sm btn-ghost">关闭</button>
    </div>

    <!-- 余额不足提示 -->
    <div v-if="insufficientBalance" class="alert alert-warning mb-6">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="stroke-current shrink-0 h-6 w-6"
        fill="none"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
        />
      </svg>
      <span>余额不足！需要 {{ knowledge?.price }} 元，当前余额 {{ userBalance }} 元</span>
      <button @click="insufficientBalance = false" class="btn btn-sm btn-ghost">关闭</button>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="text-center py-12 flex flex-col items-center">
      <span class="loading loading-spinner loading-lg text-primary"></span>
      <p class="mt-4 text-gray-500">加载知识详情中...</p>
    </div>

    <!-- 知识详情 -->
    <div v-else-if="knowledge" class="card bg-base-100 shadow-lg overflow-hidden">
      <div class="card-body p-6 sm:p-8">
        <!-- 标题区域 -->
        <div class="flex flex-col sm:flex-row justify-between items-start gap-4 mb-6">
          <div class="flex items-start gap-3">
            <h1 class="card-title text-2xl sm:text-3xl font-bold text-gray-900 break-words">
              {{ knowledge.title }}
            </h1>

            <!-- 作者标识 -->
            <div v-if="isAuthor" class="badge badge-lg badge-success gap-2 mt-1.5">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              我的知识
            </div>
          </div>

          <!-- 价格徽章 -->
          <div
            v-if="knowledge.encrypted"
            :class="['badge', (isAuthor || isPurchased) ? 'badge-success' : 'badge-primary', 'gap-1']"
          >
            <span>{{ knowledge.price }}元</span>
            <span v-if="isAuthor || isPurchased" class="ml-1">✓</span>
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
          <span>发布于 {{ formatDate(knowledge.createdAt) }}</span>
        </div>

        <!-- 加密内容提示 - 作者自动拥有权限 -->
        <div
          v-if="knowledge.encrypted && !isAuthor && !isPurchased"
          class="bg-yellow-50 border border-yellow-200 rounded-lg p-6 text-center mb-6"
        >
          <div class="flex justify-center mb-4">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-12 w-12 text-yellow-500"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
              />
            </svg>
          </div>
          <h2 class="text-xl font-semibold text-gray-800 mb-2">此知识已加密</h2>
          <p class="text-gray-600 mb-4">购买后可查看完整内容。购买后永久有效。</p>
          <button
            @click="handlePurchase"
            class="btn btn-primary w-full sm:w-auto mb-2"
            :disabled="isPurchasing"
          >
            <span v-if="isPurchasing" class="loading loading-spinner"></span>
            {{ isPurchasing ? '购买中...' : `购买查看 (¥${knowledge.price})` }}
          </button>

          <!-- 余额不足提示 -->
          <div v-if="userBalance < knowledge.price" class="mt-3">
            <p class="text-red-500 text-sm mb-2">余额不足，请先充值</p>
            <button @click="goToRecharge" class="btn btn-sm btn-outline btn-primary">
              立即充值
            </button>
          </div>
        </div>

        <!-- 知识内容 - 作者自动拥有权限 -->
        <div v-if="!knowledge.encrypted || isAuthor || isPurchased" class="prose prose-sm max-w-none">
          <component :is="renderTiptapContent(knowledge.content)" />
        </div>
      </div>
    </div>

    <!-- 知识不存在 -->
    <div v-else class="text-center py-12 bg-base-100 rounded-lg shadow">
      <div class="text-2xl font-bold text-gray-500 mb-4">知识不存在或已被删除</div>
      <button @click="router.push({ name: 'knowledgeSquare' })" class="btn btn-primary">
        返回知识广场
      </button>
    </div>
  </div>
</template>
