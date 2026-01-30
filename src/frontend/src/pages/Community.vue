<template>
  <div class="page-bg">
    <div class="page">
      <div class="head">
        <div>
          <h1>Community</h1>
          <p class="sub">Public bike path information and reports shared by registered users.</p>
        </div>
        <div class="chips">
          <span class="chip">PUBLIC</span>
        </div>
      </div>

      <section class="c searchC">
        <label>Search by street / area</label>
        <input v-model="q" placeholder="e.g., Via Giuseppe Ponzio" />
      </section>

      <div class="grid">
        <!-- Recorder -->
        <section class="card">
          <div class="card-title">
            <h3>Reports</h3>
            <span class="badge">{{ filteredReports.length }}</span>
          </div>

          <div class="list" v-if="filteredReports.length">
            <div
              class="item"
              v-for="r in filteredReports"
              :key="r.id"
              @click="openReport(r)"
              :class="{ active: selectedReportId === r.id }"
            >
              <div class="left">
                <div class="title">
                  {{ r.startPlaceShort }} - {{ r.endPlaceShort }}
                </div>

                <div class="meta">
                  Condition {{ r.conditionRating }}/5
                  • Safety {{ r.safetyRating }}/5
                  • {{ Number(r.distanceKm || 0).toFixed(2) }} km
                  • {{ formatDuration(r.durationSec) }}
                  • {{ (r.date || '').slice(0, 10) }}
                </div>

                <div class="muted" style="margin-top:8px;" v-if="r.notes">
                  {{ r.notes }}
                </div>
                <div class="muted" style="margin-top:8px;" v-else>
                  (no notes)
                </div>
              </div>
            </div>
          </div>
          <div class="empty" v-else>
            No public reports found.
          </div>
        </section>

        <!-- Map -->
        <section class="card">
          <div class="card-title">
            <h3>Trip details</h3>
            <span class="badge soft">map preview</span>
          </div>
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
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { fetchPublicTrips, fetchTripById } from '../api/trips'

const q = ref('')
const reports = ref([])
const selectedReportId = ref(null)

const paths = ref([
  {
    id: 1,
    name: 'Via Torino bike segment',
    summary: 'mostly optimal',
    reports: [
      { id: 11, publishStatus: 'PUBLIC', createdAt: '2025-12-20', conditionRating: 4, safetyRating: 4, notes: 'Good surface, minor bumps near intersection.' },
      { id: 12, publishStatus: 'PUBLIC', createdAt: '2025-12-21', conditionRating: 3, safetyRating: 4, notes: 'Crowded at peak hours.' },
    ],
  },
  {
    id: 2,
    name: 'Navigli canal route',
    summary: 'medium, some potholes',
    reports: [
      { id: 21, publishStatus: 'PUBLIC', createdAt: '2025-12-18', conditionRating: 2, safetyRating: 3, notes: 'Several potholes, ride carefully.' },
    ],
  },
])

const filteredReports = computed(() => {
  const s = q.value.trim().toLowerCase()
  if (!s) return reports.value
  return reports.value.filter(r => {
    const name = `${r.startPlaceShort || ''} ${r.endPlaceShort || ''}`.toLowerCase()
    const notes = (r.notes || '').toLowerCase()
    return name.includes(s) || notes.includes(s)
  })
})

let map = null
let polyline = null
let startMarker = null
let endMarker = null

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

function setStartMarker(p) {
  if (!map) return
  if (startMarker) startMarker.remove()
  startMarker = L.marker([p.lat, p.lon], { title: 'Start' }).addTo(map)
}

function setEndMarker(p) {
  if (!map) return
  if (endMarker) endMarker.remove()
  endMarker = L.marker([p.lat, p.lon], { title: 'End' }).addTo(map)
}

function renderTripOnMap(trackPoints) {
  if (!map) return

  resetPolyline()
  clearMarkers()

  if (!trackPoints || trackPoints.length === 0) return

  const latlngs = trackPoints
    .filter(p => p && p.lat != null && p.lon != null)
    .map(p => [p.lat, p.lon])

  if (latlngs.length === 0) return

  polyline.setLatLngs(latlngs)

  const start = trackPoints[0]
  const end = trackPoints[trackPoints.length - 1]
  setStartMarker(start)
  setEndMarker(end)

  map.fitBounds(polyline.getBounds(), { padding: [20, 20] })
}

function initMap() {
  if (map) return

  const el = document.getElementById('tripMap')
  if (!el) return

  const center = [45.4781, 9.2270]
  const zoom = 13

  map = L.map('tripMap', {
    zoomControl: true,
  }).setView(center, zoom)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors',
  }).addTo(map)

  setTimeout(() => {
    map && map.invalidateSize()
  }, 50)

  resetPolyline()

}

async function loadPublicTrips() {
  console.log('[community] loading public trips...')
  try {
    const data = await fetchPublicTrips()
    console.log('[community] got', data)
    reports.value = data
  } catch (e) {
    console.error('loadPublicTrips failed', e)
    reports.value = []
  }
}

