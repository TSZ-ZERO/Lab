// 为发送按钮添加点击事件监听器
document.getElementById('sendBtn').addEventListener('click', async () => {
    // 获取并清理用户输入的名称
    const name = document.getElementById('nameInput').value.trim();
    // 构建API请求URL，对名称参数进行URL编码
    const url = `http://localhost:8080/api/hello?name=${encodeURIComponent(name || '')}`;

    // 获取响应显示区域
    const responseDiv = document.getElementById('responseArea');
    // 显示加载状态
    responseDiv.innerHTML = '⏳ 请求中...';

    try {
        // 发送GET请求到后端API
        const res = await fetch(url);
        // 检查响应状态，非200状态码抛出错误
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        // 解析JSON响应数据
        const data = await res.json();
        // 显示成功响应
        responseDiv.innerHTML = `
            ✅ 后端响应成功<br>
            <strong>消息：</strong> ${data.message}<br>
            <strong>服务器时间戳：</strong> ${data.serverTime}
        `;
    } catch (error) {
        // 处理请求失败情况，显示错误信息
        responseDiv.innerHTML = `❌ 请求失败：${error.message}<br>
            请确认后端已启动在 <code>http://localhost:8080</code>`;
    }
});