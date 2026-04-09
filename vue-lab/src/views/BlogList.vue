<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { Blog } from '../types'

type SortType = 'date' | 'wordCount'
type SortOrder = 'asc' | 'desc'

const blogs = ref<Blog[]>([])
const loading = ref(false)
const sortBy = ref<SortType>('date')
const sortOrder = ref<SortOrder>('desc')
const searchKeyword = ref('')

const sortedBlogs = computed(() => {
  let result = [...blogs.value]

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      blog =>
        blog.title.toLowerCase().includes(keyword) ||
        blog.content.toLowerCase().includes(keyword)
    )
  }

  result.sort((a, b) => {
    let comparison = 0
    if (sortBy.value === 'date') {
      const dateA = new Date(a.createTime || 0).getTime()
      const dateB = new Date(b.createTime || 0).getTime()
      comparison = dateA - dateB
    } else {
      comparison = (a.wordCount || 0) - (b.wordCount || 0)
    }
    return sortOrder.value === 'asc' ? comparison : -comparison
  })

  return result
})

async function fetchBlogs() {
  loading.value = true
  try {
    const res = await fetch('/api/blogs')
    if (res.ok) {
      blogs.value = await res.json()
    }
  } catch (e) {
    blogs.value = [
      { id: 1, title: 'Vue3 最佳实践', content: '本文介绍Vue3的核心概念和最佳实践...', author: 'admin', createTime: '2026-04-08', wordCount: 2500 },
      { id: 2, title: 'TypeScript 入门指南', content: 'TypeScript是JavaScript的超集...', author: 'admin', createTime: '2026-04-07', wordCount: 3200 },
      { id: 3, title: 'Vite 性能优化', content: 'Vite是一个基于Native ESM...', author: 'admin', createTime: '2026-04-06', wordCount: 1800 },
      { id: 4, title: 'Pinia 状态管理', content: 'Pinia是Vue的状态管理库...', author: 'admin', createTime: '2026-04-05', wordCount: 2100 },
      { id: 5, title: 'Vue Router 详解', content: 'Vue Router是Vue的官方路由...', author: 'admin', createTime: '2026-04-04', wordCount: 2800 }
    ]
  } finally {
    loading.value = false
  }
}

function toggleSortOrder() {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
}

onMounted(() => {
  fetchBlogs()
})
</script>

<template>
  <div class="blog-list-view">
    <h1>查看博客</h1>

    <div class="toolbar">
      <div class="search-box">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索博客..."
        />
      </div>
      <div class="sort-controls">
        <span class="sort-label">排序:</span>
        <select v-model="sortBy">
          <option value="date">按日期</option>
          <option value="wordCount">按字数</option>
        </select>
        <button @click="toggleSortOrder" class="sort-order-btn">
          {{ sortOrder === 'asc' ? '↑ 升序' : '↓ 降序' }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="sortedBlogs.length === 0" class="empty-state">
      暂无博客
    </div>
    <div v-else class="blogs-container">
      <div v-for="blog in sortedBlogs" :key="blog.id" class="blog-card">
        <h2 class="blog-title">{{ blog.title }}</h2>
        <div class="blog-meta">
          <span>作者: {{ blog.author }}</span>
          <span>字数: {{ blog.wordCount }}</span>
          <span>{{ blog.createTime }}</span>
        </div>
        <p class="blog-content">{{ blog.content }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.blog-list-view {
  max-width: 1000px;
}

h1 {
  margin-bottom: 30px;
  color: #333;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.search-box input {
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  width: 250px;
  font-size: 14px;
}

.search-box input:focus {
  outline: none;
  border-color: #667eea;
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-label {
  color: #666;
  font-size: 14px;
}

select {
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.sort-order-btn {
  padding: 10px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.sort-order-btn:hover {
  background: #5a6fd6;
}

.loading,
.empty-state {
  text-align: center;
  padding: 60px;
  color: #888;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.blogs-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.blog-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
}

.blog-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.blog-title {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 20px;
}

.blog-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #888;
}

.blog-content {
  color: #666;
  line-height: 1.8;
  margin: 0;
}
</style>