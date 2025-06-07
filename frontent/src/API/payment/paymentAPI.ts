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


/**
 * 创建支付订单
 * @param data 支付请求数据
 * @param type 支付类型：'vip' | 'recharge' | 'knowledge'
 */
export const createPaymentOrder = async (
  data: {
    amount: number
    subject: string
  },
  type: 'vip' | 'recharge' | 'knowledge' = 'vip'
): Promise<PaymentAPIResponse> => {
  try {
    let endpoint = '/api/payment/alipay/create';

    // 根据支付类型选择不同的后端端点
    if (type === 'recharge') {
      endpoint = '/api/payment/recharge';
    } else if (type === 'knowledge') {
      endpoint = '/api/payment/alipay/create';
    }

    const res = await apiClient.post<PaymentResponse>(endpoint, {
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
// export const createPaymentOrder = async (
//   knowledgeId: number,
//   amount: number
// ): Promise<PaymentAPIResponse> => {
//   try {
//     const res = await apiClient.post<PaymentResponse>('/api/payment/alipay/create', {
//       amount,
//       subject: `知识购买-${knowledgeId}`,
//       knowledgeId
//     })
//     return { success: true, data: res.data }
//   } catch (error) {
//     console.error('支付请求失败:', error)
//     return {
//       success: false,
//       error: error instanceof Error ? error.message : '未知错误',
//     }
//   }
// }

/**
 * 查询支付状态
 * @param orderNo 订单号
 */
export const checkPaymentStatus = async (orderNo: string): Promise<PaymentStatusAPIResponse> => {
  try {
    const response = await apiClient.get<PaymentStatusResponse>(
      `/api/payment/status/${orderNo}`,
      { timeout: 10000 }
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




