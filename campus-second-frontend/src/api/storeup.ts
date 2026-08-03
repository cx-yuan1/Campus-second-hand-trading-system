import request from '../utils/request'

export interface StoreupItem {
  id: number
  userid: number
  refid: number
  tablename: string
  name: string
  picture: string
  type: string
}

export const getStoreupList = (userid: number) =>
  request.get<any>('/api/storeup/list', { params: { userid } }).then((res: any) => res.data ?? res)

export const addStoreup = (data: { userid: number; refid: number }) =>
  request.post<any>('/api/storeup/add', data).then((res: any) => res.data ?? res)

export const deleteStoreup = (id: number, userid: number) =>
  request.delete<any>(`/api/storeup/${id}`, { params: { userid } }).then((res: any) => res.data ?? res)
