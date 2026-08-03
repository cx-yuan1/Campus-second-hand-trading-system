import request from '../utils/request'

export interface CartItem {
  id: number
  userid: number
  goodid: number
  goodname: string
  picture: string
  buynumber: number
  price: number
  discountprice: number
  yonghuzhanghao: string
}

export const getCartList = (userid: number) =>
  request.get<any>('/api/cart/list', { params: { userid } }).then((res: any) => res.data ?? res)

export const addCart = (data: { userid: number; goodid: number; buynumber?: number }) =>
  request.post<any>('/api/cart/add', data).then((res: any) => res.data ?? res)

export const deleteCart = (id: number, userid: number) =>
  request.delete<any>(`/api/cart/${id}`, { params: { userid } }).then((res: any) => res.data ?? res)
