<script lang="ts" setup>
import { Bars3Icon } from '@heroicons/vue/24/outline'
import { MagnifyingGlassIcon } from '@heroicons/vue/20/solid'

// ----------------- store -----------------
import { useSidebarStore } from '@/stores/sidebar'
import { useEditorStore } from '@/stores/main/editorStore'
import { useCollaborationStore } from '@/stores/collaborationStore'

const editorStore = useEditorStore()
const sidebarStore = useSidebarStore()
const collaborationStore = useCollaborationStore()
const collaborationMode = ref('create')

import IconSaveData from '@/assets/icons/iconSaveData.vue'
import NotificationPanel from '@/components/message/notificationPanel.vue'
import { ref, watch } from 'vue'
import router from '@/router'

const isSaving = ref(false)
const showCollaborationDialog = ref(false)
const collaborationUsername = ref('')
const collaborationRoomId = ref('')

const handleSaveMainData = async () => {
  if (isSaving.value) return

  try {
    isSaving.value = true
    if (editorStore.saveDocument) {
      await editorStore.saveDocument()
    }
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    isSaving.value = false
  }
}

// 生成随机房间号 (6位字母数字组合)
const generateRoomId = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < 6; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

// 监听弹窗开启状态
watch(showCollaborationDialog, (isOpen) => {
  if (isOpen && !collaborationRoomId.value) {
    collaborationRoomId.value = generateRoomId()
  }
})

// 启动协作功能
const startCollaboration = () => {
  if (!collaborationUsername.value.trim()) {
    alert('请输入您的协作名称')
    return
  }

  const username = collaborationUsername.value.trim()

  collaborationStore.setCollaborationInfo(collaborationRoomId.value, username)

  router.push({
    name: 'collaborationView',
  })

  // 关闭弹窗
  showCollaborationDialog.value = false
}

</script>

<template>
  <div
    class="sticky top-0 z-40 flex h-16 shrink-0 items-center gap-x-4 border-b border-gray-200 bg-white px-4 shadow-sm sm:gap-x-6 sm:px-6 lg:px-8"
  >
    <icon-save-data
      class="h-6 w-6 cursor-pointer text-gray-600 hover:text-primary"
      @click="handleSaveMainData"
    />

    <button
      type="button"
      :class="['btn btn-ghost p-2', sidebarStore.sidebarOpen ? '' : 'lg:hidden']"
      @click="sidebarStore.toggleSidebar"
    >
      <Bars3Icon class="size-6 text-gray-600" />
      <span class="sr-only">切换侧边栏</span>
    </button>

    <div class="flex flex-1 items-center gap-x-4">
      <div class="relative flex-1 max-w-xl">
        <MagnifyingGlassIcon
          class="pointer-events-none absolute left-3 top-1/2 size-5 -translate-y-1/2 text-gray-400"
        />
        <input
          id="search-field"
          class="input input-bordered w-full pl-10"
          placeholder="搜索..."
          type="search"
        />
      </div>

      <div class="flex items-center gap-x-2">
        <NotificationPanel />
      </div>
    </div>



    <!-- 新增协作按钮 -->
    <button
      class="btn btn-ghost btn-sm ml-2"
      @click="showCollaborationDialog = true"
      title="开始协作编辑"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-5 w-5"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
        />
      </svg>
      <span class="ml-1 hidden md:inline">协作编辑</span>
    </button>

    <!-- 协作对话框 -->
    <div v-if="showCollaborationDialog" class="modal modal-open">
      <div class="modal-box max-w-md">
        <h3 class="font-bold text-lg mb-4">协作编辑</h3>

        <!-- 模式选择 -->
        <div class="tabs tabs-boxed mb-6">
          <button
            :class="['tab flex-1', collaborationMode === 'create' ? 'tab-active' : '']"
            @click="
              collaborationMode = 'create';
              collaborationRoomId = generateRoomId()
            "
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 mr-1"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 6v6m0 0v6m0-6h6m-6 0H6"
              />
            </svg>
            创建新房间
          </button>
          <button
            :class="['tab flex-1', collaborationMode === 'join' ? 'tab-active' : '']"
            @click="
              collaborationMode = 'join';
              collaborationRoomId = ''
            "
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 mr-1"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
              />
            </svg>
            加入房间
          </button>
        </div>

        <div class="space-y-4">
          <!-- 创建房间模式 -->
          <div v-if="collaborationMode === 'create'">
            <div class="mb-4 p-3 bg-blue-50 rounded-lg border border-blue-100">
              <div class="flex items-center text-blue-700">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 mr-2"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                <p class="text-sm">创建一个新房间并邀请他人加入</p>
              </div>
            </div>

            <!-- 房间号区域 -->
            <div>
              <label class="label">
                <span class="label-text">房间号</span>
              </label>
              <div class="flex items-center gap-2">
                <input
                  type="text"
                  class="input input-bordered w-full flex-1"
                  :value="collaborationRoomId"
                  placeholder="正在生成房间号..."
                  readonly
                />
                <button
                  class="btn btn-outline"
                  @click="collaborationRoomId = generateRoomId()"
                  title="重新生成房间号"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4"
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
              <p class="text-sm text-gray-500 mt-2">
                将此房间号分享给协作者: <span class="font-semibold">{{ collaborationRoomId }}</span>
              </p>
            </div>
          </div>

          <!-- 加入房间模式 -->
          <div v-if="collaborationMode === 'join'">
            <div class="mb-4 p-3 bg-purple-50 rounded-lg border border-purple-100">
              <div class="flex items-center text-purple-700">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 mr-2"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                  />
                </svg>
                <p class="text-sm">加入已有房间进行协作编辑</p>
              </div>
            </div>

            <!-- 房间号输入 -->
            <div>
              <label class="label">
                <span class="label-text">输入房间号</span>
              </label>
              <input
                type="text"
                class="input input-bordered w-full"
                v-model="collaborationRoomId"
                placeholder="请输入要加入的房间号"
              />
              <p class="text-sm text-gray-500 mt-2">请输入他人分享给您的房间号</p>
            </div>
          </div>

          <!-- 用户名输入区域 -->
          <div>
            <label class="label">
              <span class="label-text">输入协作用户名</span>
              <span class="label-text-alt text-error">* 必填</span>
            </label>
            <input
              type="text"
              class="input input-bordered w-full"
              v-model="collaborationUsername"
              placeholder="您的协作名称"
            />
            <p class="text-sm text-gray-500 mt-2">此名称将显示给其他协作者，请使用2-12个字符</p>
          </div>
        </div>

        <div class="modal-action">
          <button class="btn" @click="showCollaborationDialog = false">取消</button>
          <button
            class="btn btn-primary"
            @click="startCollaboration"
            :disabled="
              !collaborationUsername.trim() ||
              collaborationUsername.trim().length < 2 ||
              !collaborationRoomId
            "
          >
            {{ collaborationMode === 'create' ? '创建房间' : '加入房间' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
