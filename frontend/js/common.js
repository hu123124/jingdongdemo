// ===== 后端地址（部署后改成 https://你的域名） =====
const BASE_URL = '';

// ===== C端（用户端）公共函数 =====

function isLogin() {
    return !!localStorage.getItem('token');
}

function tok() {
    return localStorage.getItem('token');
}

// index.html 里用的别名
function token() {
    return localStorage.getItem('token');
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    location.href = './login.html';
}

function updateNav() {
    var nav = document.getElementById('nav-right');
    if (!nav) return;
    if (isLogin()) {
        nav.innerHTML = '<a href="./coupon.html">优惠券</a>' +
            '<a href="./cart.html">购物车</a>' +
            '<a href="./order.html">我的订单</a>' +
            '<a href="javascript:void(0)" onclick="logout()">退出</a>';
    } else {
        nav.innerHTML = '<a href="./login.html">登录/注册</a>';
    }
}

// C端 toast 提示（适配不同页面的 toast id）
function showResult(msg, ok) {
    var t = document.getElementById('resultToast') || document.getElementById('toast');
    if (!t) return;
    t.textContent = msg;
    t.style.background = ok ? '#27ae60' : '#e74c3c';
    t.style.opacity = '1';
    setTimeout(function () { t.style.opacity = '0'; }, 2000);
}

// ===== B端（管理端）公共函数 =====

function admIsLogin() {
    return !!localStorage.getItem('admToken');
}

function admTok() {
    return localStorage.getItem('admToken');
}

function admLogout() {
    localStorage.removeItem('admToken');
    localStorage.removeItem('admName');
    location.href = './admin-login.html';
}

function admApi(url, method, data) {
    var options = {
        method: method || 'GET',
        headers: {
            'Authorization': 'Bearer ' + admTok(),
            'Content-Type': 'application/json'
        }
    };
    if (data) options.body = JSON.stringify(data);
    return fetch(BASE_URL + url, options).then(function (r) { return r.json(); });
}

// 文件上传（图片）：选完文件自动调后端上传接口，成功回调返回图片 URL
function admUpload(fileInput, cb) {
    var f = fileInput.files[0];
    if (!f) { cb(null); return; }
    var fd = new FormData();
    fd.append('file', f);                       // 字段名必须和后端 @RequestParam("file") 一致
    fetch(BASE_URL + '/api/admin/v1/upload', {
        method: 'POST',
        headers: { 'Authorization': 'Bearer ' + admTok() },   // 接口在 /api/admin/** 下，必须带管理员 token
        body: fd
    }).then(function (r) { return r.json(); }).then(function (res) {
        if (res.code === 200 && res.data && res.data.url) { cb(res.data.url); }
        else { toast((res && res.message) || '上传失败', false); cb(null); }
    }).catch(function () { toast('上传失败', false); cb(null); });
}

function toast(msg, ok) {
    var t = document.getElementById('toast');
    if (!t) return;
    t.textContent = msg;
    t.style.background = ok ? '#27ae60' : '#e74c3c';
    t.style.opacity = '1';
    setTimeout(function () { t.style.opacity = '0'; }, 2000);
}

function fmtTime(ts) {
    if (!ts) return '';
    return ts.replace('T', ' ').substring(0, 19);
}
