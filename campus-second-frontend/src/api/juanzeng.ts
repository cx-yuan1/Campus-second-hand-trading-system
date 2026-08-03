import request from '../utils/request'

export interface JuanzengxinxiItem {
  id: number
  juanzengbianhao: string
  shangpinmingcheng: string
  tupian: string
  juanzengshuliang: number
  juanzengshijian: string
  juanzengdizhi: string
  yonghuzhanghao: string
  yonghuxingming: string
  lianxifangshi: string
  beizhu: string
}

export const getJuanzengxinxiList = (params: { page?: number; limit?: number; keyword?: string }) =>
  request.get<any>('/api/juanzengxinxi/list', { params }).then((res: any) => res.data ?? res)

export const addJuanzengxinxi = (data: Partial<JuanzengxinxiItem>) =>
  request.post<any>('/api/juanzengxinxi/add', data).then((res: any) => res.data ?? res)

export const updateJuanzengxinxi = (data: Partial<JuanzengxinxiItem>) =>
  request.put<any>('/api/juanzengxinxi/update', data).then((res: any) => res.data ?? res)

export const deleteJuanzengxinxi = (id: number) =>
  request.delete<any>(`/api/juanzengxinxi/${id}`).then((res: any) => res.data ?? res)