function formatDuration(sec) {
  const s = Number(sec || 0)
  if (!Number.isFinite(s) || s <= 0) return '0m 0s'
  const m = Math.floor(s / 60)
  const r = s % 60
  return `${m}m ${r}s`
}

async function loadReports() {
  reports.value = await fetchPublicTrips()
}

function normalizeTrack(track) {
  if (!track) return []
  if (Array.isArray(track)) return track
  if (typeof track === 'string') {
    try { return JSON.parse(track) } catch { return [] }
  }
  return []
}

async function openReport(r) {
  try {
    selectedReportId.value = r.id

    const full = await fetchTripById(r.id)   // ✅ 现在有 import 了
    const trackPoints = normalizeTrack(full.track)

    renderTripOnMap(trackPoints)
  } catch (e) {
    console.error('openReport failed:', e)
    alert('Failed to load report detail')
  }
}

onMounted(() => {
  initMap()
  loadPublicTrips()
})

onBeforeUnmount(() => {
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

.head{
  display:flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}
h1{ margin:0; color: rgba(15,23,42,0.92); }
.sub{ margin-top: 8px; color: rgba(15,23,42,0.68); }

.chips{ display:flex; gap: 8px; flex-wrap: wrap; }
.chip{
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.55);
  border: 1px solid rgba(15,23,42,0.10);
  color: rgba(15,23,42,0.75);
  font-weight: 800;
  font-size: 12px;
}
.chip.soft{ background: rgba(147,197,253,0.18); border-color: rgba(147,197,253,0.30); }

.c {
  overflow: hidden;      /* ✅ 防止阴影/内部元素造成“视觉重叠” */
}
.searchC{ margin-top: 16px; }

.c-title{
  display:flex;
  align-items:center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}
.c h3{ margin: 0; color: rgba(15,23,42,0.90); }

label{
  display:block;
  margin-bottom: 6px;
  color: rgba(15,23,42,0.78);
  font-weight: 800;
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

.grid{
  display:grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 4px;
}

/* left list */
.list{ display:flex; flex-direction: column; gap: 10px; margin-top: 6px; }
.item{
  border-radius: 18px;
  padding: 14px;

  background: rgba(255,255,255,0.46);
  border: 1px solid rgba(15,23,42,0.08);
  box-shadow: 0 14px 30px rgba(0,0,0,0.12);
}
.title{ font-weight: 900; color: rgba(15,23,42,0.92); }
.meta{ color: rgba(15,23,42,0.62); font-size: 14px; margin-top: 6px; }

.btn{
  border: 0;
  cursor: pointer;
  border-radius: 999px;
  padding: 8px 12px;
  font-weight: 900;
  transition: transform 150ms ease, box-shadow 150ms ease;
}
.btn.tiny{
  margin-top: 10px;
  background: rgba(37,99,235,0.88);
  color:#fff;
  box-shadow: 0 16px 34px rgba(37,99,235,0.28);
}
.btn.tiny:hover{ transform: translateY(-1px); box-shadow: 0 22px 46px rgba(37,99,235,0.34); }

.empty{
  padding: 14px;
  border-radius: 18px;
  background: rgba(255,255,255,0.36);
  border: 1px dashed rgba(15,23,42,0.18);
  color: rgba(15,23,42,0.65);
}

/* reports */
.reports{ display:flex; flex-direction: column; gap: 10px; margin-top: 6px; }
.rep{
  border-radius: 18px;
  padding: 14px;

  background: rgba(255,255,255,0.46);
  border: 1px solid rgba(15,23,42,0.08);
  box-shadow: 0 14px 30px rgba(0,0,0,0.12);
}
.repTop{ display:flex; align-items:center; justify-content: space-between; gap: 10px; }
.pill{
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(16,185,129,0.14);
  border: 1px solid rgba(16,185,129,0.22);
  color: rgba(15,23,42,0.80);
  font-weight: 900;
  font-size: 12px;
}
.muted{ color: rgba(15,23,42,0.60); }
.repMeta{ margin-top: 8px; color: rgba(15,23,42,0.75); }
.notes{ margin-top: 8px; color: rgba(15,23,42,0.82); }

@media (max-width: 1000px){
  .grid{ grid-template-columns: 1fr; }
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

.mapbox{
  height: 360px;        /* ✅ 你可以调成 380/420 */
  margin-top: 10px;
  border-radius: 18px;
  overflow: hidden;
  background: rgba(255,255,255,0.25);
  border: 1px solid rgba(15,23,42,0.10);
}

.card{
  display: flex;
  flex-direction: column;
}
.mapbox{
  flex: 1;
  min-height: 340px;
}

.grid {
  margin-top: 18px;
}

/* ===== clickable list item (Community) ===== */
.item {
  cursor: pointer;
  transition:
    transform 120ms ease,
    box-shadow 120ms ease,
    border-color 120ms ease,
    background 120ms ease;
}

/* hover */
.item:hover {
  transform: translateY(-1px);
  box-shadow: 0 18px 40px rgba(0,0,0,0.14);
  border-color: rgba(37,99,235,0.25);
}

/* selected */
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

</style>
