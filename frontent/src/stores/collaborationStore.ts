// src/stores/collaborationStore.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCollaborationStore = defineStore('collaborationStore', () => {
  // 协作房间号
  const room = ref('')

  // 用户协作名称
  const username = ref('')

  // 设置协作信息的函数
  const setCollaborationInfo = (newRoom: string, newUsername: string) => {
    room.value = newRoom
    username.value = newUsername
  }

  // 清除协作信息
  const clearCollaborationInfo = () => {
    room.value = ''
    username.value = ''
  }

  return {
    room,
    username,
    setCollaborationInfo,
    clearCollaborationInfo
  }
})
