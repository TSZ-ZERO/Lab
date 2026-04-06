<script setup lang="ts">
// 引入 Vue 的 ref 函数，用于创建响应式数据
import { ref } from 'vue'

// ==================== /api/hello 接口相关 ====================
// 用户输入的名字
const name = ref('')
// 后端返回的消息
const message = ref('')
// 后端返回的时间戳
const serverTime = ref('')
// 请求加载状态
const loading = ref(false)
// 错误信息
const error = ref('')

/**
 * 发送 GET 请求到 /api/hello 接口
 * 获取后端的问候消息
 */
async function sendRequest() {
  // 设置加载状态为 true
  loading.value = true
  // 清空错误信息
  error.value = ''
  // 清空之前的结果
  message.value = ''
  serverTime.value = ''

  try {
    // 发送 GET 请求，name 参数进行 URL 编码
    const res = await fetch(`/api/hello?name=${encodeURIComponent(name.value)}`)
    // 检查响应状态码
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    // 解析 JSON 响应
    const data = await res.json()
    // 更新响应数据
    message.value = data.message
    serverTime.value = data.serverTime
  } catch (e: any) {
    // 捕获错误并显示
    error.value = e.message
  } finally {
    // 请求完成，设置加载状态为 false
    loading.value = false
  }
}

// ==================== /api/findDuplicates 接口相关 ====================
// 用户输入的数组字符串
const numsInput = ref('1, 2, 3, 2, 4, 5, 3, 6')
// 后端返回的重复元素结果
const duplicatesResult = ref<any>(null)
// 请求加载状态
const duplicatesLoading = ref(false)
// 错误信息
const duplicatesError = ref('')

/**
 * 发送 POST 请求到 /api/findDuplicates 接口
 * 查找数组中的重复元素
 */
async function findDuplicates() {
  // 设置加载状态为 true
  duplicatesLoading.value = true
  // 清空错误信息
  duplicatesError.value = ''
  // 清空之前的结果
  duplicatesResult.value = null

  try {
    // 将输入的字符串解析为数字数组
    // 1. 按逗号分隔 2. 去除首尾空格 3. 转换为数字 4. 过滤掉非数字
    const nums = numsInput.value
      .split(',')
      .map(s => parseInt(s.trim()))
      .filter(n => !isNaN(n))

    // 发送 POST 请求
    const res = await fetch('/api/findDuplicates', {
      method: 'POST',  // 请求方法
      headers: { 'Content-Type': 'application/json' },  // 请求头
      body: JSON.stringify(nums)  // 请求体，转换为 JSON 字符串
    })
    // 检查响应状态码
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    // 解析 JSON 响应
    duplicatesResult.value = await res.json()
  } catch (e: any) {
    // 捕获错误并显示
    duplicatesError.value = e.message
  } finally {
    // 请求完成，设置加载状态为 false
    duplicatesLoading.value = false
  }
}
</script>

