<script setup lang="ts">
import { ref } from 'vue'

const name = ref('')
const message = ref('')
const serverTime = ref('')
const loading = ref(false)
const error = ref('')

const numsInput = ref('1, 2, 3, 2, 4, 5, 3, 6')
const duplicatesResult = ref<any>(null)
const duplicatesLoading = ref(false)
const duplicatesError = ref('')

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

async function findDuplicates() {
  duplicatesLoading.value = true
  duplicatesError.value = ''
  duplicatesResult.value = null

  try {
    const nums = numsInput.value
      .split(',')
      .map(s => parseInt(s.trim()))
      .filter(n => !isNaN(n))

    const res = await fetch('/api/findDuplicates', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(nums)
    })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    duplicatesResult.value = await res.json()
  } catch (e: any) {
    duplicatesError.value = e.message
  } finally {
    duplicatesLoading.value = false
  }
}
</script>

<template>
  <div class="api-test">
    <h1>其它测试接口</h1>

    <div class="api-section">
      <h2>/api/hello 接口</h2>
      <div class="form-group">
        <label>输入名字</label>
        <div class="input-row">
          <input v-model="name" type="text" placeholder="请输入名字" />
          <button @click="sendRequest" :disabled="loading">
            {{ loading ? '请求中...' : '发送请求' }}
          </button>
        </div>
      </div>
      <div v-if="error" class="error-box">错误: {{ error }}</div>
      <div v-if="message" class="result-box">
        <p><strong>消息:</strong> {{ message }}</p>
        <p><strong>服务器时间:</strong> {{ serverTime }}</p>
      </div>
    </div>

    <div class="api-section">
      <h2>/api/findDuplicates 接口</h2>
      <div class="form-group">
        <label>输入数字数组</label>
        <div class="input-row">
          <input v-model="numsInput" type="text" placeholder="1, 2, 3, 2, 4, 5, 3, 6" />
          <button @click="findDuplicates" :disabled="duplicatesLoading">
            {{ duplicatesLoading ? '处理中...' : '查找重复' }}
          </button>
        </div>
      </div>
      <div v-if="duplicatesError" class="error-box">错误: {{ duplicatesError }}</div>
      <div v-if="duplicatesResult" class="result-box">
        <p><strong>结果:</strong> {{ JSON.stringify(duplicatesResult) }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.api-test {
  max-width: 800px;
}

h1 {
  margin-bottom: 30px;
  color: #333;
}

.api-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.api-section h2 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.input-row {
  display: flex;
  gap: 12px;
}

.input-row input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.input-row input:focus {
  outline: none;
  border-color: #667eea;
}

.input-row button {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
  white-space: nowrap;
}

.input-row button:hover:not(:disabled) {
  transform: translateY(-2px);
}

.input-row button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-box {
  padding: 12px;
  background: #fee;
  border: 1px solid #fcc;
  border-radius: 6px;
  color: #c33;
  font-size: 14px;
}

.result-box {
  padding: 16px;
  background: #efe;
  border: 1px solid #cfc;
  border-radius: 6px;
  color: #363;
  font-size: 14px;
}

.result-box p {
  margin: 8px 0;
}
</style>