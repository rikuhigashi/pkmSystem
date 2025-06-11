import { ref } from 'vue'
import { defineStore } from 'pinia'

type AlertType = 'info' | 'success' | 'warning' | 'error'

export const useAlertStore = defineStore('alert', () => {
  // 定义响应式状态
  const show = ref(false)
  const message = ref('')
  const type = ref<AlertType>('info')

  // 显示警告消息
  const showAlert = (msg: string, alertType: AlertType = 'success', timeout: number = 3000) => {
    message.value = msg
    type.value = alertType
    show.value = true

    if (timeout > 0) {
      setTimeout(hideAlert, timeout)
    }
  }

  // 隐藏警告消息
  const hideAlert = () => {
    show.value = false
  }

  // 显示加载中的消息
  const showLoading = (msg: string) => {
    showAlert(msg, 'info', 0)  // 0表示持续显示
  }

  // 显示成功消息
  const showSuccess = (msg: string) => {
    showAlert(msg, 'success')
  }

  // 返回响应式状态和方法
  return {
    show,
    message,
    type,
    showAlert,
    hideAlert,
    showLoading,
    showSuccess
  }
})
