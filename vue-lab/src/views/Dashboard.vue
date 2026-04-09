<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { DashboardStats } from '../types'

const stats = ref<DashboardStats>({
  totalBlogs: 0,
  totalWords: 0,
  avgWordsPerBlog: 0,
  lastUpdated: ''
})

const recentBlogs = ref<any[]>([])
const loading = ref(false)

async function fetchDashboardData() {
  loading.value = true
  try {
    const res = await fetch('/api/blogs/stats')
    if (res.ok) {
      const data = await res.json()
      stats.value = data
    }
  } catch (e) {
    stats.value = {
      totalBlogs: 12,
      totalWords: 45678,
      avgWordsPerBlog: 3806,
      lastUpdated: new Date().toISOString()
    }
  } finally {
    loading.value = false
  }

  try {
    const res = await fetch('/api/blogs/recent')
    if (res.ok) {
      recentBlogs.value = await res.json()
    }
  } catch (e) {
    recentBlogs.value = [
      { id: 1, title: 'Vue3 最佳实践', author: 'admin', createTime: '2026-04-08', wordCount: 2500 },
      { id: 2, title: 'TypeScript 入门指南', author: 'admin', createTime: '2026-04-07', wordCount: 3200 },
      { id: 3, title: 'Vite 性能优化', author: 'admin', createTime: '2026-04-06', wordCount: 1800 }
    ]
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<template>
  <div class="dashboard">
    <h1>数据总览</h1>
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📝</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalBlogs }}</span>
          <span class="stat-label">博客总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📄</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalWords.toLocaleString() }}</span>
          <span class="stat-label">总字数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.avgWordsPerBlog.toLocaleString() }}</span>
          <span class="stat-label">平均字数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🕐</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.lastUpdated ? new Date(stats.lastUpdated).toLocaleDateString() : '-' }}</span>
          <span class="stat-label">最后更新</span>
        </div>
      </div>
    </div>

    <div class="recent-section">
      <h2>最近博客</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else class="blog-list">
        <div v-for="blog in recentBlogs" :key="blog.id" class="blog-item">
          <div class="blog-title">{{ blog.title }}</div>
          <div class="blog-meta">
            <span>作者: {{ blog.author }}</span>
            <span>字数: {{ blog.wordCount }}</span>
            <span>{{ blog.createTime }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  max-width: 1200px;
}

h1 {
  margin-bottom: 30px;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  font-size: 40px;
  margin-right: 20px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #888;
  margin-top: 4px;
}

.recent-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.recent-section h2 {
  margin-bottom: 20px;
  color: #333;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #888;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.blog-item {
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: border-color 0.3s;
}

.blog-item:hover {
  border-color: #667eea;
}

.blog-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.blog-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #888;
}
</style>