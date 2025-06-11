// 支付类型
export type PaymentType = 'vip' | 'recharge' | 'knowledge';

// 支付负载类型
export interface PaymentPayload {
  knowledgeId?: number;
  vipType?: string;
  [key: string]: unknown; // 允许其他未知属性
}

// 支付请求参数
export interface PaymentRequest {
  amount: number;
  subject: string;
  type: PaymentType;
  payload?: PaymentPayload;
}

// 支付响应
export interface PaymentResponse {
  orderNo: string;
  payForm: string;
  expireTime: string;
}

// 支付状态
export type PaymentStatus = 'SUCCESS' | 'CREATED' | 'EXPIRED' | 'FAILED';

// 支付状态响应
export interface PaymentStatusResponse {
  orderNo: string;
  status: PaymentStatus;
  transactionId?: string;
}

// 支付API响应
export interface PaymentAPIResponse {
  success: boolean;
  data?: PaymentResponse;
  error?: string;
}

// 支付状态API响应
export interface PaymentStatusAPIResponse {
  success: boolean;
  data?: PaymentStatusResponse;
  error?: string;
}
