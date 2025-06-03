<template>
  <div class="flex flex-col h-screen bg-gray-50">
    <!-- 顶部协作信息栏 -->
    <div class="flex items-center justify-between px-6 py-3 bg-white shadow-sm border-b">
      <div class="flex items-center space-x-4">
        <div class="flex items-center">
          <div class="text-gray-600 text-sm">房间ID:</div>
          <div class="ml-2 px-3 py-1 bg-blue-50 text-blue-700 rounded-full font-medium text-sm">
            {{ roomId }}
          </div>
        </div>

        <div class="flex items-center">
          <div class="text-gray-600 text-sm">我的名称:</div>
          <div class="ml-2 px-3 py-1 bg-purple-50 text-purple-700 rounded-full font-medium text-sm">
            {{ username }}
          </div>
        </div>
      </div>

      <div class="flex items-center space-x-4">
        <div class="flex items-center">
          <div class="w-2.5 h-2.5 rounded-full bg-green-500 mr-2"></div>
          <span class="text-gray-700 text-sm">在线用户: {{ onlineUsers.length }}</span>
        </div>

        <div class="flex items-center text-gray-600 text-sm">
          <template v-if="isSaving">
            <svg
              class="animate-spin -ml-1 mr-2 h-4 w-4 text-gray-600"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
            </svg>
            保存中...
          </template>
          <template v-else>
            <svg
              class="w-4 h-4 mr-1 text-green-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M5 13l4 4L19 7"
              ></path>
            </svg>
            已同步 {{ lastSavedTime }}
          </template>
        </div>
      </div>
    </div>

    <!-- 编辑器区域 -->
    <div class="flex-1 flex flex-col overflow-hidden min-h-screen">
      <div class="flex-1 overflow-hidden relative">
        <editor-content :editor="editor" class="min-h-screen max-w-6xl mx-auto p-4 border-2 border-gray-300 rounded-lg shadow-lg overflow-auto" />
      </div>
    </div>

    <!-- 右下角在线用户列表 -->
    <div class="fixed bottom-4 right-4 flex flex-col items-end space-y-2">
      <div class="flex -space-x-2">
        <div
          v-for="(user, index) in onlineUsers"
          :key="user.id"
          class="relative group"
          :style="{ zIndex: onlineUsers.length - index }"
        >
          <div
            class="w-10 h-10 rounded-full flex items-center justify-center text-white font-medium text-sm shadow-md"
            :style="{ backgroundColor: user.color }"
          >
            {{ getUserInitials(user.name) }}
          </div>
          <div
            class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 hidden group-hover:block px-2 py-1 bg-gray-800 text-white text-xs rounded whitespace-nowrap"
          >
            {{ user.name }}
          </div>
        </div>
      </div>
      <div
        v-if="onlineUsers.length > 0"
        class="px-3 py-1 bg-white rounded-full shadow text-xs text-gray-600"
      >
        当前在线用户
      </div>
    </div>

    <!-- 退出协作按钮 -->
    <div class="fixed bottom-4 left-4">
      <button @click="exitCollaboration" class="btn btn-sm btn-outline btn-error">
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
            d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
          />
        </svg>
        退出协作
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { Editor, EditorContent } from '@tiptap/vue-3'
import Collaboration from '@tiptap/extension-collaboration'
import CollaborationCursor from '@tiptap/extension-collaboration-cursor'
import * as Y from 'yjs'
// import { TiptapCollabProvider } from '@hocuspocus/provider'
import { HocuspocusProvider } from '@hocuspocus/provider'

import StarterKit from '@tiptap/starter-kit'
import { useCollaborationStore } from '@/stores/collaborationStore'
import router from '@/router'
import { createTiptapToken } from '@/API/tiptap/tiptapAPI'

// 从store获取协作信息
const collaborationStore = useCollaborationStore()
const roomId = ref(collaborationStore.room)
const username = ref(collaborationStore.username)

const token = ref({ appId: '', token: '' })

// 编辑器实例
let editor: Editor | undefined = undefined
const doc = new Y.Doc()
const provider = ref<HocuspocusProvider | null>(null)

// 状态管理
const isSaving = ref(false)
const lastSavedTime = ref('刚刚')
const onlineUsers = ref<
  Array<{
    id: string
    name: string
    color: string
    avatar?: string
  }>
>([])

// 生成随机颜色（用于用户光标）
const generateColor = () => {
  const colors = [
    '#4F46E5',
    '#10B981',
    '#F59E0B',
    '#EF4444',
    '#8B5CF6',
    '#EC4899',
    '#06B6D4',
    '#F97316',
  ]
  return colors[Math.floor(Math.random() * colors.length)]
}

