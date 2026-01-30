<template>
  <div class="page-bg">
    <div class="page">
      <div class="head">
        <div>
          <h1>Explore Routes</h1>
          <p class="sub">Enter an origin and destination. The system returns route options (prototype).</p>
        </div>
        <div class="chips">
          <span class="chip">Prototype</span>
          <span class="chip soft">Milano</span>
        </div>
      </div>

      <div class="grid">
        <section class="card">
          <div class="card-title">
            <h3>Route query</h3>
            <span class="badge">form</span>
          </div>

          <label>Origin</label>
          <input v-model="origin" placeholder="e.g., Piazza Duomo" />

          <label>Destination</label>
          <input v-model="destination" placeholder="e.g., Politecnico di Milano" />

          <div class="row">
            <label class="chk">
              <input
                  type="radio"
                  name="preference"
                  value="safer"
                  v-model="preference"
              />
              Prefer safer
            </label>

            <label class="chk">
              <input
                  type="radio"
                  name="preference"
                  value="shorter"
                  v-model="preference"
              />
              Prefer shorter
            </label>
          </div>

          <button class="btn primary" @click="searchPublic" :disabled="loading">
            {{ loading ? 'Searching...' : 'Search' }}
          </button>

          <div class="divider"></div>

          <div class="card-title">
            <h3>Backend health</h3>
            <span class="badge ok">api</span>
          </div>

          <p class="muted">Call: <code>GET /api/health</code></p>
          <button class="btn secondary" @click="checkHealth" :disabled="loading">
            {{ loading ? 'Checking...' : 'Check health' }}
          </button>

          <p v-if="health" class="state okText">Result: {{ health }}</p>
          <p v-if="err" class="state errText">Error: {{ err }}</p>
        </section>

        <section class="card">
          <div class="card-title">
            <h3>Map</h3>
            <span class="badge soft">preview</span>
          </div>

          <div class="mapbox">
            <div id="map" class="map-container"></div>
          </div>

          <div class="mini" v-if="selected">
            <div class="mini-top">
              <h4>Selected route</h4>
              <span class="pill">score {{ selected.score }}</span>
            </div>
            <div class="mini-meta">
              <div><b>{{ selected.name }}</b></div>
              <div class="muted">Distance: {{ selected.distanceKm }} km • {{ selected.status }}</div>
            </div>
          </div>
        </section>
      </div>

      <section class="card">
        <div class="card-title">
          <h3>Results</h3>
          <span class="badge">{{ results.length }}</span>
        </div>

        <div v-if="results.length === 0" class="empty">
          No results yet. Run a search.
        </div>

        <div class="list" v-else>
          <div class="item" v-for="r in results" :key="r.id">
            <div class="left">
              <div class="title">{{ r.name }}</div>
              <div class="meta">Score {{ r.score }} • {{ r.distanceKm }} km • {{ r.status }}</div>
            </div>
            <button class="btn tiny" @click="selected = r">View</button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getHealth } from '../api/health'

import L from "leaflet";
import "leaflet/dist/leaflet.css";

const origin = ref('')
const destination = ref('')
const preference = ref("safer")

const results = ref([])
const selected = ref(null)

const loading = ref(false)
const health = ref('')
const err = ref('')

let map = null
let routeLayer = null

function buildQuery(params) {
  const qs = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && String(v).trim() !== '') qs.set(k, v)
  })
  return qs.toString()
}

async function searchPublic() {
  loading.value = true
  err.value = ''

  try {
    const qs = buildQuery({
      origin: origin.value,
      destination: destination.value,
      preference: preference.value,
    })

    const res = await fetch(`/api/explore-route/public/search?${qs}`)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)

    const data = await res.json()

    // 映射成你 UI 现在的字段结构（不改模板）
    results.value = (data || []).map(t => ({
      id: t.id,
      name: `${t.startPlaceShort || 'Origin'} → ${t.endPlaceShort || 'Destination'}`,
      score: t.safetyRating ?? '-',                     // score 用 safety_rating
      distanceKm: t.distanceKm ?? '-',                  // km
      status: `cond ${t.conditionRating ?? '-'} / safe ${t.safetyRating ?? '-'}`,
      track: t.track,
      startLat: t.startLat,
      startLon: t.startLon,
      endLat: t.endLat,
      endLon: t.endLon,
    }))

    selected.value = results.value[0] || null
  } catch (e) {
    err.value = e?.message || 'request failed'
  } finally {
    loading.value = false
  }
}

async function checkHealth() {
  loading.value = true
  health.value = ''
  err.value = ''
  try {
    health.value = await getHealth()
  } catch (e) {
    err.value = e?.message || 'request failed'
  } finally {
    loading.value = false
  }
}

function clearRouteLayer() {
  if (routeLayer && map) {
    routeLayer.remove()
    routeLayer = null
  }
}

function drawSelectedRoute(r) {
  if (!map) return

  clearRouteLayer()
  if (!r) return

  // 优先：track 是 GeoJSON 字符串（LineString / Feature / FeatureCollection）
  if (r.track) {
    try {
      const geo = JSON.parse(r.track)
      routeLayer = L.geoJSON(geo)
      routeLayer.addTo(map)

      const bounds = routeLayer.getBounds()
      if (bounds && bounds.isValid()) {
        map.fitBounds(bounds, { padding: [20, 20] })
      }
      return
    } catch (e) {
      // track 不是合法 JSON/GeoJSON，退回用起终点 marker
    }
  }

  // 退回：用起终点 marker
  const pts = []
  const slat = Number(r.startLat)
  const slon = Number(r.startLon)
  const elat = Number(r.endLat)
  const elon = Number(r.endLon)

  if (!Number.isNaN(slat) && !Number.isNaN(slon)) pts.push([slat, slon])
  if (!Number.isNaN(elat) && !Number.isNaN(elon)) pts.push([elat, elon])

  if (pts.length > 0) {
    routeLayer = L.featureGroup(pts.map(p => L.marker(p))).addTo(map)
    map.fitBounds(routeLayer.getBounds(), { padding: [20, 20] })
  }
}

