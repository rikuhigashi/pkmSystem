<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { renderTiptapContent } from '@/utils/tiptapRenderer'
import type { JSONContent } from '@tiptap/core'

const route = useRoute()
const router = useRouter()
const knowledgeId = ref(route.params.id as string)

// 知识详情数据结构
interface KnowledgeDetail {
  id: string;
  title: string;
  author: string;
  createdAt: string;
  price: number;
  isEncrypted: boolean;
  purchased: boolean;
  tags: string[];
  content: JSONContent;
}

// 模拟知识数据
const knowledge = ref<KnowledgeDetail | null>(null)
const isLoading = ref(true)
const isPurchasing = ref(false)

// 模拟API获取知识详情
const fetchKnowledgeDetail = async (id: string) => {
  // 模拟API请求
  await new Promise(resolve => setTimeout(resolve, 500))

  // 模拟数据
  const mockData: KnowledgeDetail[] = [
    {
      id: "1",
      title: "Vue3 高级技巧",
      author: "张三",
      createdAt: "2023-06-15",
      price: 9.9,
      isEncrypted: true,
      purchased: false,
      tags: ["Vue", "前端", "JavaScript"],
      content: {
        type: "doc",
        content: [
          {
            type: "heading",
            attrs: { level: 2 },
            content: [{ type: "text", text: "Vue3 响应式原理深度解析" }]
          },
          {
            type: "paragraph",
            content: [
              { type: "text", text: "Vue3 使用 " },
              { type: "text", marks: [{ type: "bold" }], text: "Proxy" },
              { type: "text", text: " 代替了 Vue2 中的 " },
              { type: "text", marks: [{ type: "code" }], text: "Object.defineProperty" },
              { type: "text", text: "，这带来了以下优势：" }
            ]
          },
          {
            type: "bulletList",
            content: [
              {
                type: "listItem",
                content: [{ type: "paragraph", content: [{ type: "text", text: "更好的性能" }] }]
              },
              {
                type: "listItem",
                content: [{ type: "paragraph", content: [{ type: "text", text: "支持数组索引变化" }] }]
              },
              {
                type: "listItem",
                content: [{ type: "paragraph", content: [{ type: "text", text: "支持 Map/Set 等新数据结构" }] }]
              }
            ]
          },
          {
            type: "heading",
            attrs: { level: 3 },
            content: [{ type: "text", text: "组合式 API 最佳实践" }]
          },
          {
            type: "paragraph",
            content: [
              { type: "text", text: "组合式 API 是 Vue3 最重要的特性之一，它允许我们更好地组织代码：" }
            ]
          },
          {
            type: "codeBlock",
            attrs: { language: "typescript" },
            content: [{
              type: "text",
              text: `import { ref, computed } from 'vue'

export default function useCounter() {
  const count = ref(0)
  const double = computed(() => count.value * 2)

  function increment() {
    count.value++
  }

  return { count, double, increment }
}`
            }]
          }
        ]
      }
    },
    {
      id: "2",
      title: "TypeScript 类型体操",
      author: "李四",
      createdAt: "2023-06-10",
      price: 0,
      isEncrypted: false,
      purchased: true,
      tags: ["TypeScript", "前端"],
      content: {
        type: "doc",
        content: [
          {
            type: "heading",
            attrs: { level: 2 },
            content: [{ type: "text", text: "高级类型技巧" }]
          },
          {
            type: "paragraph",
            content: [
              { type: "text", text: "TypeScript 的类型系统非常强大，可以完成许多复杂的类型操作。" }
            ]
          }
        ]
      }
    }
  ]

  return mockData.find(item => item.id === id) || null
}

// 购买知识
const purchaseKnowledge = async () => {
  if (!knowledge.value) return

  isPurchasing.value = true
  try {
    // 模拟购买API请求
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 更新购买状态
    knowledge.value.purchased = true

    // 显示成功消息
    alert(`已成功购买 "${knowledge.value.title}"，现在可以查看完整内容了！`)
  } catch (error) {
    console.error('购买失败:', error)
    alert('购买失败，请稍后再试')
  } finally {
    isPurchasing.value = false
  }
}

onMounted(async () => {
  try {
    knowledge.value = await fetchKnowledgeDetail(knowledgeId.value)
  } catch (error) {
    console.error('加载知识详情失败:', error)
  } finally {
    isLoading.value = false
  }
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

          <div v-if="knowledge.isEncrypted"
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
              <span class="text-gray-600 font-medium">{{ knowledge.author.charAt(0) }}</span>
            </div>
          </div>
          <span class="ml-2">{{ knowledge.author }}</span>
          <span class="mx-2">•</span>
          <span>发布于 {{ knowledge.createdAt }}</span>
        </div>

        <!-- 加密内容提示 -->
        <div
          v-if="knowledge.isEncrypted && !knowledge.purchased"
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
            @click="purchaseKnowledge"
            class="btn btn-primary w-full sm:w-auto"
            :disabled="isPurchasing"
          >
            <span v-if="isPurchasing" class="loading loading-spinner"></span>
            {{ isPurchasing ? '购买中...' : `购买查看 (¥${knowledge.price})` }}
          </button>
        </div>

        <!-- 知识内容 -->
        <div
          v-if="!knowledge.isEncrypted || knowledge.purchased"
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
