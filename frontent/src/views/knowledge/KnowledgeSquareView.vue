<!--知识广场页面-->
<script lang="ts" setup>
import { ref } from 'vue'
import { MagnifyingGlassIcon } from '@heroicons/vue/20/solid'
import router from '@/router'

// 模拟知识数据
const knowledgeList = ref([
  {
    id: 1,
    title: 'Vue3 高级技巧',
    author: '张三',
    price: 9.9,
    isEncrypted: true,
    purchased: false,
  },
  {
    id: 2,
    title: 'TypeScript 类型体操',
    author: '李四',
    price: 0,
    isEncrypted: false,
    purchased: true,
  },
  {
    id: 3,
    title: 'Tailwind CSS 实战',
    author: '王五',
    price: 12.5,
    isEncrypted: true,
    purchased: false,
  },
])

const searchQuery = ref('')

const viewKnowledge = (id: number) => {
  router.push({ name: 'knowledgeDetail', params: { id } })
}

// 新增购买功能
const purchaseKnowledge = (id: number) => {
  const knowledge = knowledgeList.value.find(item => item.id === id)
  if (knowledge) {
    // 模拟购买过程
    knowledge.purchased = true
    // 这里可以添加实际的购买逻辑，如打开支付弹窗等
    alert(`已成功购买 "${knowledge.title}"，现在可以查看内容了！`)
    // 购买后自动跳转到详情页
    viewKnowledge(id)
  }
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-3xl font-bold text-gray-900">知识分享广场</h1>
      <router-link
        to="/knowledge/upload"
        class="btn btn-primary"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        分享知识
      </router-link>
    </div>

    <div class="relative mb-6 max-w-md">
      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
        <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
      </div>
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索知识..."
        class="input input-bordered w-full pl-10"
      />
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="item in knowledgeList"
        :key="item.id"
        class="card bg-base-100 shadow-xl hover:shadow-2xl transition-shadow"
      >
        <div class="card-body">
          <div class="flex justify-between items-start">
            <h2 class="card-title">{{ item.title }}</h2>
            <div
              v-if="item.isEncrypted"
              class="badge badge-primary"
              :class="{'badge-success': item.purchased}"
            >
              {{ item.price }}元
              <span v-if="item.purchased" class="ml-1">✓</span>
            </div>
          </div>
          <p class="text-gray-500">作者: {{ item.author }}</p>

          <div class="card-actions justify-end mt-4">
            <button
              v-if="item.isEncrypted && !item.purchased"
              class="btn btn-primary btn-sm"
              @click="purchaseKnowledge(item.id)"
            >
              购买查看
            </button>
            <button
              v-else
              @click="viewKnowledge(item.id)"
              class="btn btn-outline btn-sm"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
