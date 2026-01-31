<template>
  <div class="page-bg">
    <div class="page">
      <div class="head">
        <div>
          <h1>Login</h1>
          <p class="sub">Authentication is out of scope. Use mock login for prototype/demo.</p>
        </div>
        <div class="chips">
          <span class="chip" :class="isLoggedIn ? 'ok' : ''">
            {{ isLoggedIn ? 'REGISTERED' : 'GUEST' }}
          </span>
          <span class="chip soft">demo</span>
        </div>
      </div>

      <section class="card">
        <div class="card-title">
          <h3>Mock access</h3>
          <span class="badge">demo</span>
        </div>

        <div class="form">
          <div>
            <label>Username</label>
            <input v-model="username" placeholder="username" />
          </div>

          <div>
            <label>Password</label>
            <input v-model="password" type="password" placeholder="password" />
          </div>
        </div>

        <div>
          <p class="hint">
            Tip: If the username does not exist, it will be registered automatically after you submit.
          </p>
        </div>
        
        <div class="row">
          <button class="btn primary" @click="doLogin" :disabled="isLoggedIn">
            Login
          </button>
          <button class="btn secondary" @click="logout" :disabled="!isLoggedIn">
            Logout
          </button>
        </div>

        <div class="divider"></div>
        <p v-if="message" class="msg">
          {{ message }}
        </p>

        <div class="status">
          <div class="k">Current status</div>
          <div class="v">{{ isLoggedIn ? authMode : 'GUEST' }}</div>
        </div>
      </section>
    </div>
  </div>
</template>

<!-- <script setup>
import { ref } from 'vue'
import { isLoggedIn, loginMock, logoutMock } from '../store/session'

const username = ref('')
const password = ref('')

function doLogin() {
  loginMock()
}
</script> -->
<script setup>
import { ref } from 'vue'
import { isLoggedIn, loginMock, logoutMock } from '../store/session'

// 你已有的输入框
const username = ref('')
const password = ref('')

// 额外：保存后端返回的信息（用于 UI 显示、后面创建 trip 的 createdBy）
const userId = ref(null)
const authMode = ref('GUEST') // REGISTER / LOGIN / GUEST
const message = ref('')       // 可选：显示错误/提示

async function doLogin() {
  message.value = ''

  // 简单校验
  if (!username.value.trim() || !password.value) {
    message.value = 'Please enter username and password.'
    return
  }

  try {
    const res = await fetch('https://huzhang-production.up.railway.app/auth/login-or-register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: username.value.trim(),
        password: password.value
      })
    })

    if (res.status === 401) {
      message.value = 'Password incorrect.'
      return
    }
    if (!res.ok) {
      const text = await res.text().catch(() => '')
      message.value = 'Request failed. ' + (text || '')
      return
    }

    const data = await res.json() // { id, username, mode }

    // 1) 保存返回值
    userId.value = data.id
    authMode.value = data.mode || 'LOGIN'

    // 2) 你现有 store 里只有“开关”，先继续用它：表示已登录
    loginMock()

    message.value = `${authMode.value} success: ${data.username} (id=${data.id})`

    // 3) 可选：持久化到 localStorage，刷新不丢
    localStorage.setItem('bbp_userId', String(data.id))
    localStorage.setItem('bbp_username', String(data.username))
    localStorage.setItem('bbp_authMode', String(authMode.value))

  } catch (e) {
    message.value = 'Network error: ' + (e?.message || e)
  }
}

function logout() {
  logoutMock()
  userId.value = null
  authMode.value = 'GUEST'
  message.value = ''
  localStorage.removeItem('bbp_userId')
  localStorage.removeItem('bbp_username')
  localStorage.removeItem('bbp_authMode')
}

// 可选：页面加载时尝试恢复登录态
const savedId = localStorage.getItem('bbp_userId')
if (savedId) {
  userId.value = Number(savedId)
  authMode.value = localStorage.getItem('bbp_authMode') || 'REGISTERED'

}
</script>


<style scoped>
/* ===== Page background ===== */
.page-bg {
  min-height: 100vh;
  width: 100%;
  position: relative;
  background: url("../image/bg-cycling.jpg") center / cover no-repeat fixed;
}
.page-bg::before {
  content: "";
  position: fixed;
  inset: 0;
  background: inherit;
  z-index: -2;
}
.page-bg::after {
  content: "";
  position: fixed;
  inset: 0;
  background: rgba(11, 18, 32, 0.35);
  z-index: -1;
}

