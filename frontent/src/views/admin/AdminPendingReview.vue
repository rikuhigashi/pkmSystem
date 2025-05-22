<template>
  <div class="container mx-auto p-6 space-y-6">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <h1 class="text-2xl font-bold">待审核数据</h1>

      <div class="flex flex-col gap-2 sm:flex-row sm:items-center">
        <div class="join w-full sm:w-96">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索名称"
            class="input input-bordered join-item w-full"
            @keyup.enter="fetchData"
          />
          <button class="btn btn-primary join-item" @click="fetchData">
            搜索
          </button>
        </div>

        <button
          class="btn btn-ghost btn-square"
          @click="fetchData"
          :disabled="loading"
        >
          <icon-refresh
            v-if="!loading"
            class="size-6 text-gray-600 hover:text-primary"
          />
          <span v-else class="loading loading-spinner"></span>
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center p-8">
      <span class="loading loading-dots loading-lg"></span>
    </div>

    <!-- 空状态 -->
    <div v-else-if="dataList.length === 0" class="text-center p-8 text-gray-500">
      暂无待审核数据
    </div>

    <!-- 数据表格 -->
    <div v-else class="space-y-4">
      <div class="overflow-x-auto rounded-lg border">
        <table class="table table-zebra">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3">ID</th>
            <th class="px-4 py-3">名称</th>
            <th class="px-4 py-3">图标</th>
            <th class="px-4 py-3">过期时间</th>
            <th class="px-4 py-3">状态</th>
            <th class="px-4 py-3">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="item in dataList" :key="item.id">
            <td class="px-4 py-3">{{ item.id }}</td>
            <td class="px-4 py-3">{{ item.name }}</td>
            <td class="px-4 py-3">
              <div class="flex items-center gap-1">
                <span v-if="item.icon" class="text-xl">{{ item.icon }}</span>
                <span v-else class="text-gray-400">无图标</span>
              </div>
            </td>
            <td class="px-4 py-3">{{ formatDate(item.expiredAt) }}</td>
            <td class="px-4 py-3">
                <span
                  class="px-2 py-1 rounded-md text-sm"
                  :class="{
                    'bg-orange-100 text-orange-800': item.status === 'PENDING',
                    'bg-green-100 text-green-800': item.status === 'APPROVED',
                    'bg-red-100 text-red-800': item.status === 'REJECTED'
                  }"
                >
                  {{ item.status }}
                </span>
            </td>
            <td class="px-4 py-3">
              <div class="flex gap-2">
                <button
                  class="btn btn-sm btn-success"
                  :disabled="item.status !== 'PENDING'"
                  @click="handleApprove(item.id)"
                >
                  同意
                </button>
                <button
                  class="btn btn-sm btn-error"
                  :disabled="item.status !== 'PENDING'"
                  @click="handleReject(item.id)"
                >
                  拒绝
                </button>
                <button
                  class="btn btn-sm btn-info"
                  @click="openDetail(item)"
                >
                  查看
                </button>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="flex justify-center">
        <div class="join">
          <button
            v-for="page in totalPages"
            :key="page"
            class="join-item btn"
            :class="{ 'btn-active': currentPage === page }"
            @click="changePage(page)"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>

    <!-- 模态框 -->
    <dialog ref="modal" class="modal" @click="handleOutsideClick">
      <div class="modal-box max-w-4xl">
        <h3 class="text-lg font-bold mb-4">详情预览 - {{ selectedItem?.name }}</h3>
        <div v-if="selectedItem" class="prose max-w-none">
          <TiptapViewer :content="selectedItem.tiptapJson" />
        </div>
        <div class="modal-action">
          <button class="btn" @click="closeModal">关闭</button>
        </div>
      </div>
    </dialog>
  </div>
  <RejectDialog ref="rejectDialog" @confirmed="handleRejectConfirm" />
</template>


<!--<template>-->
<!--  <div class="container mx-auto p-6 space-y-8">-->
<!--    <div class="flex justify-between items-center">-->
<!--      <h1 class="text-2xl font-bold">待审核数据</h1>-->
<!--      <div class="flex items-center gap-4">-->
<!--        <button-->
<!--          class="btn btn-ghost btn-circle"-->
<!--          @click="fetchData"-->
<!--          :disabled="loading"-->
<!--        >-->
<!--          <icon-refresh-->
<!--            v-if="!loading"-->
<!--            class="w-6 h-6 text-gray-600 hover:text-primary"-->
<!--          />-->
<!--          <span v-else class="loading loading-spinner"></span>-->
<!--        </button>-->

<!--        <div class="join">-->
<!--          <input-->
<!--            v-model="searchKeyword"-->
<!--            type="text"-->
<!--            placeholder="搜索名称"-->
<!--            class="input input-bordered join-item"-->
<!--            @keyup.enter="fetchData"-->
<!--          />-->
<!--          <button class="btn btn-primary join-item" @click="fetchData">搜索</button>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
<!--    &lt;!&ndash; 加载状态 &ndash;&gt;-->
<!--    <div v-if="loading" class="text-center">-->
<!--      <span class="loading loading-dots loading-lg"></span>-->
<!--    </div>-->

<!--    &lt;!&ndash; 空状态 &ndash;&gt;-->
<!--    <div v-else-if="dataList.length === 0" class="text-center text-gray-500">暂无待审核数据</div>-->

