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
    // 保存到sessionStorage（页面关闭时清除）
    sessionStorage.setItem('collaboration_room', newRoom)
    sessionStorage.setItem('collaboration_username', newUsername)
  }

  // 清除协作信息
  const clearCollaborationInfo = () => {
    room.value = ''
    username.value = ''
    // 清除sessionStorage
    sessionStorage.removeItem('collaboration_room')
    sessionStorage.removeItem('collaboration_username')
  }

  // 初始化时尝试从sessionStorage加载
  const initFromStorage = () => {
    const savedRoom = sessionStorage.getItem('collaboration_room')
    const savedUsername = sessionStorage.getItem('collaboration_username')
    if (savedRoom && savedUsername) {
      room.value = savedRoom
      username.value = savedUsername
    }
  }

  // 立即初始化
  initFromStorage()

  return {
    room,
    username,
    setCollaborationInfo,
    clearCollaborationInfo
  }
})
