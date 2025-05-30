import * as Y from 'yjs'
import { WebsocketProvider } from 'y-websocket'
import { ref, onMounted } from 'vue'
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
  docId: string
) => {
  const ydoc = new Y.Doc()
  const websocketProvider = ref<WebsocketProvider | null>(null)

  const currentUser = ref({
    name: authStore.userInfo?.username || '匿名用户',
    color: authStore.userInfo?.username
      ? generateUserColor(authStore.userInfo.username)
      : '#f783ac',
  })

  onMounted(() => {
    const token = localStorage.getItem('authToken') || ''
    websocketProvider.value = new WebsocketProvider(
      'wss://websocket-5ngf.onrender.com',
      `room-${docId}`,
      ydoc,
      { params: { token } }
    )
  })

  return { ydoc, websocketProvider, currentUser }
}
