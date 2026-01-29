<template>
  <div class="page-bg">
    <div class="page">
      <!-- Header (same structure as Explore Routes) -->
      <div class="head">
        <div>
          <h1>Best Bike Paths</h1>
          <p class="sub">Explore cycling routes and community reports.</p>
        </div>
        <div class="chips">
          <span class="chip">Prototype</span>
          <span class="chip soft">Milano</span>
        </div>
      </div>

      <!-- Hero (kept, but styled to match the system) -->
      <section class="card hero">
        <div class="hero-text">
          <h2>Ride smarter, not harder</h2>
          <p class="muted">
            Find better routes, check public reports, and (when registered) record your trips and publish path conditions.
          </p>

          <div class="actions">
            <RouterLink class="btn primary" to="/explore">Explore routes</RouterLink>
            <RouterLink class="btn secondary" to="/community">Community reports</RouterLink>
          </div>
        </div>

        <img class="hero-img" src="../image/hero-route.jpg" alt="Cycling route" />
      </section>

      <!-- Two cards grid -->
      <div class="grid">
        <section class="card">
          <div class="card-title">
            <h3>Access modes</h3>
            <span class="badge">info</span>
          </div>

          <ul class="bullets">
            <li><b>Guest:</b> explore routes and public reports</li>
            <li><b>Registered:</b> record trips and publish reports</li>
          </ul>
        </section>

        <section class="card">
          <div class="card-title">
            <h3>Privacy</h3>
            <span class="badge soft">policy</span>
          </div>

          <p class="muted">
            Your reports can be <b>Private</b> (only you) or <b>Public</b> (shared with the community).
          </p>
        </section>
      </div>

      <!-- Map preview -->
      <section class="card">
        <div class="card-title">
          <h3>Map preview</h3>
          <span class="badge soft">preview</span>
        </div>

        <div class="mapbox">
          <div id="map" class="map-container"></div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

onMounted(() => {
  const map = L.map("map").setView([45.4642, 9.19], 13);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "© OpenStreetMap contributors",
  }).addTo(map);
});
</script>

<style scoped>
/* ===== Page background (same as Explore Routes) ===== */
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
.page {
  position: relative;
  z-index: 1;
  max-width: 1120px;
  margin: 0 auto;
  padding: 26px 22px;

  background: rgba(255, 255, 255, 0.52);
  backdrop-filter: blur(18px) saturate(140%);
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 28px;

  box-shadow: 0 40px 110px rgba(0, 0, 0, 0.28);
}

/* ===== Header (same pattern) ===== */
.head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

h1 {
  margin: 0;
  color: rgba(15, 23, 42, 0.92);
  letter-spacing: 0.2px;
}

.sub {
  margin-top: 8px;
  color: rgba(15, 23, 42, 0.68);
}

.chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.chip {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(15, 23, 42, 0.10);
  color: rgba(15, 23, 42, 0.75);
  font-weight: 700;
  font-size: 12px;
}
.chip.soft {
  background: rgba(147, 197, 253, 0.18);
  border-color: rgba(147, 197, 253, 0.30);
}

/* ===== Layout ===== */
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 16px;
  margin-bottom: 18px;
}

@media (max-width: 1000px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

/* ===== Cards (same “clean” style as Explore Routes) ===== */
.card {
  background: rgba(255, 255, 255, 0.46);
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 22px;
  padding: 18px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.14);
  overflow: hidden;
}

.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.card h3 {
  margin: 0;
  color: rgba(15, 23, 42, 0.90);
}

.badge {
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(15, 23, 42, 0.10);
  font-size: 12px;
  font-weight: 800;
  color: rgba(15, 23, 42, 0.70);
}

.badge.soft {
  background: rgba(147, 197, 253, 0.16);
  border-color: rgba(147, 197, 253, 0.28);
}

/* ===== Hero card (still a card, just a bit more structured) ===== */
.hero {
  display: grid;
  grid-template-columns: 1.15fr 1fr;
  gap: 16px;
  margin-top: 16px;
  margin-bottom: 18px;
  align-items: center;
}

.hero-text h2 {
  margin: 0 0 8px;
  color: rgba(15, 23, 42, 0.92);
  letter-spacing: 0.1px;
}

.hero-img {
  width: 100%;
  height: 220px;
  object-fit: cover;
  border-radius: 18px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 30px rgba(0, 0, 0, 0.12);
}

@media (max-width: 1000px) {
  .hero {
    grid-template-columns: 1fr;
  }
  .hero-img {
    height: 180px;
  }
}

/* ===== Buttons (same as Explore Routes) ===== */
.actions {
  display: flex;
  gap: 12px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.btn {
  border: 0;
  cursor: pointer;
  border-radius: 999px;
  padding: 10px 14px;
  font-weight: 800;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 150ms ease, box-shadow 150ms ease, background 150ms ease;
}

.btn.primary {
  background: rgba(37, 99, 235, 0.88);
  color: #fff;
  box-shadow: 0 16px 34px rgba(37, 99, 235, 0.28);
}
.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 22px 46px rgba(37, 99, 235, 0.34);
}

.btn.secondary {
  background: rgba(255, 255, 255, 0.50);
  border: 1px solid rgba(15, 23, 42, 0.12);
  color: rgba(15, 23, 42, 0.88);
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.10);
}
.btn.secondary:hover {
  transform: translateY(-1px);
}

/* ===== Text helpers ===== */
.muted {
  color: rgba(15, 23, 42, 0.65);
}

.bullets {
  margin: 10px 0 0;
  padding-left: 18px;
  color: rgba(15, 23, 42, 0.78);
}

/* ===== Map placeholder ===== */
.mapbox {
  height: 340px;
  margin-top: 10px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.26);
  border: 1px dashed rgba(15, 23, 42, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
}
.hint {
  color: rgba(15, 23, 42, 0.62);
  text-align: center;
  padding: 0 14px;
}
.map-container {
  width: 100%;
  height: 360px;   /* 或 400px */
  border-radius: 12px;
}

</style>
