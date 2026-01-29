<template>
  <div class="page-bg">
    <div class="page">
      <div class="head">
        <div>
          <h1>My Trips</h1>
          <p class="sub">Record and consult personal bike trips (prototype).</p>
        </div>
        <div class="chips">
          <span class="chip" :class="isLoggedIn ? 'ok' : ''">
            {{ isLoggedIn ? 'REGISTERED' : 'GUEST' }}
          </span>
          <span class="chip soft">prototype</span>
        </div>
      </div>

      <div v-if="!isLoggedIn" class="card">
        <div class="card-title">
          <h3>Access</h3>
          <span class="badge">auth</span>
        </div>

        <div class="empty">
          <div style="font-weight: 900; margin-bottom: 6px;">Registered users only.</div>
          <div class="muted">Go to Login and enable “Logged-in mode”.</div>
          <RouterLink class="btn primary" to="/login" style="display:inline-block; margin-top:12px;">
            Go to Login
          </RouterLink>
        </div>
      </div>

      <template v-else>
        <div class="grid">
          <!-- Recorder -->
          <section class="card">
            <div class="card-title">
              <h3>Recording</h3>
              <span class="badge" :class="recording ? 'warn' : ''">
                {{ recording ? 'recording' : 'idle' }}
              </span>
            </div>

            <p class="muted" style="margin-top:0;">This is a mock recorder (no GPS yet).</p>

            <div class="row">
              <button class="btn primary" @click="start" :disabled="recording">Start</button>
              <button class="btn secondary" @click="stop" :disabled="!recording">Stop</button>
            </div>

            <div class="stats">
              <div class="stat">
                <div class="k">Duration</div>
                <div class="v">{{ seconds }} <span class="u">s</span></div>
              </div>
              <div class="stat">
                <div class="k">Distance</div>
                <div class="v">{{ distanceKm.toFixed(2) }} <span class="u">km</span></div>
              </div>
              <div class="stat">
                <div class="k">Pace (mock)</div>
                <div class="v">{{ pace }}</div>
              </div>
            </div>

            <div class="divider"></div>

            <div class="mini">
              <div class="mini-title">Tips</div>
              <ul class="mini-list">
                <li>In a real version, we would read GPS + timestamps.</li>
                <li>Trips could be stored server-side and visualized on the map.</li>
              </ul>
            </div>
          </section>

          <!-- Map -->
          <section class="card">
            <div class="card-title">
              <h3>Trip details</h3>
              <span class="badge soft">map preview</span>
            </div>

            <!-- <div class="mapbox">
              <div class="hint">
                <div style="font-weight:900; margin-bottom:6px; color: rgba(15,23,42,0.90);">
                  Map placeholder
                </div>
                Trip geometry will render here (later: Leaflet/Mapbox).
              </div>
            </div> -->

            <div class="mapbox">
              <div
                id="tripMap"
                style="
                  height: 100%;
                  width: 100%;
                  border-radius: 12px;
                "
              ></div>
            </div>


            <div class="mini">
              <div class="mini-title">Current session</div>
              <div class="mini-row">
                <span class="pill">{{ recording ? 'Live' : 'Stopped' }}</span>
                <span class="pill">{{ distanceKm.toFixed(2) }} km</span>
                <span class="pill">{{ seconds }} s</span>
              </div>
            </div>
          </section>
        </div>

        <!-- History -->
        <section class="card">
          <div class="card-title">
            <h3>History</h3>
            <span class="badge">{{ trips.length }}</span>
          </div>

          <div v-if="trips.length === 0" class="empty">
            No trips saved yet.
          </div>

          <div v-else class="list">
            <div class="item" v-for="t in trips" :key="t.id">
              <div class="left">
                <div class="title">
                  {{ t.startPlaceShort }} - {{ t.endPlaceShort }}
                </div>
                <div class="meta">
                  {{ t.date }} • {{ t.distanceKm.toFixed(2) }} km • {{ t.durationSec }} s • {{ t.paceText }}
                  <br />
                  {{ t.startPlaceFull }} → {{ t.endPlaceFull }}
                </div>
              </div>
              <div class="right">
                <span class="pill">{{ t.durationSec }}s</span>
                <span class="pill">{{ t.distanceKm.toFixed(1) }}km</span>
              </div>
            </div>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<!-- <script setup>
