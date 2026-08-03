import request from '../utils/request'

export interface NewsItem {
  id: number
  title: string
  introduction: string
  picture: string
  content: string
  addtime: string
}

export const getNewsList = (params: { page?: number; limit?: number }) =>
  request.get<any>('/api/news/list', { params }).then((res: any) => res.data ?? res)

export const getNewsDetail = (id: number) =>
  request.get<any>(`/api/news/${id}`).then((res: any) => res.data ?? res)

export const addNews = (data: Partial<NewsItem>) =>
  request.post<any>('/api/news/add', data).then((res: any) => res.data ?? res)

export const updateNews = (data: Partial<NewsItem>) =>
  request.put<any>('/api/news/update', data).then((res: any) => res.data ?? res)

export const deleteNews = (id: number) =>
  request.delete<any>(`/api/news/${id}`).then((res: any) => res.data ?? res)
