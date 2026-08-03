import request from '../utils/request'

export interface AddressItem {
  id: number
  userid: number
  address: string
  name: string
  phone: string
  isdefault: string
}

export const getAddressList = (userid: number) =>
  request.get<any>('/api/address/list', { params: { userid } }).then((res: any) => res.data ?? res)

export const addAddress = (data: Partial<AddressItem>) =>
  request.post<any>('/api/address/add', data).then((res: any) => res.data ?? res)

export const updateAddress = (data: Partial<AddressItem>) =>
  request.put<any>('/api/address/update', data).then((res: any) => res.data ?? res)

export const deleteAddress = (id: number, userid: number) =>
  request.delete<any>(`/api/address/${id}`, { params: { userid } }).then((res: any) => res.data ?? res)
