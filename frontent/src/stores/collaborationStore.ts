import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCollaborationStore = defineStore('collaborationStore', () => {
  // 协作房间号
  const room = ref('')

  // 用户协作名称
  const username = ref('')

  // 关联的主文档ID
  const docId = ref<number | null>(null)

  // 初始内容
  const initialContent = ref<Record<string, any> | null>(null)

  // 设置协作信息的函数
  const setCollaborationInfo = (
    newRoom: string,
    newUsername: string,
    newDocId?: number,
    newContent?: Record<string, any>,
  ) => {

    room.value = newRoom
    username.value = newUsername
    if (newDocId !== undefined) docId.value = newDocId
    if (newContent !== undefined) initialContent.value = newContent

    // 保存到sessionStorage（页面关闭时清除）
    sessionStorage.setItem('collaboration_room', newRoom)
    sessionStorage.setItem('collaboration_username', newUsername)
    if (newDocId !== undefined) sessionStorage.setItem('collaboration_docId', newDocId.toString())
    if (newContent !== undefined)
      sessionStorage.setItem('collaboration_initialContent', JSON.stringify(newContent))
  }

  // 清除协作信息
  const clearCollaborationInfo = () => {
    room.value = ''
    username.value = ''
    docId.value = null
    initialContent.value = null

    // 清除sessionStorage
    sessionStorage.removeItem('collaboration_room')
    sessionStorage.removeItem('collaboration_username')
    sessionStorage.removeItem('collaboration_docId')
    sessionStorage.removeItem('collaboration_initialContent')
  }

  // 初始化时尝试从sessionStorage加载
  const initFromStorage = () => {
    const savedRoom = sessionStorage.getItem('collaboration_room')
    const savedUsername = sessionStorage.getItem('collaboration_username')
    const savedDocId = sessionStorage.getItem('collaboration_docId')
    const savedInitialContent = sessionStorage.getItem('collaboration_initialContent')

    if (savedRoom && savedUsername) {
      room.value = savedRoom
      username.value = savedUsername
    }
    if (savedDocId) docId.value = parseInt(savedDocId, 10)
    if (savedInitialContent) initialContent.value = JSON.parse(savedInitialContent)
  }

  // 立即初始化
  initFromStorage()

  return {
    room,
    username,
    docId,
    initialContent,
    setCollaborationInfo,
    clearCollaborationInfo,
  }
})