/* ===== Page glass panel (same as Explore Routes) ===== */
.page{
  position: relative;
  z-index: 1;
  max-width: 1120px;
  margin: 0 auto;
  padding: 26px 22px;

  background: rgba(255,255,255,0.52);
  backdrop-filter: blur(18px) saturate(140%);
  border: 1px solid rgba(255,255,255,0.22);
  border-radius: 28px;

  box-shadow: 0 40px 110px rgba(0,0,0,0.28);
}

/* ===== Header ===== */
.head{
  display:flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}
h1{
  margin: 0;
  color: rgba(15,23,42,0.92);
  letter-spacing: .2px;
}
.sub{
  margin-top: 8px;
  color: rgba(15,23,42,0.68);
}

/* chips (same as Explore Routes) */
.chips{ display:flex; gap: 8px; flex-wrap: wrap; }
.chip{
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.55);
  border: 1px solid rgba(15,23,42,0.10);
  color: rgba(15,23,42,0.75);
  font-weight: 700;
  font-size: 12px;
}
.chip.soft{ background: rgba(147,197,253,0.18); border-color: rgba(147,197,253,0.30); }
.chip.ok{ background: rgba(16,185,129,0.14); border-color: rgba(16,185,129,0.22); }

/* ===== Card ===== */
.card{
  margin-top: 16px;
  padding: 18px;
  border-radius: 22px;
  background: rgba(255,255,255,0.40);
  border: 1px solid rgba(15,23,42,0.10);
  box-shadow: 0 18px 46px rgba(0,0,0,0.14);
  overflow: hidden;
  max-width: 780px;
  align-items: flex-start;   /* 不拉伸子元素 */
}

.card-title{
  display:flex;
  align-items:center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}
.card h3{ margin: 0; color: rgba(15,23,42,0.90); }

/* badge */
.badge{
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.55);
  border: 1px solid rgba(15,23,42,0.10);
  font-size: 12px;
  font-weight: 800;
  color: rgba(15,23,42,0.70);
}

/* form */
.form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 50px;              /* 🔑 拉开两列距离 */
  margin-top: 10px;

  max-width: 640px;       /* 🔑 限制表单整体宽度 */
}


label{
  display:block;
  margin-top: 12px;
  margin-bottom: 6px;
  color: rgba(15,23,42,0.78);
  font-weight: 700;
  font-size: 13px;
}
input{
  width:100%;
  padding: 11px 12px;
  border-radius: 14px;
  border: 1px solid rgba(15,23,42,0.12);
  background: rgba(255,255,255,0.40);
  outline: none;
  transition: box-shadow 160ms ease, background 160ms ease, border-color 160ms ease;
}
input:focus{
  background: rgba(255,255,255,0.58);
  border-color: rgba(147,197,253,0.70);
  box-shadow: 0 0 0 4px rgba(147,197,253,0.22);
}

/* buttons */
.row{ display:flex; gap: 10px; margin-top: 16px; flex-wrap: wrap; }

.btn{
  border: 0;
  cursor: pointer;
  border-radius: 999px;
  padding: 10px 14px;
  font-weight: 800;
  transition: transform 150ms ease, box-shadow 150ms ease, background 150ms ease;
}
.btn.primary{
  background: rgba(37,99,235,0.88);
  color:#fff;
  box-shadow: 0 16px 34px rgba(37,99,235,0.28);
}
.btn.primary:hover{ transform: translateY(-1px); box-shadow: 0 22px 46px rgba(37,99,235,0.34); }

.btn.secondary{
  background: rgba(255,255,255,0.50);
  border: 1px solid rgba(15,23,42,0.12);
  color: rgba(15,23,42,0.88);
  box-shadow: 0 14px 28px rgba(0,0,0,0.10);
}
.btn.secondary:hover{ transform: translateY(-1px); }
.btn:disabled{ opacity: .65; cursor: not-allowed; box-shadow: none; transform: none; }

.divider{
  height: 1px;
  background: rgba(15,23,42,0.10);
  margin: 16px 0;
}

/* status box */
.status{
  padding: 14px;
  border-radius: 18px;
  background: rgba(255,255,255,0.36);
  border: 1px dashed rgba(15,23,42,0.18);
}
.k{
  font-size: 12px;
  color: rgba(15,23,42,0.62);
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: .6px;
}
.v{
  margin-top: 6px;
  font-size: 18px;
  font-weight: 900;
  color: rgba(15,23,42,0.92);
}
.muted{ color: rgba(15,23,42,0.65); }

@media (max-width: 1000px){
  .form{ grid-template-columns: 1fr; }
  .card{ max-width: 100%; }
}
</style>
