import request from '../utils/request'

export interface AboutusItem {
  id: number
  title: string
  subtitle: string
  content: string
  picture1: string
  picture2: string
  picture3: string
}

export const getAboutusDetail = () =>
  request.get<any>('/api/aboutus/detail').then((res: any) => res.data ?? res)

export const updateAboutus = (data: Partial<AboutusItem>) =>
  request.put<any>('/api/aboutus/update', data).then((res: any) => res.data ?? res)
