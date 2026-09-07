// ============================================================
// C端 智能客服悬浮窗（右下角"联系客服"）
// 用法：页面在 <script src="../js/common.js"></script> 之后加：
//       <script src="../js/kefu.js"></script>
// 注意：本脚本可能在 <head> 中被加载（此时 document.body 尚不存在），
//       所以全部 DOM 操作延迟到 DOMContentLoaded 后再执行。
// 连接地址自动推导：
//   https 页面 → wss://同域名/ws（线上）
//   file:// 或 http://localhost:8080（本地开发）→ ws://localhost:8090/ws（本地 Netty）
//   也可在引入前设 window.KEFU_WS 强制指定
// ============================================================
(function () {
    if (window.__kefuLoaded) return;
    window.__kefuLoaded = true;

    function init() {
        // ---- 注入样式 ----
        var css = [
            '#kefu-btn{position:fixed;right:24px;bottom:60px;z-index:9999;width:64px;height:64px;border-radius:50%;',
            'background:#e1251b;color:#fff;border:none;cursor:pointer;font-size:13px;box-shadow:0 4px 12px rgba(0,0,0,.25);',
            'display:flex;align-items:center;justify-content:center;line-height:1.3;font-family:"Microsoft YaHei",sans-serif}',
            '#kefu-btn:hover{background:#c81f16}',
            '#kefu-panel{position:fixed;right:24px;bottom:130px;z-index:9999;width:320px;height:440px;background:#fff;',
            'border-radius:10px;box-shadow:0 6px 24px rgba(0,0,0,.25);display:none;flex-direction:column;overflow:hidden;',
            'font-family:"Microsoft YaHei",sans-serif;font-size:14px}',
            '#kefu-panel.open{display:flex}',
            '#kefu-head{background:#e1251b;color:#fff;padding:12px 14px;font-weight:bold;display:flex;justify-content:space-between;align-items:center}',
            '#kefu-close{cursor:pointer;border:none;background:none;color:#fff;font-size:20px;line-height:1}',
            '#kefu-msgs{flex:1;overflow-y:auto;padding:12px;background:#f5f5f5}',
            '.kefu-row{margin-bottom:10px;display:flex}',
            '.kefu-row.mine{justify-content:flex-end}',
            '.kefu-bubble{max-width:78%;padding:8px 11px;border-radius:8px;word-break:break-word;white-space:pre-wrap}',
            '.kefu-row.bot .kefu-bubble{background:#fff;color:#333;border:1px solid #e5e5e5}',
            '.kefu-row.mine .kefu-bubble{background:#e1251b;color:#fff}',
            '.kefu-row.human .kefu-bubble{background:#fff7e6;color:#ad6800;border:1px solid #ffe1a8}',
            '.kefu-row.sys{justify-content:center}',
            '.kefu-row.sys .kefu-bubble{background:transparent;color:#999;font-size:12px;border:none;max-width:100%}',
            '#kefu-foot{display:flex;border-top:1px solid #eee;padding:8px}',
            '#kefu-input{flex:1;border:1px solid #ddd;border-radius:4px;padding:6px 8px;outline:none}',
            '#kefu-send{margin-left:8px;background:#e1251b;color:#fff;border:none;border-radius:4px;padding:6px 14px;cursor:pointer}'
        ].join('\n');
        var style = document.createElement('style');
        style.textContent = css;
        document.head.appendChild(style);

        // ---- DOM ----
        var btn = document.createElement('button');
        btn.id = 'kefu-btn';
        btn.textContent = '联系客服';
        btn.title = '智能客服';

        var panel = document.createElement('div');
        panel.id = 'kefu-panel';
        panel.innerHTML =
            '<div id="kefu-head"><span>JDemo · 智能客服</span><button id="kefu-close">&times;</button></div>' +
            '<div id="kefu-msgs"></div>' +
            '<div id="kefu-foot"><input id="kefu-input" placeholder="请输入您的问题…"/><button id="kefu-send">发送</button></div>';
        document.body.appendChild(btn);
        document.body.appendChild(panel);

        var msgs = document.getElementById('kefu-msgs');
        var input = document.getElementById('kefu-input');

        function addMsg(role, text) {
            var row = document.createElement('div');
            row.className = 'kefu-row ' + role;
            var b = document.createElement('div');
            b.className = 'kefu-bubble';
            b.textContent = text;
            row.appendChild(b);
            msgs.appendChild(row);
            msgs.scrollTop = msgs.scrollHeight;
        }

        // ---- WebSocket ----
        function resolveWs() {
            if (window.KEFU_WS) return window.KEFU_WS;
            if (location.protocol.indexOf('https') === 0 && location.host) {
                return 'wss://' + location.host + '/ws';      // 线上：走 nginx 反代
            }
            return 'ws://localhost:8090/ws';                  // 本地开发：直连 Netty
        }
        var WS_URL = resolveWs();
        var token = localStorage.getItem('token');       // C 端登录后才有；未登录为游客（服务端按游客处理）
        var greeted = false;
        var retryTimes = 0;                              // 指数退避计数
        var ws = null;

        function buildConnUrl() {
            // 登录用户带 JWT，由服务端握手时校验身份；游客不传 token
            return token ? (WS_URL + '?token=' + encodeURIComponent(token)) : WS_URL;
        }

        function connect() {
            try {
                ws = new WebSocket(buildConnUrl());
            } catch (e) { return; }
            ws.onopen = function () {
                retryTimes = 0;                          // 连上即重置退避
                addMsg('sys', '已连接智能客服');
            };
            ws.onmessage = function (e) {
                var obj;
                try { obj = JSON.parse(e.data); } catch (err) { return; }
                if (obj.type === 'welcome') {
                    if (!greeted) { addMsg('bot', obj.content); greeted = true; }
                } else if (obj.type === 'human_reply') {
                    addMsg('human', '[人工客服] ' + obj.content);
                } else if (obj.type === 'notice') {
                    addMsg('sys', obj.content);
                } else if (obj.type === 'reply') {
                    addMsg('bot', obj.content);
                }
            };
            ws.onclose = function () {
                // 指数退避自动重连：2s → 4s → 8s … 封顶 30s；页面关闭即停止
                var delay = Math.min(30000, Math.pow(2, retryTimes) * 2000);
                retryTimes++;
                addMsg('sys', '连接已断开，' + (delay / 1000) + ' 秒后自动重连…');
                setTimeout(connect, delay);
            };
            ws.onerror = function () { /* 等 onclose 统一处理 */ };
        }

        function send() {
            var text = input.value.trim();
            if (!text) return;
            addMsg('mine', text);
            input.value = '';
            if (ws && ws.readyState === WebSocket.OPEN) {
                ws.send(JSON.stringify({ type: 'chat', content: text }));
            } else {
                addMsg('sys', '连接已断开，正在自动重连…');   // 真正的重连在 onclose 的退避逻辑里
            }
        }

        btn.onclick = function () {
            var open = panel.classList.toggle('open');
            btn.style.display = open ? 'none' : 'flex';
            if (open) {
                input.focus();
                if (!ws || ws.readyState !== WebSocket.OPEN) connect();
            }
        };
        document.getElementById('kefu-close').onclick = function () {
            panel.classList.remove('open');
            btn.style.display = 'flex';
        };
        document.getElementById('kefu-send').onclick = send;
        input.addEventListener('keydown', function (e) { if (e.key === 'Enter') send(); });

        // 页面加载完成即预连接（不弹窗），用户点开即有响应
        connect();
    }

    // 等 DOM 就绪再初始化（脚本可能在 <head> 里被加载，此时 body 还不存在）
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
