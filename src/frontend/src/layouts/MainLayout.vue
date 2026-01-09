<template>
  <div class="app">
    <header class="topbar">
      <div class="brand">BBP</div>

      <nav class="nav">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/explore">Explore Routes</RouterLink>
        <RouterLink to="/community">Community</RouterLink>

        <RouterLink to="/trips" :class="{ disabled: !isLoggedIn }">My Trips</RouterLink>
        <RouterLink to="/report" :class="{ disabled: !isLoggedIn }">Report Path</RouterLink>

        <RouterLink to="/login">Login</RouterLink>

        <button class="ghost" v-if="isLoggedIn" @click="logoutMock">Logout</button>
      </nav>
    </header>

    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { isLoggedIn, logoutMock } from '../store/session'
</script>

<style scoped>

.app {
  font-family: Arial, sans-serif;
  min-height: 100vh;
  position: relative;
  background: url("../image/bg-cycling.jpg") center/cover no-repeat fixed;
}

/* 全局遮罩：保证导航和内容都清晰 */
.app::after {
  content: "";
  position: fixed;
  inset: 0;
  background:
      radial-gradient(1200px 600px at 20% 10%, rgba(37, 99, 235, 0.14), transparent 60%),
      radial-gradient(900px 500px at 80% 20%, rgba(16, 185, 129, 0.10), transparent 55%),
      linear-gradient(180deg, rgba(8, 15, 25, 0.62), rgba(8, 15, 25, 0.38));
  z-index: 0;
}

/* 顶栏浮在背景上 */
.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;

  background: rgba(20, 30, 45, 0.55);
  backdrop-filter: blur(14px) saturate(140%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.brand { font-weight: 800; letter-spacing: 0.5px; color: rgba(255,255,255,0.92); }

.nav { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
.nav a {
  text-decoration: none;
  color: rgba(255,255,255,0.82);
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
}
.nav a.router-link-active {
  background: rgba(147,197,253,0.18);
  border: 1px solid rgba(147,197,253,0.28);
}
.nav a.disabled { opacity: 0.45; pointer-events: none; }
.ghost {
  border: 1px solid rgba(255,255,255,0.18);
  background: rgba(255,255,255,0.10);
  color: rgba(255,255,255,0.86);
  padding: 6px 10px;
  border-radius: 999px;
  cursor: pointer;
}

/* content 改成全宽，真正铺满背景 */
.content {
  position: relative;
  z-index: 1;
  padding: 26px 16px 60px;
}

/* 用 inner 来限制最大宽度，而不是 content 本身 */
.content :deep(.page) {
  max-width: 1120px;
  margin: 0 auto;
}


</style>