watch(selected, (r) => drawSelectedRoute(r))

onMounted(() => {
  map = L.map("map").setView([45.4642, 9.19], 13);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "© OpenStreetMap contributors",
  }).addTo(map);
});
</script>

<style scoped>
/* ===== Page background ===== */
.page-bg {
  min-height: 100vh;
  width: 100%;
  position: relative;

  /* 真·全屏背景 */
  background: url("../image/bg-cycling.jpg") center / cover no-repeat fixed;

  /* 让背景更稳、两侧不发白（加暗罩） */
}

.page-bg::before {
  content: "";
  position: fixed;          /* 固定在视口，不随滚动露白 */
  inset: 0;
  background: inherit;      /* 继承背景图 */
  z-index: -2;
}

/* 暗色遮罩：让任何边缘都不会“发白” */
.page-bg::after {
  content: "";
  position: fixed;
  inset: 0;
  background: rgba(11, 18, 32, 0.35);
  z-index: -1;
}

/* ===== Page glass panel ===== */
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

/* ===== Layout ===== */
.grid {
  display: grid;
  grid-template-columns: 1fr 1.15fr;
  gap: 16px;
  margin-top: 16px;
  margin-bottom: 18px;   /* ✅ 强制拉开和 Results 的距离 */
}




/* ===== Cards: lighter, cleaner, less “pillow shadow” ===== */
.card {
  overflow: hidden;      /* ✅ 防止阴影/内部元素造成“视觉重叠” */
}
.card-title{
  display:flex;
  align-items:center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}
.card h3{ margin: 0; color: rgba(15,23,42,0.90); }
.badge{
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.55);
  border: 1px solid rgba(15,23,42,0.10);
  font-size: 12px;
  font-weight: 800;
  color: rgba(15,23,42,0.70);
}
.badge.soft{ background: rgba(147,197,253,0.16); border-color: rgba(147,197,253,0.28); }
.badge.ok{ background: rgba(16,185,129,0.14); border-color: rgba(16,185,129,0.22); }

/* ===== Form ===== */
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

.row{ display:flex; gap: 12px; margin-top: 12px; flex-wrap: wrap; }
.chk{
  display:flex;
  align-items:center;
  gap: 8px;

  padding: 8px 12px;
  border-radius: 999px;

  background: rgba(255,255,255,0.34);
  border: 1px solid rgba(15,23,42,0.10);
  color: rgba(15,23,42,0.80);
  font-weight: 700;
  font-size: 13px;
}
.chk input{ width:auto; margin:0; }

/* ===== Buttons ===== */
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
.btn.tiny{
  padding: 8px 12px;
  background: rgba(255,255,255,0.52);
  border: 1px solid rgba(15,23,42,0.10);
  color: rgba(15,23,42,0.88);
  box-shadow: 0 12px 22px rgba(0,0,0,0.10);
}
.btn:disabled{ opacity: .65; cursor: not-allowed; box-shadow: none; transform: none; }

.divider{
  height: 1px;
  background: rgba(15,23,42,0.10);
  margin: 14px 0;
}

/* ===== Map box ===== */
.mapbox{
  height: 360px;
  border-radius: 18px;
  background: rgba(255,255,255,0.26);
  border: 1px dashed rgba(15,23,42,0.18);
  display:flex;
  align-items:center;
  justify-content:center;
  margin-top: 10px;
}
.hint{ color: rgba(15,23,42,0.62); text-align:center; padding: 0 14px; }

/* selected route panel */
.mini{
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid rgba(15,23,42,0.10);
}
.mini-top{
  display:flex;
  align-items:center;
  justify-content: space-between;
  gap: 10px;
}
.mini h4{ margin: 0; color: rgba(15,23,42,0.90); }
.pill{
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(147,197,253,0.18);
  border: 1px solid rgba(147,197,253,0.28);
  color: rgba(15,23,42,0.80);
  font-weight: 900;
  font-size: 12px;
}
.muted{ color: rgba(15,23,42,0.65); }

.state{ margin-top: 10px; font-weight: 800; }
.okText{ color:#0a7a2f; }
.errText{ color:#b00020; }

/* results list */
.list{ display:flex; flex-direction: column; gap: 10px; margin-top: 12px; }
.item{
  display:flex;
  align-items:center;
  justify-content: space-between;
  gap: 12px;

  padding: 14px;
  border-radius: 18px;

  background: rgba(255,255,255,0.46);
  border: 1px solid rgba(15,23,42,0.08);
  box-shadow: 0 14px 30px rgba(0,0,0,0.12);
}
.title{ font-weight: 900; color: rgba(15,23,42,0.92); }
.meta{ color: rgba(15,23,42,0.62); font-size: 14px; margin-top: 4px; }
.empty{
  padding: 14px;
  border-radius: 18px;
  background: rgba(255,255,255,0.36);
  border: 1px dashed rgba(15,23,42,0.18);
  color: rgba(15,23,42,0.65);
}

code{
  background: rgba(255,255,255,0.55);
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid rgba(15,23,42,0.08);
}

@media (max-width: 1000px){
  .grid{ grid-template-columns: 1fr; }
}

.map-container {
  width: 100%;
  height: 360px;   /* 或 400px */
  border-radius: 12px;
}
</style>
