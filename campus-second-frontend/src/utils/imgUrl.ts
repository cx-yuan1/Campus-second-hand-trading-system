/**
 * 获取图片完整 URL
 * 开发环境通过 vite proxy 转发 /upload 到后端
 * 生产环境需配置 VITE_API_BASE 或与前端同域部署
 */
const API_BASE = import.meta.env.VITE_API_BASE || ''

export const getImgUrl = (path: string | null | undefined): string => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  const relativePath = path.startsWith('/') ? path : `/${path}`
  return API_BASE ? `${API_BASE.replace(/\/$/, '')}${relativePath}` : relativePath
}
