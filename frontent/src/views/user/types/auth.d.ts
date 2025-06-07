export interface UserInfo {
  id:number
  username: string
  email: string
  vipActive?: boolean
  vipExpireDate?: string
  role?:string
  balance?: number
}
