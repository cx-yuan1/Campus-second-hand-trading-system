import request from '../utils/request'

export interface ConfigItem {
  id: number
  name: string
  value: string
}

export const getBannerList = () =>
  request.get<any>('/api/config/banner').then((res: any) => res.data ?? res)

export const updateConfig = (data: Partial<ConfigItem>) =>
  request.put<any>('/api/config/update', data).then((res: any) => res.data ?? res)
