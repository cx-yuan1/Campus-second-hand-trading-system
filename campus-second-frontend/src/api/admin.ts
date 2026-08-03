import request from '../utils/request'

/** 仪表盘统计 */
export const getDashboardStats = () =>
  request.get<any>('/api/dashboard/stats').then((res: any) => res.data ?? res)

/** 订单列表（管理端，不传 userid 查全部） */
export const getOrdersListAdmin = (params: {
  page?: number
  limit?: number
  status?: string
}) => request.get<any>('/api/orders/list', { params }).then((res: any) => res.data ?? res)

/** 更新订单状态 */
export const updateOrderStatus = (id: number, status: string) =>
  request.put<any>('/api/orders/updateStatus', null, { params: { id, status } }).then((res: any) => res.data ?? res)