import { computed, onBeforeUnmount, ref } from 'vue'
import { isLoggedIn } from '../store/session'

const recording = ref(false)
const seconds = ref(0)
const distanceKm = ref(0)

const trips = ref([])
let timer = null

const pace = computed(() => {
  if (seconds.value === 0) return '—'
  const min = seconds.value / 60
  const km = distanceKm.value
  if (km <= 0.01) return '—'
  const minPerKm = min / km
  const m = Math.floor(minPerKm)
  const s = Math.round((minPerKm - m) * 60)
  return `${m}:${String(s).padStart(2, '0')} min/km`
})

function start() {
  recording.value = true
  seconds.value = 0
  distanceKm.value = 0
  timer = setInterval(() => {
    seconds.value += 1
    distanceKm.value += 0.03 // mock speed
  }, 1000)
}

function stop() {
  recording.value = false
  if (timer) clearInterval(timer)
  timer = null

  trips.value.unshift({
    id: Date.now(),
    date: new Date().toISOString().slice(0, 10),
    durationSec: seconds.value,
    distanceKm: distanceKm.value,
  })
}

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script> -->

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { isLoggedIn } from '../store/session'

import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const recording = ref(false)
const seconds = ref(0)
const distanceKm = ref(0)

const trips = ref([])
let timer = null

// ---- GPS track ----
const points = ref([]) // {t, lat, lon}[]
let map = null
let polyline = null
let lastPoint = null
let startMarker = null
let endMarker = null

// Politecnico di Milano – Leonardo Campus
const DEFAULT_LAT = 45.4781
const DEFAULT_LON = 9.2271

const pace = computed(() => {
  if (seconds.value === 0) return '—'
  const km = distanceKm.value
  if (km <= 0.01) return '—'
  const minPerKm = (seconds.value / 60) / km
  const m = Math.floor(minPerKm)
  const s = Math.round((minPerKm - m) * 60)
  return `${m}:${String(s).padStart(2, '0')} min/km`
})

function haversineMeters(a, b) {
  const R = 6371000
  const toRad = (d) => (d * Math.PI) / 180

  const dLat = toRad(b.lat - a.lat)
  const dLon = toRad(b.lon - a.lon)
  const lat1 = toRad(a.lat)
  const lat2 = toRad(b.lat)

  const sinDLat = Math.sin(dLat / 2)
  const sinDLon = Math.sin(dLon / 2)
  const x = sinDLat * sinDLat + Math.cos(lat1) * Math.cos(lat2) * sinDLon * sinDLon
  const c = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x))
  return R * c
}

// 生成更像骑行的 next point：沿某个方向前进 + 轻微摆动
// 这里为了简单，把 heading 放在闭包外维护
let headingRad = 0 // 初始朝向
function nextMockPoint(prev) {
  const t = Date.now()
  if (!prev) {
    headingRad = 0
    return { t, lat: DEFAULT_LAT, lon: DEFAULT_LON }
  }

  // speed
  const speed = 20 + Math.random() * 5
  const dt = 1 // 1 sec per tick
  const d = speed * dt

  // 轻微转向（-10°~10°）
  const turn = ((Math.random() * 20) - 10) * (Math.PI / 180)
  headingRad += turn

  const metersPerDegLat = 111320
  const metersPerDegLon = 111320 * Math.cos((prev.lat * Math.PI) / 180)

  const dNorth = d * Math.cos(headingRad)
  const dEast = d * Math.sin(headingRad)

  const lat = prev.lat + dNorth / metersPerDegLat
  const lon = prev.lon + dEast / metersPerDegLon

  return { t, lat, lon }
}

function resetPolyline() {
  if (!map) return
  if (polyline) {
    polyline.remove()
    polyline = null
  }
  polyline = L.polyline([], { weight: 4, color: 'red' }).addTo(map)
}

function clearMarkers() {
  if (startMarker) { startMarker.remove(); startMarker = null }
  if (endMarker) { endMarker.remove(); endMarker = null }
}

function calcPaceMinPerKm(durationSec, distanceKm) {
  if (!distanceKm || distanceKm <= 0.01) return null
  return (durationSec / 60) / distanceKm
}

