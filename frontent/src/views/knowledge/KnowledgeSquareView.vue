<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { MagnifyingGlassIcon } from '@heroicons/vue/20/solid'
import router from '@/router'
import { searchKnowledge, type KnowledgeItem } from '@/API/knowledge/knowledgeAPI'
import { userAuthStore } from '@/stores/auth'
import { usePaymentStore } from '@/stores/payment'
import { useRoute } from 'vue-router'
import { useAlertStore } from '@/stores/alert'
import { formatDate } from '@/utils/dateUtils';

const route = useRoute()
const authStore = userAuthStore()
const paymentStore = usePaymentStore()
const alertStore = useAlertStore()

const knowledgeList = ref<KnowledgeItem[]>([])
const searchQuery = ref('')
const isLoading = ref(true)
const showRechargeModal = ref(false)
const rechargeAmount = ref(50)
const isRecharging = ref(false)

// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = ref(0)

// 排序选项类型
type SortOption = 'newest' | 'oldest' | 'price_low' | 'price_high';
// 排序状态
const sortOption = ref<SortOption>('newest');

const userBalance = computed(() => authStore.userInfo?.balance || 0)

// 防抖搜索计时器
let searchTimeout: ReturnType<typeof setTimeout> | null = null

// 获取知识列表
const fetchKnowledgeList = async () => {
  try {
    isLoading.value = true

    // 构建排序参数
    let sortParams = {};
    switch (sortOption.value) {
      case 'newest':
        sortParams = { sortField: 'createdAt', sortDirection: 'DESC' };
        break;
      case 'oldest':
        sortParams = { sortField: 'createdAt', sortDirection: 'ASC' };
        break;
      case 'price_low':
        sortParams = { sortField: 'price', sortDirection: 'ASC' };
        break;
      case 'price_high':
        sortParams = { sortField: 'price', sortDirection: 'DESC' };
        break;
    }


    const response = await searchKnowledge(
      searchQuery.value,
      currentPage.value - 1, // 后端页码从0开始
      pageSize.value,
      sortParams.sortField,
      sortParams.sortDirection
    )

    // 更新列表项的购买状态
    knowledgeList.value = response.content.map(item => {
      const purchased = authStore.hasPurchasedKnowledge(item.id)
      return {
        ...item,
        purchased: purchased || item.authorId === authStore.userInfo?.id
      }
    })

    totalItems.value = response.totalElements
    totalPages.value = response.totalPages
  } catch (error: unknown) {
    console.error('获取知识列表失败:', error)
    if (error instanceof Error) {
      alertStore.showAlert(`获取知识列表失败: ${error.message}`, 'error', 5000)
    } else {
      alertStore.showAlert('获取知识列表失败，请稍后再试', 'error', 5000)
    }
  } finally {
    isLoading.value = false
  }
}

// 排序变更
const handleSortChange = () => {
  currentPage.value = 1;
  fetchKnowledgeList();
};

const handleSearch = () => {
  // 清除之前的定时器
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  // 设置新的防抖定时器
  searchTimeout = setTimeout(() => {
    currentPage.value = 1 // 搜索时重置到第一页
    fetchKnowledgeList()
  }, 300) // 300ms防抖
}

// 查看知识详情
const viewKnowledge = (id: number) => {
  router.push({ name: 'knowledgeDetail', params: { id } })
}

// 导航到我的知识库
const goToMyKnowledge = () => {
  router.push({ name: 'myKnowledge' })
}

// 处理分页大小变更
const handlePageSizeChange = () => {
  currentPage.value = 1
  fetchKnowledgeList()
}

