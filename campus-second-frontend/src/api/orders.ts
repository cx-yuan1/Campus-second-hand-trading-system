import request from '../utils/request'

export interface OrderItem {
  id: number
  orderid: string
  userid: number
  goodid: number
  goodname: string
  picture: string
  buynumber: number
  price: number
  discountprice: number
  total: number
  discounttotal: number
  status: string
  address: string
  tel: string
  consignee: string
  yonghuzhanghao?: string
  addtime: string
}

export const updateOrderStatus = (id: number, status: string) =>
  request.put<any>('/api/orders/updateStatus', null, { params: { id, status } }).then((res: any) => res.data ?? res)

export const getOrdersList = (params: {
  userid: number
  page?: number
  limit?: number
  status?: string
}) => request.get<any>('/api/orders/list', { params }).then((res: any) => res.data ?? res)

export const addOrder = (data: Partial<OrderItem>) =>
  request.post<any>('/api/orders/add', data).then((res: any) => res.data ?? res)

export const checkout = (data: { userid: number; addressId: number; cartIds: number[] }) =>
  request.post<any>('/api/orders/checkout', data).then((res: any) => res.data ?? res)
