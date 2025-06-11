import { defineStore } from 'pinia'
import { ref } from 'vue'
import { createPaymentOrder, checkPaymentStatus } from '@/API/payment/paymentAPI'
import type {
  PaymentType,
  PaymentRequest,
  PaymentPayload,
  PaymentStatusResponse
} from '@/types/paymentTypes'

interface PaymentOrder {
  orderNo: string;
  amount: number;
  type: PaymentType;
  payload?: PaymentPayload;
}

export const usePaymentStore = defineStore('payment', () => {
  const isPaying = ref(false)
  const currentOrder = ref<PaymentOrder | null>(null)
  const paymentStatus = ref<'idle' | 'pending' | 'success' | 'failed'>('idle')

  // 创建支付订单
  const createOrder = async (
    amount: number,
    type: PaymentType,
    subject: string,
    payload?: PaymentPayload
  ): Promise<string> => {
    isPaying.value = true
    try {
      const request: PaymentRequest = {
        amount,
        subject,
        type,
        payload
      }

      const response = await createPaymentOrder(request)

      if (response.success && response.data) {
        currentOrder.value = {
          orderNo: response.data.orderNo,
          amount,
          type,
          payload
        }
        return response.data.payForm
      }

      throw new Error(response.error || '创建订单失败')
    } catch (error: unknown) {
      let errorMessage = '支付订单创建失败'
      if (error instanceof Error) {
        errorMessage = error.message
      }
      throw new Error(errorMessage)
    } finally {
      isPaying.value = false
    }
  }

  // 检查支付状态
  const checkOrderStatus = async (orderNo: string): Promise<PaymentStatusResponse> => {
    paymentStatus.value = 'pending'
    try {
      const response = await checkPaymentStatus(orderNo)

      if (response.success && response.data) {
        return response.data
      }

      throw new Error(response.error || '状态查询失败')
    } catch (error: unknown) {
      paymentStatus.value = 'failed'
      let errorMessage = '支付状态查询失败'
      if (error instanceof Error) {
        errorMessage = error.message
      }
      throw new Error(errorMessage)
    }
  }

  return {
    isPaying,
    paymentStatus,
    currentOrder,
    createOrder,
    checkOrderStatus
  }
})
