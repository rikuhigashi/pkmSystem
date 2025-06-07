<!--知识广场页面-->
<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { MagnifyingGlassIcon } from '@heroicons/vue/20/solid'
import router from '@/router'
import { searchKnowledge, type KnowledgeItem } from '@/API/knowledge/knowledgeAPI'
import { userAuthStore } from '@/stores/auth'
import { createPaymentOrder, checkPaymentStatus } from '@/API/payment/paymentAPI'

const authStore = userAuthStore()
const knowledgeList = ref<KnowledgeItem[]>([])
const searchQuery = ref('')
const isLoading = ref(true)

// 获取用户余额
const userBalance = computed(() => authStore.userInfo?.balance || 0)

// 充值弹窗状态
const showRechargeModal = ref(false)
const rechargeAmount = ref(50) // 默认充值金额
const isRecharging = ref(false)

// 处理充值
const handleRecharge = async () => {
  try {
    isRecharging.value = true

    // 调用统一的支付接口
    const response = await createPaymentOrder(
      {
        amount: rechargeAmount.value,
        subject: `账户充值-${rechargeAmount.value}元`,
      },
      'recharge', // 指定支付类型为充值
    )

    if (!response.success) {
      throw new Error(response.error || '充值订单创建失败')
    }

    const orderNo = response.data.orderNo
    const payForm = response.data.payForm

    // 处理支付表单跳转
    const formContainer = document.createElement('div')
    formContainer.innerHTML = payForm
    document.body.appendChild(formContainer)

    const form = formContainer.querySelector('form')
    if (form) {
      form.submit()
    } else {
      throw new Error('支付表单解析失败')
    }

    // 支付状态轮询
    const maxAttempts = 30
    let attempts = 0

    const checkStatus = async () => {
      attempts++
      try {
        const statusRes = await checkPaymentStatus(orderNo)
        if (statusRes.success) {
          switch (statusRes.data.status) {
            case 'SUCCESS':
              clearInterval(interval)
              // 更新用户信息
              await authStore.fetchUserInfo()
              document.body.removeChild(formContainer)
              showRechargeModal.value = false
              alert(`充值成功！当前余额：${authStore.userInfo?.balance}元`)
              break
            case 'EXPIRED':
            case 'FAILED':
              alert('支付失败或超时')
              clearInterval(interval)
              document.body.removeChild(formContainer)
              break
          }
        }
        if (attempts >= maxAttempts) {
          clearInterval(interval)
          alert('支付状态查询超时')
          document.body.removeChild(formContainer)
        }
      } catch (error) {
        console.error('支付状态查询失败:', error)
      }
    }
    const interval = setInterval(checkStatus, 3000)
  } catch (error) {
    console.error('充值失败:', error)
    alert(`充值失败: ${error instanceof Error ? error.message : '未知错误'}`)
  } finally {
    isRecharging.value = false
  }
}

// 获取知识列表
const fetchKnowledgeList = async () => {
  try {
    isLoading.value = true
    const response = await searchKnowledge(searchQuery.value)
    knowledgeList.value = response.content
  } catch (error) {
    console.error('获取知识列表失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 查看知识详情
const viewKnowledge = (id: number) => {
  router.push({ name: 'knowledgeDetail', params: { id } })
}

onMounted(() => {
  fetchKnowledgeList()
  authStore.fetchUserInfo() // 获取用户信息
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- 顶部区域：标题、余额和充值按钮 -->
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-3xl font-bold text-gray-900">知识分享广场</h1>

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

    <!-- 搜索框 -->
    <div class="relative mb-6 max-w-md">
      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
        <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
      </div>
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索知识..."
        class="input input-bordered w-full pl-10"
        @input="fetchKnowledgeList"
      />
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

    <!-- 知识列表 -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="item in knowledgeList"
        :key="item.id"
        class="card bg-base-100 shadow-xl hover:shadow-2xl transition-shadow"
      >
        <div class="card-body">
          <div class="flex justify-between items-start">
            <h2 class="card-title">{{ item.title }}</h2>
            <div
              v-if="item.encrypted"
              :class="['badge', item.purchased ? 'badge-success' : 'badge-primary']"
            >
              {{ item.price }}元
              <span v-if="item.purchased" class="ml-1">✓</span>
            </div>
          </div>
          <p class="text-gray-500">作者: {{ item.authorName }}</p>

          <!-- 标签展示 -->
          <div class="flex flex-wrap gap-2 mt-2">
            <span v-for="(tag, index) in item.tags" :key="index" class="badge badge-outline">
              {{ tag }}
            </span>
          </div>

          <div class="card-actions justify-end mt-4">
            <button @click="viewKnowledge(item.id)" class="btn btn-outline btn-sm">查看详情</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
