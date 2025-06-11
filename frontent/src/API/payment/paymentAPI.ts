import apiClient from '@/API/axios'
import type {
  PaymentRequest,
  PaymentAPIResponse,
  PaymentStatusAPIResponse, PaymentStatusResponse, PaymentResponse
} from '@/types/paymentTypes'

/**
 * 创建支付订单
 * @param data 支付请求数据
 */
export const createPaymentOrder = async (
  data: PaymentRequest
): Promise<PaymentAPIResponse> => {
  try {
    const res = await apiClient.post<PaymentResponse>('/api/payment/alipay/create', data)
    return {
      success: true,
      data: {
        orderNo: res.data.orderNo,
        payForm: res.data.payForm,
        expireTime: res.data.expireTime
      }
    }
  } catch (error: unknown) {
    let errorMessage = '未知错误'
    if (error instanceof Error) {
      errorMessage = error.message
    } else if (typeof error === 'string') {
      errorMessage = error
    }

    console.error('支付请求失败:', errorMessage)
    return {
      success: false,
      error: errorMessage
    }
  }
}

/**
 * 查询支付状态
 * @param orderNo 订单号
 */
export const checkPaymentStatus = async (
  orderNo: string
): Promise<PaymentStatusAPIResponse> => {
  try {
    const res = await apiClient.get<PaymentStatusResponse>(`/api/payment/status/${orderNo}`, {
      timeout: 10000
    })

    return {
      success: true,
      data: {
        orderNo: res.data.orderNo,
        status: res.data.status,
        transactionId: res.data.transactionId
      }
    }
  } catch (error: unknown) {
    let errorMessage = '支付状态查询失败'
    if (error instanceof Error) {
      errorMessage = error.message
    } else if (typeof error === 'string') {
      errorMessage = error
    }

    console.error('支付状态查询失败:', errorMessage)
    return {
      success: false,
      error: errorMessage
    }
  }
}
