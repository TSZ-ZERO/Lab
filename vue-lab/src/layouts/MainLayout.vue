<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const currentPath = ref(route.path)

const menuItems = [
  { path: '/', name: 'Dashboard', label: '数据总览', icon: '📊' },
  { path: '/write', name: 'WriteBlog', label: '撰写博客', icon: '✍️' },
  { path: '/blogs', name: 'BlogList', label: '查看博客', icon: '📖' },
  { path: '/apis', name: 'ApiTest', label: '其它测试接口', icon: '🔧' }
]

function navigateTo(path: string) {
  currentPath.value = path
  router.push(path)
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="main-layout">
    <aside class="sidebar">
      <div class="logo">
        <h2>博客管理</h2>
      </div>
      <nav class="menu">
        <div
          v-for="item in menuItems"
          :key="item.path"
          :class="['menu-item', { active: currentPath === item.path }]"
          @click="navigateTo(item.path)"
        >
          <span class="icon">{{ item.icon }}</span>
          <span class="label">{{ item.label }}</span>
        </div>
      </nav>
      <div class="user-info">
        <span>{{ userStore.userInfo?.username }}</span>
        <button @click="handleLogout">退出</button>
      </div>
    </aside>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.main-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h2 {
  margin: 0;
  font-size: 20px;
}

.menu {
  flex: 1;
  padding: 20px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.menu-item.active {
  background: rgba(255, 255, 255, 0.2);
  border-left-color: white;
}

.icon {
  font-size: 20px;
  margin-right: 12px;
}

.label {
  font-size: 15px;
}

.user-info {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info span {
  font-size: 14px;
}

.user-info button {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: background 0.3s;
}

.user-info button:hover {
  background: rgba(255, 255, 255, 0.3);
}

.content {
  flex: 1;
  background: #f5f7fa;
  padding: 30px;
  overflow-y: auto;
}
</style>