// 处理充值
const handleRecharge = async () => {
  try {
    isRecharging.value = true

    // 创建充值订单
    const payForm = await paymentStore.createOrder(
      rechargeAmount.value,
      'recharge',
      `账户充值-${rechargeAmount.value}元`
    )

    // 处理支付表单
    const formContainer = document.createElement('div')
    formContainer.innerHTML = payForm
    document.body.appendChild(formContainer)
    const paymentForm = formContainer.querySelector('form')

    if (!paymentForm) {
      throw new Error('支付表单解析失败')
    }

    paymentForm.submit()

    // 轮询支付状态
    const maxAttempts = 30
    let attempts = 0

    const pollPaymentStatus = async () => {
      if (!paymentStore.currentOrder) return

      try {
        const statusResponse = await paymentStore.checkOrderStatus(paymentStore.currentOrder.orderNo)

        if (statusResponse.status === 'SUCCESS') {
          // 更新用户信息
          await authStore.fetchUserInfo()
          // 移除表单容器
          document.body.removeChild(formContainer)
          showRechargeModal.value = false
          alertStore.showAlert(
            `充值成功！当前余额：${authStore.userInfo?.balance}元`,
            'success'
          )
        } else if (statusResponse.status === 'EXPIRED' || statusResponse.status === 'FAILED') {
          throw new Error('支付失败或超时')
        }

        if (attempts < maxAttempts && statusResponse.status !== 'SUCCESS') {
          setTimeout(pollPaymentStatus, 3000)
        } else if (statusResponse.status !== 'SUCCESS') {
          throw new Error('支付状态查询超时')
        }
      } catch (error: unknown) {
        let errorMessage = '支付状态查询失败'
        if (error instanceof Error) {
          errorMessage = error.message
        }
        console.error(errorMessage)
        document.body.removeChild(formContainer)
      }

      attempts++
    }

    // 开始轮询
    setTimeout(pollPaymentStatus, 3000)
  } catch (error: unknown) {
    let errorMessage = '充值失败'
    if (error instanceof Error) {
      errorMessage = error.message
    }
    console.error(errorMessage)
    alertStore.showAlert(`充值失败: ${errorMessage}`, 'error', 5000)
  } finally {
    isRecharging.value = false
  }
}

// 监听路由变化，处理充值参数
watch(() => route.query, (newQuery) => {
  if (newQuery.recharge === 'true') {
    showRechargeModal.value = true
  }
})

// 监听页码变化
watch(currentPage, () => {
  fetchKnowledgeList()
})

// 监听分页大小变化
watch(pageSize, () => {
  handlePageSizeChange()
})


