<template>
  <div>
    <h1>Login</h1>
    <p class="sub">Authentication is out of scope. Use mock login for prototype/demo.</p>

    <div class="card">
      <label>Username</label>
      <input v-model="username" placeholder="username" />

      <label>Password</label>
      <input v-model="password" type="password" placeholder="password" />

      <div class="row">
        <button @click="doLogin" :disabled="isLoggedIn">Enable logged-in mode</button>
        <button class="ghost" @click="logoutMock" :disabled="!isLoggedIn">Logout</button>
      </div>

      <p class="hint">
        Current status:
        <b>{{ isLoggedIn ? 'REGISTERED' : 'GUEST' }}</b>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { isLoggedIn, loginMock, logoutMock } from '../store/session'

const username = ref('')
const password = ref('')

function doLogin() {
  // mock: no validation
  loginMock()
}
</script>

<style scoped>
.sub { color:#555; margin-top: 8px; }
.card { border: 1px solid #eee; border-radius: 12px; padding: 14px; margin-top: 14px; max-width: 520px; }
label { display:block; margin-top: 10px; margin-bottom: 6px; color:#444; font-size: 14px; }
input { width:100%; padding: 10px; border: 1px solid #ddd; border-radius: 10px; }
.row { display:flex; gap: 10px; margin-top: 12px; flex-wrap: wrap; }
button { padding: 10px 12px; border: 0; border-radius: 10px; background:#f2f2f2; cursor:pointer; }
button:disabled { opacity: 0.5; cursor:not-allowed; }
.ghost { border: 1px solid #ddd; background:#fff; }
.hint { color:#666; margin-top: 12px; }
</style>
