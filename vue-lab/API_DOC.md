# 博客管理系统 API 接口文档

## 基础信息

- **基础 URL**: `http://localhost:8080/api`
- **数据格式**: JSON
- **认证方式**: Bearer Token (登录后获取)

vue-lab/src/
├── layouts/
│   └── MainLayout.vue     # 主布局组件（含侧边导航栏）
├── router/
│   └── index.ts           # 路由配置
├── stores/
│   └── user.ts            # 用户状态管理
├── types/
│   └── index.ts           # TypeScript 类型定义
├── views/
│   ├── Login.vue          # 登录页面
│   ├── Dashboard.vue      # 数据总览模块
│   ├── WriteBlog.vue      # 撰写博客模块
│   ├── BlogList.vue       # 查看博客模块（含排序功能）
│   └── ApiTest.vue        # 其它测试接口模块
├── App.vue                # 根组件
├── main.ts                # 入口文件
└── style.css              # 全局样式
---

## 目录

1. [用户认证](#1-用户认证)
2. [博客管理](#2-博客管理)
3. [数据统计](#3-数据统计)
4. [测试接口](#4-测试接口)

---

## 1. 用户认证

### 1.1 用户登录

**请求**

```
POST /api/login
Content-Type: application/json
```

**请求体**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**请求参数说明**

| 参数名 | 类型   | 必填 | 说明     |
|--------|--------|------|----------|
| username | string | 是   | 用户名   |
| password | string | 是   | 密码     |

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "admin"
  }
}
```

**失败响应 (401)**

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

---

## 2. 博客管理

### 2.1 获取博客列表

**请求**

```
GET /api/blogs
```

**请求头**

```
Authorization: Bearer <token>
```

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "Vue3 最佳实践",
      "content": "本文介绍Vue3的核心概念...",
      "author": "admin",
      "createTime": "2026-04-08T10:30:00Z",
      "wordCount": 2500
    },
    {
      "id": 2,
      "title": "TypeScript 入门指南",
      "content": "TypeScript是JavaScript的超集...",
      "author": "admin",
      "createTime": "2026-04-07T09:20:00Z",
      "wordCount": 3200
    }
  ]
}
```

**响应参数说明**

| 参数名     | 类型   | 说明       |
|------------|--------|------------|
| id         | number | 博客ID     |
| title      | string | 博客标题   |
| content    | string | 博客内容   |
| author     | string | 作者       |
| createTime | string | 创建时间   |
| wordCount  | number | 字数统计   |

---

### 2.2 发布博客

**请求**

```
POST /api/blogs
Content-Type: application/json
Authorization: Bearer <token>
```

**请求体**

```json
{
  "title": "Vue3 最佳实践",
  "content": "本文介绍Vue3的核心概念和最佳实践，包括组合式API、响应式原理等...",
  "author": "admin",
  "wordCount": 58
}
```

**请求参数说明**

| 参数名  | 类型   | 必填 | 说明     |
|---------|--------|------|----------|
| title   | string | 是   | 博客标题 |
| content | string | 是   | 博客内容 |
| author  | string | 是   | 作者     |
| wordCount | number | 否 | 字数统计 |

**成功响应 (201)**

```json
{
  "code": 201,
  "message": "发布成功",
  "data": {
    "id": 3,
    "title": "Vue3 最佳实践",
    "content": "本文介绍...",
    "author": "admin",
    "createTime": "2026-04-09T14:00:00Z",
    "wordCount": 58
  }
}
```

**失败响应 (400)**

```json
{
  "code": 400,
  "message": "标题和内容不能为空",
  "data": null
}
```

---

### 2.3 获取单篇博客

**请求**

```
GET /api/blogs/:id
Authorization: Bearer <token>
```

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "Vue3 最佳实践",
    "content": "本文介绍Vue3的核心概念...",
    "author": "admin",
    "createTime": "2026-04-08T10:30:00Z",
    "wordCount": 2500
  }
}
```

**失败响应 (404)**

```json
{
  "code": 404,
  "message": "博客不存在",
  "data": null
}
```

---

### 2.4 删除博客

**请求**

```
DELETE /api/blogs/:id
Authorization: Bearer <token>
```

**路径参数说明**

| 参数名 | 类型   | 必填 | 说明   |
|--------|--------|------|--------|
| id     | number | 是   | 博客ID |

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**失败响应 (404)**

```json
{
  "code": 404,
  "message": "博客不存在",
  "data": null
}
```

---

## 3. 数据统计

### 3.1 获取统计数据

**请求**

```
GET /api/blogs/stats
Authorization: Bearer <token>
```

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalBlogs": 12,
    "totalWords": 45678,
    "avgWordsPerBlog": 3806,
    "lastUpdated": "2026-04-09T10:00:00Z"
  }
}
```

**响应参数说明**

| 参数名          | 类型   | 说明         |
|-----------------|--------|--------------|
| totalBlogs      | number | 博客总数     |
| totalWords      | number | 总字数       |
| avgWordsPerBlog | number | 平均每篇字数 |
| lastUpdated     | string | 最后更新时间 |

---

### 3.2 获取最近博客

**请求**

```
GET /api/blogs/recent
Authorization: Bearer <token>
```

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 12,
      "title": "最新的博客标题",
      "author": "admin",
      "createTime": "2026-04-09T15:00:00Z",
      "wordCount": 1800
    }
  ]
}
```

---

## 4. 测试接口

### 4.1 Hello 接口

**请求**

```
GET /api/hello?name={name}
```

**请求参数说明**

| 参数名 | 类型   | 必填 | 说明       |
|--------|--------|------|------------|
| name   | string | 否   | 问候名字   |

**成功响应 (200)**

```json
{
  "message": "Hello, {name}! Welcome to our API.",
  "serverTime": "2026-04-09T12:00:00Z"
}
```

**示例**

```
GET /api/hello?name=John
```

```json
{
  "message": "Hello, John! Welcome to our API.",
  "serverTime": "2026-04-09T12:00:00Z"
}
```

---

### 4.2 查找重复元素接口

**请求**

```
POST /api/findDuplicates
Content-Type: application/json
```

**请求体**

```json
[1, 2, 3, 2, 4, 5, 3, 6]
```

**成功响应 (200)**

```json
{
  "originalArray": [1, 2, 3, 2, 4, 5, 3, 6],
  "duplicates": [2, 3],
  "count": 2
}
```

**响应参数说明**

| 参数名         | 类型     | 说明           |
|----------------|----------|----------------|
| originalArray  | number[] | 原始数组       |
| duplicates     | number[] | 重复的元素列表 |
| count          | number   | 重复元素数量   |

**示例**

```
POST /api/findDuplicates
Body: [1, 2, 3, 2, 4, 5, 3, 6]
```

```json
{
  "originalArray": [1, 2, 3, 2, 4, 5, 3, 6],
  "duplicates": [2, 3],
  "count": 2
}
```

---

## 错误码说明

| 错误码 | 说明               |
|--------|--------------------|
| 200    | 请求成功           |
| 201    | 创建成功           |
| 400    | 请求参数错误       |
| 401    | 未授权/登录失败    |
| 404    | 资源不存在         |
| 500    | 服务器内部错误     |

---

## 开发建议

1. **跨域处理**: 前端运行在 `http://localhost:5173`，后端建议运行在 `http://localhost:8080`，需配置 CORS
2. **Token 存储**: 建议使用 localStorage 存储登录 token
3. **Mock 数据**: 后端未启动时，前端会使用本地 Mock 数据演示