// 退出协作功能
const exitCollaboration = () => {
  provider.value?.disconnect()
  router.push({ name: 'home' }) // 跳转到首页或其他页面
  collaborationStore.clearCollaborationInfo()
}

// 获取用户名称首字母
const getUserInitials = (name: string) => {
  return name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .substring(0, 2)
    .toUpperCase()
}

// 初始化编辑器
onMounted(async () => {
  try {
    token.value = await createTiptapToken()
    console.log('获取到初始 token:', token.value)
  } catch (error) {
    console.error('获取初始 token 失败:', error)
    return
  }

  try {
    console.log('创建 provider，appId:', token.value.appId)

    // 初始化 Tiptap Collab 提供者
    provider.value = new HocuspocusProvider({
      url: `wss://${token.value.appId}.collab.tiptap.cloud`, // 正确的URL格式
      name: roomId.value,
      token: token.value.token, // 直接传递token字符串
      document: doc,
      broadcast: false, // 禁用广播以提高性能

      // 添加WebSocket事件处理
      onOpen: () => console.log('WebSocket连接已打开'),
      onConnect: () => console.log('协作服务已连接'),
      onDisconnect: () => console.log('协作服务已断开'),
      onDestroy: () => console.log('Provider已销毁'),
      onStateless: (payload) => console.log('无状态消息:', payload),

      // 监听用户状态变化
      onAwarenessUpdate: ({ states }) => {
        onlineUsers.value = Array.from(states.entries())
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          .filter(([_, state]) => state.user) // 确保有用户信息
          .map(([key, state]) => ({
            id: key.toString(), // 转换为字符串
            name: state.user?.name || '匿名用户',
            color: state.user?.color || generateColor(),
          }))

        console.log('在线用户更新:', onlineUsers.value)
      },
    })

    //设置完整的用户信息
    const userColor = generateColor()
    provider.value.awareness?.setLocalStateField('user', {
      name: username.value,
      color: userColor,
      clientId: provider.value.awareness?.clientID, // 添加客户端ID
      lastActive: Date.now(), // 添加最后活动时间
    })
    // 创建编辑器实例
    editor = new Editor({
      extensions: [
        StarterKit.configure({
          history: false,
        }),
        Collaboration.configure({
          document: doc,
        }),
        CollaborationCursor.configure({
          provider: provider.value,
          user: {
            name: username.value,
            color: userColor,
          },
        }),
      ],
      editable: true,
      autofocus: true,
    })

    // 定期更新用户活动状态
    const activityInterval = setInterval(() => {
      const currentState = provider.value?.awareness?.getLocalState()?.user || {}
      provider.value?.awareness?.setLocalStateField('user', {
        ...currentState,
        lastActive: Date.now(),
      })
    }, 5000)

    // 在组件卸载时清除定时器
    onBeforeUnmount(() => {
      clearInterval(activityInterval)
    })



    const connectionStatus = ref('connecting')
    provider.value.on('status', ({ status }) => {
      connectionStatus.value = status
    })


    // 模拟保存状态
    setInterval(() => {
      isSaving.value = true
      setTimeout(() => {
        isSaving.value = false
        const now = new Date()
        lastSavedTime.value = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
      }, 1000)
    }, 30000)
  } catch (e) {
    console.error('初始化协作编辑器失败:', e)
  }
})

// 销毁编辑器实例
onBeforeUnmount(() => {
  editor?.destroy()
  provider.value?.destroy()
  collaborationStore.clearCollaborationInfo()
})
</script>

<style lang="scss" >
/* 协作光标样式 */
.collaboration-cursor__caret {
  border-left: 2px solid;
  border-right: 2px solid;
  margin-left: -1px;
  margin-right: -1px;
  position: relative;
  word-break: normal;
}

.collaboration-cursor__label {
  border-radius: 0 3px 3px 3px; /* 调整圆角方向 */
  color: white;
  font-size: 12px;
  font-weight: 600;
  left: -1px;
  line-height: normal;
  padding: 0.1rem 0.3rem;
  position: absolute;
  top: 100%; /* 在光标下方 */
  margin-top: 4px; /* 添加一点间距 */
  user-select: none;
  white-space: nowrap;
  font-family: 'Inter', sans-serif;
  z-index: 10; /* 确保标签不被遮挡 */
}

/* 编辑器内容区域样式 */
.ProseMirror {
  min-height: 100%;
  padding: 1.5rem;
  box-sizing: border-box;
  position: relative;
  outline: none;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

::-webkit-scrollbar-track {
  background: #f3f4f6;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}


.editor-container {
  min-height: 100%;
}
</style>
