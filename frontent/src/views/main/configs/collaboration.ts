import * as Y from 'yjs'
import { WebsocketProvider } from 'y-websocket'
import { ref, computed, onUnmounted, watch } from 'vue'
import { userAuthStore } from '@/stores/auth'

const generateUserColor = (username: string): string => {
  let hash = 0
  for (let i = 0; i < username.length; i++) {
    hash = username.charCodeAt(i) + ((hash << 5) - hash)
  }
  const color = Math.floor(Math.abs(((Math.sin(hash) * 10000) % 1) * 16777215)).toString(16)
  return `#${'000000'.slice(0, 6 - color.length)}${color}`
}

export const useEditorCollaboration = (
  authStore: ReturnType<typeof userAuthStore>,
  docId: string,
) => {
  const ydoc = new Y.Doc()
  const websocketProvider = ref<WebsocketProvider | null>(null)

  const currentDocId = computed(() => docId)

  const currentUser = ref({
    name: authStore.userInfo?.username || '匿名用户',
    color: authStore.userInfo?.username
      ? generateUserColor(authStore.userInfo.username)
      : '#f783ac',
  })

  // 连接 WebSocket 的函数
  const connectWebSocket = () => {
    if (!currentDocId.value) {
      console.warn('文档ID未定义，无法连接协作服务器')
      return
    }

    // 销毁现有连接
    if (websocketProvider.value) {
      websocketProvider.value.disconnect()
      websocketProvider.value.destroy()
    }

    const token = localStorage.getItem('authToken') || ''
    websocketProvider.value = new WebsocketProvider(
      'wss://websocket-5ngf.onrender.com/collaboration', // 注意添加路径
      `room-${currentDocId.value}`,
      ydoc,
      {
        params: { token },
        connect: true,
      },
    )

    // 添加重连逻辑
    websocketProvider.value.on('status', (event) => {
      console.log('协作连接状态:', event.status)
      if (event.status === 'connected') {
        console.log('协作服务器连接成功')
      }
      if (event.status === 'disconnected') {
        console.log('协作连接断开，尝试重新连接...')
        setTimeout(connectWebSocket, 5000) // 5秒后重连
      }
    })
  }

  // 监听文档ID变化
  watch(
    () => currentDocId.value,
    (newId) => {
      if (newId) {
        console.log('文档ID更新为:', newId)
        connectWebSocket()
      }
    },
    { immediate: true }, // 立即执行一次
  )

  // 组件卸载时清理
  onUnmounted(() => {
    if (websocketProvider.value) {
      websocketProvider.value.disconnect()
      websocketProvider.value.destroy()
    }
    ydoc.destroy()
  })

  return { ydoc, websocketProvider, currentUser }

  // onMounted(() => {
  //   const token = localStorage.getItem('authToken') || ''
  //   websocketProvider.value = new WebsocketProvider(
  //     'wss://websocket-5ngf.onrender.com/collaboration',
  //     `room-${docId}`,
  //     ydoc,
  //     { params: { token } },
  //   )
  //
  //   websocketProvider.value.on('status', (event) => {
  //     console.log('WebSocket状态:', event.status)
  //     if (event.status === 'disconnected') {
  //       console.warn('协作连接断开，尝试重连...')
  //     }
  //   })
  // })
  //
  // return { ydoc, websocketProvider, currentUser }
}
