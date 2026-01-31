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
              <button
                class="btn primary"
                @click="() => { useMock = false; start() }"
                :disabled="recording"
              >
                Start
              </button>

              <button
                class="btn primary"
                @click="() => { useMock = true; start() }"
                :disabled="recording"
              >
                Start (Simulate GPS)
              </button>

              <button
                class="btn secondary"
                @click="stop"
                :disabled="!recording"
              >
                Stop
              </button>
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
            <div class="item"
              v-for="t in trips"
              :key="t.id"
              @click="openTrip(t)"
              :class="{ active: selectedTripId === t.id }"
              style="cursor:pointer"
            >
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
                <button class="pill-btn" @click.stop="goReport(t)">
                  Publish
                </button>
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

import { gpsStart, gpsNext, gpsStop } from '../api/gps'
import { createTrip, fetchTripsByUser, fetchTripById } from '../api/trips'
import { useRouter } from 'vue-router' 

const recording = ref(false)
const seconds = ref(0)
const distanceKm = ref(0)
const selectedTripId = ref(null)

const trips = ref([])
let timer = null
const router = useRouter()

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
// let headingRad = 0 // 初始朝向
// function nextMockPoint(prev) {
//   const t = Date.now()
//   if (!prev) {
//     headingRad = 0
//     return { t, lat: DEFAULT_LAT, lon: DEFAULT_LON }
//   }

//   // speed
//   const speed = 20 + Math.random() * 5
//   const dt = 1 // 1 sec per tick
//   const d = speed * dt

//   // 轻微转向（-10°~10°）
//   const turn = ((Math.random() * 20) - 10) * (Math.PI / 180)
//   headingRad += turn

//   const metersPerDegLat = 111320
//   const metersPerDegLon = 111320 * Math.cos((prev.lat * Math.PI) / 180)

//   const dNorth = d * Math.cos(headingRad)
//   const dEast = d * Math.sin(headingRad)

//   const lat = prev.lat + dNorth / metersPerDegLat
//   const lon = prev.lon + dEast / metersPerDegLon

//   return { t, lat, lon }
// }

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

async function loadTrips() {
  const userId = Number(localStorage.getItem('bbp_userId'))
  if (!userId) return

  try {
    const data = await fetchTripsByUser(userId)

    // 后端现在返回 paceMinPerKm / durationSec / distanceKm 等
    // 我们在前端补一个 paceText，方便直接渲染
    trips.value = (data || []).map(t => {
      const paceText = formatPaceFromMinPerKm(t.paceMinPerKm)
      return { ...t, paceText }
    })
  } catch (e) {
    console.error('loadTrips failed', e)
  }
}

function normalizeTrack(track) {
  // 后端可能返回：JSON 字符串 或 数组
  if (!track) return []
  if (Array.isArray(track)) return track
  if (typeof track === 'string') {
    try { return JSON.parse(track) } catch { return [] }
  }
  return []
}

function renderTripOnMap(trackPoints) {
  if (!map) return
  resetPolyline()
  clearMarkers()

  if (!trackPoints || trackPoints.length === 0) return

  // 统一成 {lat, lon}（你 track 里是 {t, lat, lon}）
  const latlngs = trackPoints
    .filter(p => p && p.lat != null && p.lon != null)
    .map(p => [p.lat, p.lon])

  if (latlngs.length === 0) return

  polyline.setLatLngs(latlngs)

  const start = trackPoints[0]
  const end = trackPoints[trackPoints.length - 1]
  setStartMarker(start)
  setEndMarker(end)

  // 视野适配
  map.fitBounds(polyline.getBounds(), { padding: [20, 20] })
}

async function openTrip(t) {
  try {
    selectedTripId.value = t.id

    // 1) 拉详情（含 track）
    const full = await fetchTripById(t.id)

    // 2) 解析 track
    const trackPoints = normalizeTrack(full.track)

    // 3) 渲染到地图
    renderTripOnMap(trackPoints)
  } catch (e) {
    console.error('openTrip failed:', e)
    alert('Failed to load trip detail')
  }
}

