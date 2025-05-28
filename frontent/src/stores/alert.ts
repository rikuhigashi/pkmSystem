import { defineStore } from 'pinia'

type AlertType = 'info' | 'success' | 'warning' | 'error'

export const useAlertStore = defineStore('alert', {
  state: () => ({
    show: false,
    message: '',
    type: 'info' as AlertType
  }),
  actions: {
    showAlert(message: string, type: AlertType = 'success', timeout: number = 3000) {
      this.show = true
      this.message = message
      this.type = type
      if (timeout > 0) {
        setTimeout(() => this.hideAlert(), timeout)
      }
    },
    hideAlert() {
      this.show = false
    },
    showLoading(message: string) {
      this.showAlert(message, 'info', 0)  // 0表示持续显示
    },
    showSuccess(message: string) {
      this.showAlert(message, 'success')
    },
  }
})

export type AlertStore = ReturnType<typeof useAlertStore>
