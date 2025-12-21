<template>
  <div>
    <h1>My Trips</h1>
    <p class="sub">Record and consult personal bike trips (prototype).</p>

    <div v-if="!isLoggedIn" class="card">
      <b>Registered users only.</b>
      <p class="hint">Go to Login and enable “Logged-in mode”.</p>
      <RouterLink class="btn" to="/login">Go to Login</RouterLink>
    </div>

    <template v-else>
      <div class="grid">
        <div class="card">
          <h3>Recording</h3>
          <p class="hint">This is a mock recorder (no GPS yet).</p>

          <div class="row">
            <button @click="start" :disabled="recording">Start</button>
            <button @click="stop" :disabled="!recording">Stop</button>
          </div>

          <div class="stats">
            <div><b>Duration:</b> {{ seconds }} s</div>
            <div><b>Distance:</b> {{ distanceKm.toFixed(2) }} km</div>
          </div>
        </div>

        <div class="card">
          <h3>Trip details (placeholder)</h3>
          <div class="mapbox">
            <div class="hint">Trip map will render here.</div>
          </div>
        </div>
      </div>

      <div class="card">
        <h3>History</h3>
        <div v-if="trips.length === 0" class="hint">No trips saved yet.</div>

        <div class="list">
          <div class="item" v-for="t in trips" :key="t.id">
            <div>
              <div class="title">Trip #{{ t.id }}</div>
              <div class="meta">{{ t.date }} • {{ t.distanceKm.toFixed(2) }} km • {{ t.durationSec }} s</div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, ref } from 'vue'
import { isLoggedIn } from '../store/session'

const recording = ref(false)
const seconds = ref(0)
const distanceKm = ref(0)

const trips = ref([])
let timer = null

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
</script>

<style scoped>
.sub { color:#555; margin-top: 8px; }
.card { border: 1px solid #eee; border-radius: 12px; padding: 14px; margin-top: 14px; }
.grid { display:grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-top: 14px; }
.row { display:flex; gap: 10px; margin-top: 10px; flex-wrap: wrap; }
button { padding: 10px 12px; border: 0; border-radius: 10px; background:#f2f2f2; cursor:pointer; }
button:disabled { opacity: 0.5; cursor:not-allowed; }
.stats { margin-top: 12px; display:flex; gap: 18px; flex-wrap: wrap; }
.mapbox { height: 320px; border: 1px dashed #bbb; border-radius: 12px; display:flex; align-items:center; justify-content:center; margin-top: 10px; }
.hint { color:#666; }
.btn { display:inline-block; margin-top: 10px; padding: 10px 12px; background:#f2f2f2; border-radius: 10px; text-decoration:none; color:#111; }
.list { display:flex; flex-direction: column; gap: 10px; margin-top: 10px; }
.item { border: 1px solid #eee; border-radius: 12px; padding: 12px; }
.title { font-weight: 800; }
.meta { color:#666; font-size: 14px; margin-top: 4px; }
@media (max-width: 1000px) { .grid { grid-template-columns: 1fr; } }
</style>
