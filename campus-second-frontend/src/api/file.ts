import request from '../utils/request'

/**
 * 上传文件，返回文件路径（如 upload/xxx.jpg）
 */
export const uploadFile = (file: File): Promise<string> => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any>('/api/file/upload', formData, { timeout: 30000 })
    .then((res: any) => {
      const path = res?.data ?? res
      return typeof path === 'string' ? path : ''
    })
}