<template>
  <!-- 主容器 -->
  <div class="container">
    <!-- 页面标题 -->
    <h1>🔧 Java 后端实验场</h1>
    <p class="subtitle">Vue 3 + TypeScript + Vite</p>

    <!-- /api/hello 接口测试卡片 -->
    <div class="card">
      <h2>API 测试: /api/hello</h2>
      <!-- 输入表单区域 -->
      <div class="form-group">
        <label for="name">输入名字：</label>
        <!-- 名字输入框，双向绑定到 name 变量 -->
        <input
          id="name"
          v-model="name"
          type="text"
          placeholder="例如：张三"
          @keyup.enter="sendRequest"
        />
        <!-- 发送按钮，点击触发 sendRequest 函数 -->
        <button @click="sendRequest" :disabled="loading">
          {{ loading ? '请求中...' : '发送请求' }}
        </button>
      </div>

      <!-- 响应结果显示区域 -->
      <div class="response" v-if="message || error">
        <!-- 显示错误信息 -->
        <div v-if="error" class="error">
          ❌ 请求失败：{{ error }}
        </div>
        <!-- 显示成功响应 -->
        <div v-else class="success">
          <p>✅ 后端响应成功</p>
          <p><strong>消息：</strong>{{ message }}</p>
          <p><strong>服务器时间戳：</strong>{{ serverTime }}</p>
        </div>
      </div>
    </div>

    <!-- /api/findDuplicates 接口测试卡片 -->
    <div class="card">
      <h2>API 测试: /api/findDuplicates</h2>
      <p class="api-desc">查找数组中的重复元素</p>
      <!-- 输入表单区域 -->
      <div class="form-group">
        <label for="nums">输入数组：</label>
        <!-- 数组输入框，双向绑定到 numsInput 变量 -->
        <input
          id="nums"
          v-model="numsInput"
          type="text"
          placeholder="例如：1, 2, 3, 2, 4, 5, 3"
          @keyup.enter="findDuplicates"
          style="min-width: 300px"
        />
        <!-- 发送按钮，点击触发 findDuplicates 函数 -->
        <button @click="findDuplicates" :disabled="duplicatesLoading">
          {{ duplicatesLoading ? '请求中...' : '查找重复' }}
        </button>
      </div>

      <!-- 响应结果显示区域 -->
      <div class="response" v-if="duplicatesResult || duplicatesError">
        <!-- 显示错误信息 -->
        <div v-if="duplicatesError" class="error">
          ❌ 请求失败：{{ duplicatesError }}
        </div>
        <!-- 显示成功响应 -->
        <div v-else class="success">
          <p>✅ 后端响应成功</p>
          <p><strong>原数组：</strong>[{{ duplicatesResult.originalArray.join(', ') }}]</p>
          <p><strong>重复元素：</strong>[{{ duplicatesResult.duplicates.join(', ') }}]</p>
          <p><strong>重复数量：</strong>{{ duplicatesResult.count }}</p>
        </div>
      </div>
    </div>

    <!-- 底部信息说明 -->
    <div class="info">
      <p>💡 后端 API: <code>GET /api/hello?name=xxx</code></p>
      <p>💡 后端 API: <code>POST /api/findDuplicates</code> (body: int[])</p>
      <p>🚀 Swagger UI: <a href="http://localhost:8080/swagger-ui.html" target="_blank">http://localhost:8080/swagger-ui.html</a></p>
    </div>
  </div>
</template>

<style scoped>
/* ==================== 布局样式 ==================== */
/* 主容器样式 */
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

/* ==================== 标题样式 ==================== */
/* 主标题样式 */
h1 {
  color: #2c3e50;
  border-left: 5px solid #42b883;
  padding-left: 15px;
  margin-bottom: 0.5rem;
}

/* 副标题样式 */
.subtitle {
  color: #7f8c8d;
  margin-top: 0;
}

/* ==================== 卡片样式 ==================== */
/* 卡片容器样式 */
.card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  margin: 1.5rem 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 卡片标题样式 */
.card h2 {
  margin-top: 0;
  color: #34495e;
}

/* API 描述文字样式 */
.api-desc {
  color: #7f8c8d;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

/* ==================== 表单样式 ==================== */
/* 表单组样式 */
.form-group {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

/* 标签样式 */
label {
  font-weight: 500;
}

/* 输入框样式 */
input {
  padding: 10px 14px;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  flex: 1;
  min-width: 200px;
}

/* 输入框聚焦样式 */
input:focus {
  outline: none;
  border-color: #42b883;
}

/* ==================== 按钮样式 ==================== */
/* 按钮基础样式 */
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

/* 按钮悬停样式 */
button:hover:not(:disabled) {
  background: #369970;
}

/* 按钮禁用样式 */
button:disabled {
  background: #95a5a6;
  cursor: not-allowed;
}

/* ==================== 响应结果样式 ==================== */
/* 响应区域容器样式 */
.response {
  margin-top: 1.5rem;
  padding: 1rem;
  border-radius: 8px;
}

/* 成功响应样式 */
.success {
  background: #e8f5e9;
  color: #2e7d32;
}

/* 错误响应样式 */
.error {
  background: #ffebee;
  color: #c62828;
}

/* ==================== 信息说明样式 ==================== */
/* 底部信息区域样式 */
.info {
  background: #f5f7fa;
  padding: 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #7f8c8d;
}

/* 信息区域代码样式 */
.info code {
  background: #e0e0e0;
  padding: 2px 6px;
  border-radius: 4px;
}

/* 信息区域链接样式 */
.info a {
  color: #42b883;
}
</style>
