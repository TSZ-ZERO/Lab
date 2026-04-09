<script setup lang="ts">
import { ref } from 'vue'

const title = ref('')
const content = ref('')
const author = ref('admin')
const loading = ref(false)
const success = ref(false)
const error = ref('')

function calculateWordCount(text: string): number {
  return text.replace(/\s/g, '').length
}

async function submitBlog() {
  if (!title.value.trim()) {
    error.value = '请输入博客标题'
    return
  }
  if (!content.value.trim()) {
    error.value = '请输入博客内容'
    return
  }

  loading.value = true
  error.value = ''
  success.value = false

  try {
    const res = await fetch('/api/blogs', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: title.value,
        content: content.value,
        author: author.value,
        wordCount: calculateWordCount(content.value)
      })
    })

    if (!res.ok) throw new Error('发布失败')

    success.value = true
    title.value = ''
    content.value = ''
  } catch (e: any) {
    error.value = e.message || '发布失败，请重试'
  } finally {
    loading.value = false
  }
}

function clearForm() {
  title.value = ''
  content.value = ''
  error.value = ''
  success.value = false
}
</script>

<template>
  <div class="write-blog">
    <h1>撰写博客</h1>

    <div class="write-form">
      <div class="form-group">
        <label>博客标题</label>
        <input v-model="title" type="text" placeholder="请输入博客标题" />
      </div>

      <div class="form-group">
        <label>作者</label>
        <input v-model="author" type="text" placeholder="作者名称" />
      </div>

      <div class="form-group">
        <label>博客内容</label>
        <textarea
          v-model="content"
          placeholder="请输入博客内容..."
          rows="15"
        ></textarea>
        <div class="word-count">字数: {{ calculateWordCount(content) }}</div>
      </div>

      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-if="success" class="success-message">发布成功!</div>

      <div class="form-actions">
        <button @click="clearForm" class="btn-secondary">清空</button>
        <button @click="submitBlog" :disabled="loading" class="btn-primary">
          {{ loading ? '发布中...' : '发布博客' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.write-blog {
  max-width: 900px;
}

h1 {
  margin-bottom: 30px;
  color: #333;
}

.write-form {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.form-group {
  margin-bottom: 24px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #667eea;
}

textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  box-sizing: border-box;
  line-height: 1.6;
}

textarea:focus {
  outline: none;
  border-color: #667eea;
}

.word-count {
  margin-top: 8px;
  text-align: right;
  font-size: 13px;
  color: #888;
}

.error-message {
  color: #e74c3c;
  margin-bottom: 16px;
  font-size: 14px;
}

.success-message {
  color: #27ae60;
  margin-bottom: 16px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

button {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f0f0f0;
  color: #666;
}

.btn-secondary:hover {
  background: #e0e0e0;
}
</style>