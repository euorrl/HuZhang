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
.app { font-family: Arial, sans-serif; }
.topbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; border-bottom: 1px solid #eee;
}
.brand { font-weight: 800; letter-spacing: 0.5px; }
.nav { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
.nav a {
  text-decoration: none; color: #333; padding: 6px 10px; border-radius: 8px;
}
.nav a.router-link-active { background: #f2f2f2; }
.nav a.disabled { opacity: 0.45; pointer-events: none; }
.ghost {
  border: 1px solid #ddd; background: #fff; padding: 6px 10px;
  border-radius: 8px; cursor: pointer;
}
.content { padding: 16px; max-width: 1100px; margin: 0 auto; }
</style>
