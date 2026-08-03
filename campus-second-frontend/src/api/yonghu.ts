import request from '../utils/request'

export interface YonghuItem {
  id: number
  yonghuzhanghao: string
  mima: string
  yonghuxingming: string
  xingbie: string
  lianxifangshi: string
  touxiang: string
  money: number
}

export const getYonghuList = (params: {
  page?: number
  limit?: number
  keyword?: string
}) => request.get<any>('/api/yonghu/list', { params }).then((res: any) => res.data ?? res)

export const getYonghuDetail = (id: number) =>
  request.get<any>(`/api/yonghu/${id}`).then((res: any) => res.data ?? res)

export const addYonghu = (data: Partial<YonghuItem>) =>
  request.post<any>('/api/yonghu/add', data).then((res: any) => res.data ?? res)

export const updateYonghu = (data: Partial<YonghuItem>) =>
  request.put<any>('/api/yonghu/update', data).then((res: any) => res.data ?? res)

export const deleteYonghu = (id: number) =>
  request.delete<any>(`/api/yonghu/${id}`).then((res: any) => res.data ?? res)

export const recharge = (data: { userid: number; amount: number }) =>
  request.post<any>('/api/yonghu/recharge', data).then((res: any) => res.data ?? res)