onMounted(() => {
  fetchKnowledgeList()
  authStore.fetchUserInfo() // 获取用户信息
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- 顶部区域：标题、余额和按钮 -->
    <div class="flex flex-col md:flex-row md:justify-between md:items-center gap-4 mb-8">
      <div>
        <h1 class="text-2xl md:text-3xl font-bold text-gray-900">知识分享广场</h1>
        <p class="text-sm text-gray-500 mt-1">发现和分享专业知识</p>
      </div>

      <div class="flex flex-wrap gap-3 items-center">
        <!-- 我的知识库按钮 -->
        <button
          @click="goToMyKnowledge"
          class="btn btn-outline btn-primary flex items-center"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          我的知识库
        </button>

        <div class="flex items-center space-x-4">
          <div class="bg-blue-50 px-4 py-2 rounded-full flex items-center">
            <span class="text-blue-700 font-medium">余额: {{ userBalance }}元</span>
          </div>
          <button @click="showRechargeModal = true" class="btn btn-primary">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 mr-1"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M4 4a2 2 0 00-2 2v4a2 2 0 002 2V6h10a2 2 0 00-2-2H4zm2 6a2 2 0 012-2h8a2 2 0 012 2v4a2 2 0 01-2 2H8a2 2 0 01-2-2v-4zm6 4a2 2 0 100-4 2 2 0 000 4z"
                clip-rule="evenodd"
              />
            </svg>
            充值
          </button>
        </div>
      </div>
    </div>

    <!-- 搜索、排序和分页控制 -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
      <!-- 搜索框 -->
      <div class="relative max-w-md w-full">
        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
        </div>
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索知识..."
          class="input input-bordered w-full pl-10"
          @input="handleSearch"
        />
      </div>

      <div class="flex flex-wrap gap-4">
        <!-- 排序下拉框 -->
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-600">排序:</span>
          <select
            v-model="sortOption"
            @change="handleSortChange"
            class="select select-bordered select-sm"
          >
            <option value="newest">最新优先</option>
            <option value="oldest">最旧优先</option>
            <option value="price_low">价格从低到高</option>
            <option value="price_high">价格从高到低</option>
          </select>
        </div>

        <!-- 分页大小选择 -->
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-600">每页显示:</span>
          <select
            v-model="pageSize"
            class="select select-bordered select-sm"
          >
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 充值模态框 -->
    <div v-if="showRechargeModal" class="modal modal-open">
      <div class="modal-box max-w-md">
        <h3 class="font-bold text-lg mb-4">账户充值</h3>

        <div class="space-y-4">
          <div class="form-control">
            <label class="label">
              <span class="label-text">充值金额 (元)</span>
            </label>
            <input
              v-model.number="rechargeAmount"
              type="number"
              min="10"
              step="10"
              class="input input-bordered w-full"
            />
          </div>

          <div class="grid grid-cols-3 gap-2 mt-2">
            <button
              @click="rechargeAmount = 10"
              :class="['btn', rechargeAmount === 10 ? 'btn-primary' : 'btn-outline']"
            >
              10元
            </button>
            <button
              @click="rechargeAmount = 50"
              :class="['btn', rechargeAmount === 50 ? 'btn-primary' : 'btn-outline']"
            >
              50元
            </button>
            <button
              @click="rechargeAmount = 100"
              :class="['btn', rechargeAmount === 100 ? 'btn-primary' : 'btn-outline']"
            >
              100元
            </button>
          </div>
        </div>

        <div class="modal-action">
          <button @click="showRechargeModal = false" class="btn btn-ghost">取消</button>
          <button @click="handleRecharge" :disabled="isRecharging" class="btn btn-primary">
            <span v-if="isRecharging" class="loading loading-spinner"></span>
            {{ isRecharging ? '充值中...' : '确认充值' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 知识列表加载状态 -->
    <div v-if="isLoading" class="text-center py-12">
      <span class="loading loading-spinner loading-lg text-primary"></span>
      <p class="mt-4 text-gray-500">加载知识列表中...</p>
    </div>

    <!-- 空状态提示 -->
    <div
      v-else-if="knowledgeList.length === 0"
      class="text-center py-12 bg-base-100 rounded-xl border border-base-200"
    >
      <div class="flex justify-center mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>
      <h3 class="text-lg font-medium text-gray-700 mb-2">未找到相关知识</h3>
      <p class="text-gray-500 max-w-md mx-auto">
        当前没有匹配"{{ searchQuery }}"的知识内容，请尝试其他搜索关键词
      </p>
    </div>

    <!-- 知识列表 -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="item in knowledgeList"
        :key="item.id"
        class="card bg-base-100 shadow-xl hover:shadow-2xl transition-shadow duration-300 border border-base-200"
      >
        <div class="card-body">
          <div class="flex justify-between items-start">
            <div>
              <h2 class="card-title text-base md:text-lg">{{ item.title }}</h2>

              <!-- 发布时间 -->
              <p class="text-xs text-gray-400 mt-1">
                {{ formatDate(item.createdAt) }}
              </p>

              <!-- 作者标识 -->
              <div v-if="item.authorId === authStore.userInfo?.id" class="badge badge-sm badge-success mt-1 gap-1">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                我的
              </div>
            </div>
            <div
              v-if="item.encrypted"
              :class="['badge', item.purchased ? 'badge-success' : 'badge-primary']"
            >
              {{ item.price }}元
              <span v-if="item.purchased" class="ml-1">✓</span>
            </div>
          </div>
          <p class="text-gray-500 text-sm">作者: {{ item.authorName }}</p>

          <!-- 标签展示 -->
          <div class="flex flex-wrap gap-2 mt-2 max-h-20 overflow-y-auto py-1">
            <span
              v-for="(tag, index) in item.tags"
              :key="index"
              class="badge badge-outline badge-sm"
            >
              {{ tag }}
            </span>
          </div>

          <div class="card-actions justify-end mt-4">
            <button
              @click="viewKnowledge(item.id)"
              class="btn btn-outline btn-sm"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页控件 -->
    <div v-if="totalPages > 1" class="flex flex-col sm:flex-row items-center justify-between gap-4 mt-8">
      <div class="text-sm text-gray-500">
        显示 {{ (currentPage - 1) * pageSize + 1 }} -
        {{ Math.min(currentPage * pageSize, totalItems) }} 条，共 {{ totalItems }} 条
      </div>

      <div class="join">
        <button
          class="join-item btn btn-sm md:btn-md"
          :class="{ 'btn-disabled': currentPage === 1 }"
          @click="currentPage = 1"
          :disabled="currentPage === 1"
        >
          首页
        </button>
        <button
          class="join-item btn btn-sm md:btn-md"
          :class="{ 'btn-disabled': currentPage === 1 }"
          @click="currentPage--"
          :disabled="currentPage === 1"
        >
          «
        </button>
        <button class="join-item btn btn-sm md:btn-md">
          第 {{ currentPage }} 页
        </button>
        <button
          class="join-item btn btn-sm md:btn-md"
          :class="{ 'btn-disabled': currentPage >= totalPages }"
          @click="currentPage++"
          :disabled="currentPage >= totalPages"
        >
          »
        </button>
        <button
          class="join-item btn btn-sm md:btn-md"
          :class="{ 'btn-disabled': currentPage >= totalPages }"
          @click="currentPage = totalPages"
          :disabled="currentPage >= totalPages"
        >
          尾页
        </button>
      </div>
    </div>
  </div>
</template>
