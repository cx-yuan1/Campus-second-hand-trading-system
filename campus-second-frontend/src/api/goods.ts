import request from '../utils/request'

export interface Shangpinxinxi {
  id: number
  shangpinbianhao: string
  shangpinmingcheng: string
  shangpinfenlei: string
  guige: string
  tupian: string
  xinjiuchengdu: string
  fabushijian: string
  shangpinxiangqing: string
  yonghuzhanghao: string
  yonghuxingming: string
  price: number
  alllimittimes: number
  clicknum: number
}

export interface Shangpinfenlei {
  id: number
  shangpinfenlei: string
}

export const getShangpinfenleiList = () =>
  request.get<any>('/api/shangpinfenlei/list').then((res: any) => res.data ?? res)

export const getShangpinxinxiList = (params: {
  page?: number
  limit?: number
  shangpinfenlei?: string
  keyword?: string
}) =>
  request.get<any>('/api/shangpinxinxi/list', { params }).then((res: any) => res.data ?? res)

export const getShangpinxinxiDetail = (id: number) =>
  request.get<any>(`/api/shangpinxinxi/${id}`).then((res: any) => res.data ?? res)

export const addShangpinxinxi = (data: Partial<Shangpinxinxi>) =>
  request.post<any>('/api/shangpinxinxi/add', data).then((res: any) => res.data ?? res)

export const updateShangpinxinxi = (data: Partial<Shangpinxinxi>) =>
  request.put<any>('/api/shangpinxinxi/update', data).then((res: any) => res.data ?? res)

export const deleteShangpinxinxi = (id: number) =>
  request.delete<any>(`/api/shangpinxinxi/${id}`).then((res: any) => res.data ?? res)

export const addShangpinfenlei = (data: Partial<Shangpinfenlei>) =>
  request.post<any>('/api/shangpinfenlei/add', data).then((res: any) => res.data ?? res)

export const updateShangpinfenlei = (data: Partial<Shangpinfenlei>) =>
  request.put<any>('/api/shangpinfenlei/update', data).then((res: any) => res.data ?? res)

export const deleteShangpinfenlei = (id: number) =>
  request.delete<any>(`/api/shangpinfenlei/${id}`).then((res: any) => res.data ?? res)
