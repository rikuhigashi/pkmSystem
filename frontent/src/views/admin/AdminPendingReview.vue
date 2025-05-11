<template>
  <div class="container mx-auto p-6">
    <!-- 导航和标题 -->
    <div class="flex justify-between items-center mb-6">
      <router-link
        to="/adminDashboard"
        class="px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition-colors"
      >
        ← 返回用户管理
      </router-link>
      <h1 class="text-3xl font-bold text-gray-800">待审核内容管理</h1>
      <div class="flex gap-4">
        <button
          @click="fetchPendingData"
          class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
        >
          <span v-if="!loading">刷新数据</span>
          <span v-else>加载中...</span>
        </button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-semibold text-gray-700">名称</th>
            <th class="px-6 py-3 text-left text-sm font-semibold text-gray-700">创建时间</th>
            <th class="px-6 py-3 text-left text-sm font-semibold text-gray-700">状态</th>
            <th class="px-6 py-3 text-left text-sm font-semibold text-gray-700 w-48">操作</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="item in pendingData" :key="item.id">
            <td class="px-6 py-4 text-sm text-gray-900">{{ item.name }}</td>
            <td class="px-6 py-4 text-sm text-gray-500">{{ formatDate(item.createdAt) }}</td>
            <td class="px-6 py-4">
              <span
                :class="statusBadgeClass(item.status)"
                class="px-2 py-1 rounded-full text-xs font-medium"
              >
                {{ getStatusText(item.status) }}
              </span>
            </td>
            <td class="px-6 py-4 space-x-2">
              <button
                @click="openPreview(item)"
                class="px-3 py-1.5 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors"
              >
                查看
              </button>
              <button
                @click="handleApprove(item.id)"
                :disabled="processing"
                class="px-3 py-1.5 bg-green-100 text-green-700 rounded-md hover:bg-green-200 disabled:opacity-50 transition-colors"
              >
                批准
              </button>
              <button
                @click="handleReject(item.id)"
                :disabled="processing"
                class="px-3 py-1.5 bg-red-100 text-red-700 rounded-md hover:bg-red-200 disabled:opacity-50 transition-colors"
              >
                拒绝
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="flex items-center gap-4 mb-4">
        <!-- 每页条数选择 -->
        <select v-model="pageSize" @change="currentPage = 1" class="border p-2 rounded">
          <option value="10">10条/页</option>
          <option value="20">20条/页</option>
          <option value="50">50条/页</option>
        </select>

        <!-- 分页按钮 -->
        <button
          v-for="page in totalPages"
          :key="page"
          @click="currentPage = page"
          :class="{ 'bg-blue-500 text-white': currentPage === page }"
          class="px-3 py-1 rounded"
        >
          {{ page }}
        </button>
      </div>
    </div>

    <!-- 空状态提示 -->
    <div v-if="pendingData.length === 0" class="text-center py-12">
      <p class="text-gray-500 text-lg">当前没有待审核的内容</p>
    </div>

    <!-- 预览模态框 -->
    <div
      v-if="previewData"
      class="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center p-4"
    >
      <div class="bg-white rounded-xl shadow-2xl w-full max-w-4xl max-h-[90vh] flex flex-col">
        <div class="flex justify-between items-center p-6 border-b">
          <h2 class="text-xl font-semibold">{{ previewData.name }}</h2>
          <button
            @click="closePreview"
            class="text-gray-500 hover:text-gray-700 p-2 rounded-full hover:bg-gray-100"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <div class="flex-1 overflow-auto p-6">
          <TiptapPreview
            :content="previewData.tiptapJson"
            :loading="previewLoading"
            class="prose max-w-none"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, watch} from 'vue'
import {approveData, getPendingData, rejectData} from '@/API/admin/adminAPI'
import TiptapPreview from '@/components/admin/TiptapPreview.vue'

interface PendingData {
  id: number
  name: string
  href: string
  status: number
  tiptapJson: Record<string, unknown>
  createdAt: string
}

const pendingData = ref<PendingData[]>([])
const previewData = ref<PendingData | null>(null)
const processing = ref(false)
const loading = ref(false)
const previewLoading = ref(false)

const searchKeyword = ref('')
const currentPage = ref(1)
const filterStatus = ref(0) // 0=待审核, 1=已处理


const pageSize = ref(10)
const totalPages = ref(0)

// 获取待审核数据
const fetchPendingData = async () => {
  try {
    loading.value = true

    const res = await getPendingData({
      keyword: searchKeyword.value,
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value
    })
    // console.log(res.data)
    if (res.success) {
      pendingData.value = res.data.content
      totalPages.value =res.data.totalPages
    }
  } catch (error) {
    console.error('获取待审核数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开预览
const openPreview = async (item: PendingData) => {
  try {
    previewLoading.value = true
    previewData.value = item
  } catch (error) {
    console.error('加载内容失败:', error)
  } finally {
    previewLoading.value = false
  }
}

// 关闭预览
const closePreview = () => {
  previewData.value = null
}

// 状态样式
const statusBadgeClass = (status: number) => {
  return {
    0: 'bg-yellow-100 text-yellow-800',
    1: 'bg-green-100 text-green-800',
    2: 'bg-red-100 text-red-800',
  }[status]
}

// 日期格式化
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

// 批准数据
const handleApprove = async (id: number) => {
  processing.value = true
  try {
    const res = await approveData(id)
    if (res.success) {
      await fetchPendingData() // 刷新数据
    }
  } catch (error) {
    console.error('批准操作失败:', error)
  } finally {
    processing.value = false
  }
}

// 拒绝数据
const handleReject = async (id: number) => {
  processing.value = true
  try {
    const res = await rejectData(id)
    if (res.success) {
      await fetchPendingData() // 刷新数据
    }
  } catch (error) {
    console.error('拒绝操作失败:', error)
  } finally {
    processing.value = false
  }
}

// 状态文本映射
const getStatusText = (status: number) => {
  const statusMap: { [key in 0 | 1 | 2]: string } = {
    0: '待审核',
    1: '已批准',
    2: '已拒绝',
  }
  return statusMap[status as 0 | 1 | 2] || '未知状态'
}

onMounted(() => {
  fetchPendingData()
})

// onUpdated(()=>{
//   fetchPendingData()
// })

watch(
  [searchKeyword, currentPage, pageSize, filterStatus],
  ([newKeyword, newPage, newStatus], [oldKeyword, oldPage, oldStatus]) => {
    // 避免重复请求
    if (newKeyword !== oldKeyword || newPage !== oldPage || newStatus !== oldStatus) {
      fetchPendingData()
    }
  },
  {deep: true},
)
</script>
