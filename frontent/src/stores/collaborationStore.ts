import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCollaborationStore = defineStore('collaborationStore', () => {
  const room = ref('')
  const username = ref('')
  const docId = ref<number | null>(null)
  const isCreator = ref(false)
  const initialContent = ref<Record<string, any> | null>(null)

  const setCollaborationInfo = (
    newRoom: string,
    newUsername: string,
    newDocId?: number,
    newContent?: Record<string, any>,
    creator = false
  ) => {
    room.value = newRoom
    username.value = newUsername
    isCreator.value = creator

    if (newDocId !== undefined) docId.value = newDocId
    if (newContent !== undefined) initialContent.value = newContent

    // 保存到sessionStorage
    sessionStorage.setItem('collaboration_room', newRoom)
    sessionStorage.setItem('collaboration_username', newUsername)
    sessionStorage.setItem('collaboration_isCreator', creator.toString())

    if (newDocId !== undefined)
      sessionStorage.setItem('collaboration_docId', newDocId.toString())

    if (newContent !== undefined)
      sessionStorage.setItem('collaboration_initialContent', JSON.stringify(newContent))
  }

  const clearCollaborationInfo = () => {
    room.value = ''
    username.value = ''
    docId.value = null
    initialContent.value = null
    isCreator.value = false

    sessionStorage.removeItem('collaboration_room')
    sessionStorage.removeItem('collaboration_username')
    sessionStorage.removeItem('collaboration_docId')
    sessionStorage.removeItem('collaboration_initialContent')
    sessionStorage.removeItem('collaboration_isCreator')
  }

  const initFromStorage = () => {
    const savedRoom = sessionStorage.getItem('collaboration_room')
    const savedUsername = sessionStorage.getItem('collaboration_username')
    const savedDocId = sessionStorage.getItem('collaboration_docId')
    const savedInitialContent = sessionStorage.getItem('collaboration_initialContent')
    const savedIsCreator = sessionStorage.getItem('collaboration_isCreator')

    if (savedRoom) room.value = savedRoom
    if (savedUsername) username.value = savedUsername
    if (savedDocId) docId.value = parseInt(savedDocId, 10)
    if (savedInitialContent) initialContent.value = JSON.parse(savedInitialContent)
    if (savedIsCreator) isCreator.value = savedIsCreator === 'true'
  }

  initFromStorage()

  return {
    room,
    username,
    docId,
    initialContent,
    isCreator,
    setCollaborationInfo,
    clearCollaborationInfo,
  }
})
