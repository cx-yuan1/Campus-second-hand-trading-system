import request from '../utils/request'

export interface QiugouxinxiItem {
  id: number
  qiugoubianhao: string
  shangpinmingcheng: string
  shangpinfenlei: string
  guige: string
  tupian: string
  qiugoujiage: number
  qiugoushijian: string
  yonghuzhanghao: string
  yonghuxingming: string
  qiugouxiangqing: string
}

export const getQiugouxinxiList = (params: { page?: number; limit?: number; keyword?: string }) =>
  request.get<any>('/api/qiugouxinxi/list', { params }).then((res: any) => res.data ?? res)

export const addQiugouxinxi = (data: Partial<QiugouxinxiItem>) =>
  request.post<any>('/api/qiugouxinxi/add', data).then((res: any) => res.data ?? res)

export const updateQiugouxinxi = (data: Partial<QiugouxinxiItem>) =>
  request.put<any>('/api/qiugouxinxi/update', data).then((res: any) => res.data ?? res)

export const deleteQiugouxinxi = (id: number) =>
  request.delete<any>(`/api/qiugouxinxi/${id}`).then((res: any) => res.data ?? res)