function goReport(trip) {
  const id = Number(trip?.id)
  if (!id) return
  router.push({ path: '/report', query: { tripId: id } })
}


async function startmock() {
  if (recording.value) return

  // 1) 通知后端开始本次 trip
  await gpsStart()

  // 2) 前端本地状态重置（保留你原来的逻辑）
  recording.value = true
  seconds.value = 0
  distanceKm.value = 0

  points.value = []
  lastPoint = null
  resetPolyline()
  clearMarkers()

  // 3) 立刻拉第一个点，避免空等 1 秒
  const firstRaw = await gpsNext()
  const first = { t: firstRaw.timestamp ?? Date.now(), lat: firstRaw.lat, lon: firstRaw.lon }

  pushPointAndUpdate(first)
  setStartMarker(first)
  if (map) map.setView([first.lat, first.lon], 16)

  // 4) 每秒拉一次 next
  timer = setInterval(async () => {
    try {
      seconds.value += 1
      const raw = await gpsNext()

      // raw 可能是 not_started 状态（防御一下）
      if (!raw || raw.lat == null || raw.lon == null) return

      // 到终点后：后端会一直返回终点
      // 为了不重复累加距离，我们在 holding=true 时不再 push
      if (raw.holding === true) {
        // 终点 marker 只设一次
        if (points.value.length > 0) {
          const endP = points.value[points.value.length - 1]
          // 如果最后一个点已经是终点，就不重复追加
          if (endP.lat !== raw.lat || endP.lon !== raw.lon) {
            const p = { t: raw.timestamp ?? Date.now(), lat: raw.lat, lon: raw.lon }
            pushPointAndUpdate(p)
          }
        }
        return
      }

      const p = { t: raw.timestamp ?? Date.now(), lat: raw.lat, lon: raw.lon }
      pushPointAndUpdate(p)

    } catch (e) {
      console.error('gpsNext failed:', e)
    }
  }, 1000)
}

let watchId = null
const useMock = ref(false)

async function start() {
  if (recording.value) return
  console.log('[start] clicked, useMock=', useMock?.value)
  // 0) reset 前端状态（共用）
  recording.value = true
  seconds.value = 0
  distanceKm.value = 0

  points.value = []
  lastPoint = null
  resetPolyline()
  clearMarkers()

  // 1) 启动 GPS 数据源（分支）
  if (useMock.value) {
    // --- MOCK：后端生成点 ---
    await gpsStart()

    // 立刻拉第一个点
    const firstRaw = await gpsNext()
    if (!firstRaw || firstRaw.lat == null || firstRaw.lon == null) return

    const first = {
      t: firstRaw.timestamp ?? Date.now(),
      lat: firstRaw.lat,
      lon: firstRaw.lon
    }

    pushPointAndUpdate(first)
    setStartMarker(first)
    if (map) map.setView([first.lat, first.lon], 16)

    // 每秒拉一次 next
    timer = setInterval(async () => {
      try {
        seconds.value += 1
        const raw = await gpsNext()
        if (!raw || raw.lat == null || raw.lon == null) return

        // holding：到终点后不重复追加
        if (raw.holding === true) {
          if (points.value.length > 0) {
            const endP = points.value[points.value.length - 1]
            if (endP.lat !== raw.lat || endP.lon !== raw.lon) {
              const p = { t: raw.timestamp ?? Date.now(), lat: raw.lat, lon: raw.lon }
              pushPointAndUpdate(p)
            }
          }
          return
        }

        const p = { t: raw.timestamp ?? Date.now(), lat: raw.lat, lon: raw.lon }
        pushPointAndUpdate(p)
      } catch (e) {
        console.error('gpsNext failed:', e)
      }
    }, 1000)

  } else {
    // --- REAL：浏览器真实 GPS ---
    if (!navigator.geolocation) {
      console.error('Geolocation not supported')
      recording.value = false
      return
    }

    // UI 秒表仍然可以每秒走（真实 GPS 回调频率不保证 1 秒）
    timer = setInterval(() => {
      seconds.value += 1
    }, 1000)

    watchId = navigator.geolocation.watchPosition(
      (pos) => {
        const { latitude, longitude } = pos.coords
        const t = pos.timestamp ?? Date.now()
        if (latitude == null || longitude == null) return

        const p = { t, lat: latitude, lon: longitude }
        pushPointAndUpdate(p)

        if (points.value.length === 1) {
          setStartMarker(p)
          if (map) map.setView([p.lat, p.lon], 16)
        }
      },
      (err) => {
        console.error('GPS error:', err)
      },
      {
        enableHighAccuracy: true,
        maximumAge: 1000,
        timeout: 10000
      }
    )
  }
}

