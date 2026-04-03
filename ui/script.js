document.getElementById('sendBtn').addEventListener('click', async () => {
    const name = document.getElementById('nameInput').value.trim();
    const url = `http://localhost:8080/api/hello?name=${encodeURIComponent(name || '')}`;

    const responseDiv = document.getElementById('responseArea');
    responseDiv.innerHTML = '⏳ 请求中...';

    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        responseDiv.innerHTML = `
            ✅ 后端响应成功<br>
            <strong>消息：</strong> ${data.message}<br>
            <strong>服务器时间戳：</strong> ${data.serverTime}
        `;
    } catch (error) {
        responseDiv.innerHTML = `❌ 请求失败：${error.message}<br>
            请确认后端已启动在 <code>http://localhost:8080</code>`;
    }
});