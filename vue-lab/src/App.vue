<script setup lang="ts">
import { ref } from 'vue'

const name = ref('')
const message = ref('')
const serverTime = ref('')
const loading = ref(false)
const error = ref('')

async function sendRequest() {
  loading.value = true
  error.value = ''
  message.value = ''
  serverTime.value = ''

  try {
    const res = await fetch(`/api/hello?name=${encodeURIComponent(name.value)}`)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const data = await res.json()
    message.value = data.message
    serverTime.value = data.serverTime
  } catch (e: any) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="container">
    <h1>🔧 Java 后端实验场</h1>
    <p class="subtitle">Vue 3 + TypeScript + Vite</p>

    <div class="card">
      <h2>API 测试: /api/hello</h2>
      <div class="form-group">
        <label for="name">输入名字：</label>
        <input
          id="name"
          v-model="name"
          type="text"
          placeholder="例如：张三"
          @keyup.enter="sendRequest"
        />
        <button @click="sendRequest" :disabled="loading">
          {{ loading ? '请求中...' : '发送请求' }}
        </button>
      </div>

      <div class="response" v-if="message || error">
        <div v-if="error" class="error">
          ❌ 请求失败：{{ error }}
        </div>
        <div v-else class="success">
          <p>✅ 后端响应成功</p>
          <p><strong>消息：</strong>{{ message }}</p>
          <p><strong>服务器时间戳：</strong>{{ serverTime }}</p>
        </div>
      </div>
    </div>

    <div class="info">
      <p>💡 后端 API: <code>GET /api/hello?name=xxx</code></p>
      <p>🚀 Swagger UI: <a href="http://localhost:8080/swagger-ui.html" target="_blank">http://localhost:8080/swagger-ui.html</a></p>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

h1 {
  color: #2c3e50;
  border-left: 5px solid #42b883;
  padding-left: 15px;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #7f8c8d;
  margin-top: 0;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  margin: 1.5rem 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card h2 {
  margin-top: 0;
  color: #34495e;
}

.form-group {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

label {
  font-weight: 500;
}

input {
  padding: 10px 14px;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  flex: 1;
  min-width: 200px;
}

input:focus {
  outline: none;
  border-color: #42b883;
}

button {
  padding: 10px 20px;
  font-size: 1rem;
  background: #42b883;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

button:hover:not(:disabled) {
  background: #369970;
}

button:disabled {
  background: #95a5a6;
  cursor: not-allowed;
}

.response {
  margin-top: 1.5rem;
  padding: 1rem;
  border-radius: 8px;
}

.success {
  background: #e8f5e9;
  color: #2e7d32;
}

.error {
  background: #ffebee;
  color: #c62828;
}

.info {
  background: #f5f7fa;
  padding: 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #7f8c8d;
}

.info code {
  background: #e0e0e0;
  padding: 2px 6px;
  border-radius: 4px;
}

.info a {
  color: #42b883;
}
</style>