async function stop() {
  recording.value = false

  // 1) 停 UI 秒表（共用）
  if (timer) clearInterval(timer)
  timer = null

  // 2) 释放 GPS 数据源（分支）
  if (useMock.value) {
    await gpsStop()
  } else {
    if (watchId != null) {
      navigator.geolocation.clearWatch(watchId)
      watchId = null
    }
  }

  // ↓↓↓ 后半段：你原 stop() 的保存逻辑，完全共用 ↓↓↓
  if (points.value.length === 0) return

  const startPoint = points.value[0]
  const endPoint = points.value[points.value.length - 1]

  setEndMarker(endPoint)

  if (polyline && map && points.value.length >= 2) {
    map.fitBounds(polyline.getBounds(), { padding: [20, 20] })
  }

  const paceMinPerKm = calcPaceMinPerKm(seconds.value, distanceKm.value)
  const paceText = formatPaceFromMinPerKm(paceMinPerKm)

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

  const createdBy = Number(localStorage.getItem('bbp_userId'))
  if (!createdBy) {
    alert('Please login first')
    return
  }

  const trackArr = points.value.map(p => ({ t: p.t, lat: p.lat, lon: p.lon }))
  const trackJson = JSON.stringify(trackArr)

  const payload = {
    createdBy,
    date: new Date().toISOString().slice(0, 10),
    durationSec: seconds.value,
    distanceKm: Number(distanceKm.value.toFixed(2)),
    paceMinPerKm: paceMinPerKm ? Number(paceMinPerKm.toFixed(2)) : null,

    startLat: startPoint.lat,
    startLon: startPoint.lon,
    endLat: endPoint.lat,
    endLon: endPoint.lon,

    startPlaceShort,
    startPlaceFull,
    endPlaceShort,
    endPlaceFull,

    track: trackJson,

    conditionRating: 0,
    safetyRating: 0,
    notes: '',
    isPublic: 0,
  }

  try {
    const saved = await createTrip(payload)
    const newId = saved?.id ?? Date.now()

    trips.value.unshift({
      ...payload,
      id: newId,
      paceText,
      track: trackArr,
    })
  } catch (e) {
    console.error('createTrip failed:', e)
    alert('Failed to save trip. Check backend console.')
  }
}


onMounted(() => {
  map = L.map('tripMap', { zoomControl: true }).setView([DEFAULT_LAT, DEFAULT_LON], 13)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap',
  }).addTo(map)

  resetPolyline()
  loadTrips()
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

/* clickable + hover */
.item {
  cursor: pointer;
  transition: transform 120ms ease, box-shadow 120ms ease, border-color 120ms ease, background 120ms ease;
}

.item:hover {
  transform: translateY(-1px);
  box-shadow: 0 18px 40px rgba(0,0,0,0.14);
  border-color: rgba(37,99,235,0.25);
}

/* selected item */
.item.active {
  background: rgba(37,99,235,0.08);
  border-color: rgba(37,99,235,0.35);
  box-shadow: 0 22px 54px rgba(37,99,235,0.18);
  position: relative;
}

/* left accent bar */
.item.active::before {
  content: "";
  position: absolute;
  left: 10px;
  top: 12px;
  bottom: 12px;
  width: 4px;
  border-radius: 999px;
  background: rgba(37,99,235,0.75);
}

.right{
  display:flex;
  gap:8px;
  align-items:center;
}

.pill-btn{
  border: none;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 600;
  background: rgba(151, 164, 248, 0.65);
}

.pill-btn:hover{
  background: rgba(69, 63, 244, 0.85);
}

</style>
