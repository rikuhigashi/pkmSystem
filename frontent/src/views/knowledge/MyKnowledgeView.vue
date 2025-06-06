<!--我的知识库页面-->
<script lang="ts" setup>
import { ref } from 'vue'
import { DocumentTextIcon, LockClosedIcon } from '@heroicons/vue/24/outline'

// 模拟数据
const purchasedKnowledge = ref([
  { id: 1, title: 'Vue3 高级技巧', author: '张三', purchasedAt: '2023-06-18' },
  { id: 2, title: 'React性能优化', author: '李四', purchasedAt: '2023-06-10' }
])

const myKnowledge = ref([
  { id: 3, title: 'TypeScript 类型体操', author: '王五', createdAt: '2023-06-05' },
  { id: 4, title: 'Node.js 实战', author: '赵六', createdAt: '2023-05-28' }
])
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <h1 class="text-3xl font-bold text-gray-900 mb-8">我的知识库</h1>

    <div class="mb-12">
      <h2 class="text-xl font-semibold mb-4 flex items-center">
        <LockClosedIcon class="h-5 w-5 mr-2 text-primary" />
        已购知识
      </h2>

      <div v-if="purchasedKnowledge.length === 0" class="text-center py-12 border-2 border-dashed rounded-lg">
        <p class="text-gray-500">您尚未购买任何知识</p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div
          v-for="item in purchasedKnowledge"
          :key="item.id"
          class="card bg-base-100 shadow hover:shadow-md transition-shadow"
        >
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="text-gray-500">作者: {{ item.author }}</p>
            <p class="text-sm text-gray-400 mt-2">购买日期: {{ item.purchasedAt }}</p>
            <div class="card-actions justify-end mt-4">
              <button class="btn btn-ghost btn-sm">查看</button>
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

      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div
          v-for="item in myKnowledge"
          :key="item.id"
          class="card bg-base-100 shadow hover:shadow-md transition-shadow"
        >
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="text-gray-500">作者: {{ item.author }}</p>
            <p class="text-sm text-gray-400 mt-2">创建日期: {{ item.createdAt }}</p>
            <div class="card-actions justify-end mt-4">
              <button class="btn btn-ghost btn-sm">查看</button>
              <button class="btn btn-ghost btn-sm">编辑</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