function formatPaceFromMinPerKm(paceMinPerKm) {
  if (!paceMinPerKm) return '—'
  const m = Math.floor(paceMinPerKm)
  const s = Math.round((paceMinPerKm - m) * 60)
  return `${m}:${String(s).padStart(2, '0')} min/km`
}

// 逆地理编码：lat/lon -> 可读地址
async function reverseGeocode(lat, lon) {
  const url =
    `https://nominatim.openstreetmap.org/reverse` +
    `?format=jsonv2` +
    `&lat=${lat}` +
    `&lon=${lon}` +
    `&addressdetails=1` +
    `&accept-language=en`

  const res = await fetch(url, { headers: { Accept: 'application/json' } })
  if (!res.ok) throw new Error(`Reverse geocode failed: ${res.status}`)
  const data = await res.json()
  const a = data.address || {}

  // --- 1) short: 你标题的“一级地址”
  // 优先用更像地点的字段，避免总是 Municipio 1
  const short =
    a.attraction ||
    a.amenity ||
    a.tourism ||
    a.shop ||
    a.road ||
    a.pedestrian ||
    a.neighbourhood ||
    a.quarter ||
    a.suburb ||
    a.city_district ||
    a.city ||
    a.town ||
    a.village ||
    'Unknown'

  // --- 2) full: 你 meta 下面显示的“较完整英文地址”
  // 典型结构：house_number + road, suburb/city_district, city, state, country
  const line1 = [a.house_number, a.road || a.pedestrian].filter(Boolean).join(' ')
  const line2 = a.suburb || a.city_district || a.neighbourhood || a.quarter
  const city = a.city || a.town || a.village
  const state = a.state
  const country = a.country

  const fullParts = [line1 || short, line2, city, state, country].filter(Boolean)
  const full = fullParts.join(', ')

  return { short, full }
}

function setStartMarker(p) {
  if (!map) return
  if (startMarker) startMarker.remove()

  startMarker = L.marker([p.lat, p.lon], {
    title: 'Start',
    color: 'green'
  }).addTo(map)
}

function setEndMarker(p) {
  if (!map) return
  if (endMarker) endMarker.remove()

  endMarker = L.marker([p.lat, p.lon], {
    title: 'End',
  }).addTo(map)
}

function pushPointAndUpdate(p) {
  points.value.push(p)

  if (lastPoint) {
    const dM = haversineMeters(lastPoint, p)
    distanceKm.value += dM / 1000
  }
  lastPoint = p

  if (polyline) polyline.addLatLng([p.lat, p.lon])

  // 让地图跟随当前位置（可选）
  if (map) map.panTo([p.lat, p.lon], { animate: true })
}

function start() {
  recording.value = true
  seconds.value = 0
  distanceKm.value = 0

  points.value = []
  lastPoint = null
  resetPolyline()
  clearMarkers()

  const first = nextMockPoint(null)
  pushPointAndUpdate(first)

  setStartMarker(first)
  if (map) map.setView([first.lat, first.lon], 16)

  timer = setInterval(() => {
    seconds.value += 1
    const p = nextMockPoint(lastPoint)
    pushPointAndUpdate(p)
  }, 1000)
}


