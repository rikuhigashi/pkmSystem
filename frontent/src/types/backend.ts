// backend.ts
export interface AdminSidedatumDto {
  id: number
  name: string
  href: string | null
  icon: string
  tiptapJson: Record<string, any>
  expiredAt: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
}

export interface PageResponse<T> {
  content: T[]
  number: number
  size: number
  totalElements: number
  totalPages: number
}
