export interface User {
  username: string
  token?: string
}

export interface Blog {
  id?: number
  title: string
  content: string
  author: string
  createTime?: string
  wordCount?: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface DashboardStats {
  totalBlogs: number
  totalWords: number
  avgWordsPerBlog: number
  lastUpdated: string
}