async function stop() {
  recording.value = false
  if (timer) clearInterval(timer)
  timer = null

  if (points.value.length === 0) return

  const startPoint = points.value[0]
  const endPoint = points.value[points.value.length - 1]

  // 地图终点 marker
  setEndMarker(endPoint)

  if (polyline && map && points.value.length >= 2) {
    map.fitBounds(polyline.getBounds(), { padding: [20, 20] })
  }

  // 计算 pace（min/km 数值 + 文本）
  const paceMinPerKm = calcPaceMinPerKm(seconds.value, distanceKm.value)
  const paceText = formatPaceFromMinPerKm(paceMinPerKm)

  // 逆地理编码
  let startPlaceShort = 'Unknown'
  let startPlaceFull = 'Unknown'
  let endPlaceShort = 'Unknown'
  let endPlaceFull = 'Unknown'

  try {
    const [s, e] = await Promise.all([
      reverseGeocode(startPoint.lat, startPoint.lon),
      reverseGeocode(endPoint.lat, endPoint.lon),
    ])
    startPlaceShort = s.short
    startPlaceFull = s.full
    endPlaceShort = e.short
    endPlaceFull = e.full
  } catch (e) {
    console.warn('Reverse geocoding failed:', e)
  }

  trips.value.unshift({
    id: Date.now(),
    date: new Date().toISOString().slice(0, 10),
    durationSec: seconds.value,
    distanceKm: Number(distanceKm.value.toFixed(2)),

    // 配速
    paceMinPerKm: paceMinPerKm ? Number(paceMinPerKm.toFixed(2)) : null,
    paceText, // 可选：UI 直接用这个字符串

    // 起终点经纬度
    startLat: startPoint.lat,
    startLon: startPoint.lon,
    endLat: endPoint.lat,
    endLon: endPoint.lon,

    // 起终点地址名
    startPlaceShort,
    startPlaceFull,
    endPlaceShort,
    endPlaceFull,

    // 未来给后端保存用：轨迹点
    track: points.value.map(p => ({ t: p.t, lat: p.lat, lon: p.lon })),
  })
}

onMounted(() => {
  map = L.map('tripMap', { zoomControl: true }).setView([DEFAULT_LAT, DEFAULT_LON], 13)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap',
  }).addTo(map)

  resetPolyline()
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
  timer = null
  if (map) {
    map.remove()
    map = null
  }
})
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

/* ===== Layout ===== */
.grid{
  display:grid;
  grid-template-columns: 1fr 1.15fr;
  gap: 16px;
  margin-top: 16px;
  margin-bottom: 18px;
  align-items: start;
}

/* ===== Cards ===== */
.card{
  padding: 18px;
  border-radius: 22px;
  background: rgba(255,255,255,0.40);
  border: 1px solid rgba(15,23,42,0.10);
  box-shadow: 0 18px 46px rgba(0,0,0,0.14);
  overflow: hidden;
}
.card-title{
  display:flex;
  align-items:center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}
.card h3{ margin: 0; color: rgba(15,23,42,0.90); }

/* badges */
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
.badge.warn{ background: rgba(245,158,11,0.16); border-color: rgba(245,158,11,0.26); }

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
.btn:disabled{ opacity: .65; cursor: not-allowed; box-shadow: none; transform: none; }

/* ===== Recorder UI ===== */
.row{ display:flex; gap: 10px; margin-top: 12px; flex-wrap: wrap; }

.stats{
  margin-top: 14px;
  display:grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.stat{
  border-radius: 18px;
  padding: 14px;
  background: rgba(255,255,255,0.46);
  border: 1px solid rgba(15,23,42,0.08);
  box-shadow: 0 14px 30px rgba(0,0,0,0.10);
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
  font-size: 20px;
  font-weight: 900;
  color: rgba(15,23,42,0.92);
}
.u{
  font-size: 12px;
  font-weight: 900;
  color: rgba(15,23,42,0.60);
  margin-left: 6px;
}

.divider{
  height: 1px;
  background: rgba(15,23,42,0.10);
  margin: 14px 0;
}

/* mini */
.mini-title{ font-weight: 900; color: rgba(15,23,42,0.86); }
.mini-list{ margin: 8px 0 0; padding-left: 18px; color: rgba(15,23,42,0.70); }
.mini-row{ display:flex; gap: 8px; flex-wrap: wrap; margin-top: 8px; }

/* map */
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

/* pills */
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

/* history list (same style) */
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
.left{ min-width: 0; }
.right{ display:flex; gap: 8px; flex-wrap: wrap; }
.title{ font-weight: 900; color: rgba(15,23,42,0.92); }
.meta{ color: rgba(15,23,42,0.62); font-size: 14px; margin-top: 4px; }

/* empty */
.empty{
  padding: 14px;
  border-radius: 18px;
  background: rgba(255,255,255,0.36);
  border: 1px dashed rgba(15,23,42,0.18);
  color: rgba(15,23,42,0.65);
}

@media (max-width: 1000px){
  .grid{ grid-template-columns: 1fr; }
  .stats{ grid-template-columns: 1fr; }
}

.mapbox {
  height: 320px;        /* 或 100%，看你布局 */
  background: #f8fafc; /* 可选 */
  border-radius: 12px;
}

</style>
