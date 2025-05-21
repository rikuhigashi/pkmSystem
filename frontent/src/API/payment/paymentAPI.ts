import apiClient from '@/API/axios'

interface PaymentResponse {
  orderNo: string
  payForm: string
  expireTime: string
}

interface PaymentStatusResponse {
  orderNo: string
  status: 'SUCCESS' | 'CREATED' | 'EXPIRED' | 'FAILED'
  transactionId?: string
}

type PaymentAPIResponse =
  | { success: true; data: PaymentResponse }
  | { success: false; error: string }

type PaymentStatusAPIResponse =
  | { success: true; data: PaymentStatusResponse }
  | { success: false; error: string }

export const createPaymentOrder = async (data: {
  amount: number
  subject: string
}): Promise<PaymentAPIResponse> => {
  try {
    const res = await apiClient.post<PaymentResponse>('/api/payment/alipay/create', {
      amount: data.amount,
      subject: data.subject,
    })
    return { success: true, data: res.data }
  } catch (error) {
    console.error('支付请求失败:', error)
    return {
      success: false,
      error: error instanceof Error ? error.message : '未知错误',
    }
  }
}

// 查询支付状态
export const checkPaymentStatus = async (orderNo: string): Promise<PaymentStatusAPIResponse> => {
  try {
    const response = await apiClient.get<PaymentStatusResponse>(
      `/api/payment/status/${orderNo}`,
      {
        timeout: 10000 // 延长超时时间
      }
    )
    return {
      success: true,
      data: response.data,
    }
  } catch (error) {
    console.error('支付状态查询失败:', error)
    return {
      success: false,
      error: error instanceof Error ? error.message : '支付状态查询失败',
    }
  }
}
// export const checkPaymentStatus = async (orderNo: string): Promise<PaymentStatusAPIResponse> => {
//   try {
//     const response = await apiClient.get<PaymentStatusResponse>(`/api/payment/status/${orderNo}`)
//     return {
//       success: true,
//       data: response.data,
//     }
//   } catch (error) {
//     console.error('支付状态查询失败:', error)
//     throw new Error('支付状态查询失败')
//   }
// }
