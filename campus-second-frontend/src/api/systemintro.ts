import request from '../utils/request'

export interface SystemintroItem {
  id: number
  title: string
  subtitle: string
  content: string
  picture1: string
  picture2: string
  picture3: string
}

export const getSystemintroDetail = () =>
  request.get<any>('/api/systemintro/detail').then((res: any) => res.data ?? res)

export const updateSystemintro = (data: Partial<SystemintroItem>) =>
  request.put<any>('/api/systemintro/update', data).then((res: any) => res.data ?? res)