<!--    &lt;!&ndash; 数据表格 &ndash;&gt;-->
<!--    <div v-else class="overflow-x-auto">-->
<!--      <table class="table table-zebra">-->
<!--        <thead>-->
<!--          <tr>-->
<!--            <th>ID</th>-->
<!--            <th>名称</th>-->
<!--            <th>图标</th>-->
<!--            <th>过期时间</th>-->
<!--            <th>状态</th>-->
<!--            <th>操作</th>-->
<!--          </tr>-->
<!--        </thead>-->
<!--        <tbody>-->
<!--          <tr v-for="item in dataList" :key="item.id">-->
<!--            <td>{{ item.id }}</td>-->
<!--            <td>{{ item.name }}</td>-->
<!--            <td>-->
<!--              <div class="flex items-center gap-1">-->
<!--                <span v-if="item.icon" class="text-xl">{{ item.icon }}</span>-->
<!--                <span v-else>无图标</span>-->
<!--              </div>-->
<!--            </td>-->
<!--            <td>{{ formatDate(item.expiredAt) }}</td>-->
<!--            <td>-->
<!--              <span :class="statusClass(item.status)">{{ item.status }}</span>-->
<!--            </td>-->
<!--            <td>-->
<!--              <div class="flex gap-2">-->
<!--                <button-->
<!--                  class="btn btn-sm btn-success"-->
<!--                  @click="handleApprove(item.id)"-->
<!--                  :disabled="item.status !== 'PENDING'"-->
<!--                >-->
<!--                  同意-->
<!--                </button>-->
<!--                <button-->
<!--                  class="btn btn-sm btn-error"-->
<!--                  @click="handleReject(item.id)"-->
<!--                  :disabled="item.status !== 'PENDING'"-->
<!--                >-->
<!--                  拒绝-->
<!--                </button>-->

<!--                <button class="btn btn-sm btn-info" @click="openDetail(item)">查看</button>-->
<!--              </div>-->
<!--            </td>-->
<!--          </tr>-->
<!--        </tbody>-->
<!--      </table>-->

<!--      &lt;!&ndash; 分页 &ndash;&gt;-->
<!--      <div class="flex justify-center mt-4" v-if="totalPages > 1">-->
<!--        <div class="join">-->
<!--          <button-->
<!--            v-for="page in totalPages"-->
<!--            :key="page"-->
<!--            class="join-item btn"-->
<!--            :class="{ 'btn-active': currentPage === page }"-->
<!--            @click="changePage(page)"-->
<!--          >-->
<!--            {{ page }}-->
<!--          </button>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

<!--    &lt;!&ndash; 查看模态框 &ndash;&gt;-->
<!--    <dialog ref="modal" class="modal" @click="handleOutsideClick">-->
<!--      <div class="modal-box w-11/12 max-w-7xl">-->
<!--        <h3 class="font-bold text-lg mb-4">详情预览 - {{ selectedItem?.name }}</h3>-->
<!--        <div v-if="selectedItem" class="prose max-w-none">-->
<!--          <TiptapViewer :content="selectedItem.tiptapJson" />-->
<!--        </div>-->
<!--        <div class="modal-action">-->
<!--          <button class="btn" @click="closeModal">关闭</button>-->
<!--        </div>-->
<!--      </div>-->
<!--    </dialog>-->
<!--  </div>-->

<!--  <RejectDialog ref="rejectDialog" @confirmed="handleRejectConfirm" />-->


<!--</template>-->

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPendingData, approveData, rejectData } from '@/API/admin/adminAPI'
import TiptapViewer from '@/components/admin/TiptapViewer.vue'
import type { AdminSidedatumDto, PageResponse } from '@/types/backend'
import iconRefresh from '@/assets/icons/adminIcon/iconRefresh.vue'
import RejectDialog from '@/components/message/rejectDialog.vue'

// 响应式数据
const loading = ref(true)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = 10
const totalPages = ref(0)
const dataList = ref<AdminSidedatumDto[]>([])
const selectedItem = ref<AdminSidedatumDto | null>(null)
const modal = ref<HTMLDialogElement | null>(null)
const rejectDialog = ref<InstanceType<typeof RejectDialog>>()





// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    const res = await getPendingData({
      keyword: searchKeyword.value,
      page: currentPage.value - 1, // 后端页码从0开始
      size: pageSize,
    })

    if (res.success) {
      const pageData = res.data as PageResponse<AdminSidedatumDto>
      dataList.value = pageData.content
      totalPages.value = pageData.totalPages
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleOutsideClick = (event: MouseEvent) => {
  if (event.target === modal.value) {
    closeModal()
  }
}



// 分页切换
const changePage = (page: number) => {
  currentPage.value = page
  fetchData()
}

// 操作处理
const handleApprove = async (id: number) => {
  try {
    const res = await approveData(id)
    if (res.success) {
      await fetchData()
    }
  } catch (error) {
    console.error('批准失败:', error)
  }
}


const handleRejectConfirm = async (id: number, reason: string) => {
  try {
    const res = await rejectData(id, reason)
    if (res.success) {
      await fetchData()
    }
  } catch (error) {
    console.error('拒绝失败:', error)
  }
}

const handleReject = async (id: number) => {
  rejectDialog.value?.open(id)
  // try {
  //   const res = await rejectData(id)
  //   if (res.success) {
  //     await fetchData()
  //   }
  // } catch (error) {
  //   console.error('拒绝失败:', error)
  // }
}

// 打开详情
const openDetail = (item: AdminSidedatumDto) => {
  selectedItem.value = item
  modal.value?.showModal()
}

// 关闭模态框
const closeModal = () => {
  modal.value?.close()
}

// 日期格式化
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString()
}

// 初始化
onMounted(fetchData)
</script>


