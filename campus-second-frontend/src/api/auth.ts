import request from '../utils/request'

export interface LoginDTO {
  username: string
  password: string
  role?: string
}

export interface LoginVO {
  id: number
  username: string
  role: string
  token: string
}

export const adminLogin = async (data: LoginDTO): Promise<LoginVO> => {
  const res = (await request.post('/api/admin/login', data)) as any
  return res.data ?? res
}

export const userLogin = async (data: LoginDTO): Promise<LoginVO> => {
  const res = (await request.post('/api/user/login', data)) as any
  return res.data ?? res
}
