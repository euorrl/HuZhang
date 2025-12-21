<template>
  <div>
    <h1>Explore Routes</h1>
    <p class="sub">Enter an origin and destination. The system returns route options (prototype).</p>

    <div class="grid">
      <div class="card">
        <h3>Route query</h3>

        <label>Origin</label>
        <input v-model="origin" placeholder="e.g., Piazza Duomo" />

        <label>Destination</label>
        <input v-model="destination" placeholder="e.g., Politecnico di Milano" />

        <div class="row">
          <label class="chk"><input type="checkbox" v-model="preferSafer" /> Prefer safer</label>
          <label class="chk"><input type="checkbox" v-model="preferShorter" /> Prefer shorter</label>
        </div>

        <button @click="searchMock">Search (mock)</button>

        <hr />

        <h3>Backend health</h3>
        <p>Call: <code>GET /api/health</code></p>
        <button @click="checkHealth" :disabled="loading">
          {{ loading ? 'Checking...' : 'Check health' }}
        </button>

        <p v-if="health" class="ok">Result: {{ health }}</p>
        <p v-if="err" class="err">Error: {{ err }}</p>
      </div>

      <div class="card">
        <h3>Map</h3>
        <div class="mapbox">
          <div class="hint">Route geometry will be rendered here.</div>
        </div>

        <div class="mini" v-if="selected">
          <h4>Selected route</h4>
          <div><b>{{ selected.name }}</b></div>
          <div>Score: {{ selected.score }} | Distance: {{ selected.distanceKm }} km</div>
          <div>Status summary: {{ selected.status }}</div>
        </div>
      </div>
    </div>

    <div class="card">
      <h3>Results</h3>
      <div v-if="results.length === 0" class="hint">No results yet. Run a search.</div>

      <div class="list">
        <div class="item" v-for="r in results" :key="r.id">
          <div class="left">
            <div class="title">{{ r.name }}</div>
            <div class="meta">Score {{ r.score }} • {{ r.distanceKm }} km • {{ r.status }}</div>
          </div>
          <button class="small" @click="selected = r">View</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getHealth } from '../api/health'

const origin = ref('')
const destination = ref('')
const preferSafer = ref(false)
const preferShorter = ref(false)

const results = ref([])
const selected = ref(null)

const loading = ref(false)
const health = ref('')
const err = ref('')

function searchMock() {
  const o = origin.value || 'Origin'
  const d = destination.value || 'Destination'

  results.value = [
    { id: 1, name: `Option A: ${o} → ${d}`, score: preferSafer.value ? 82 : 75, distanceKm: preferShorter.value ? 6.1 : 7.4, status: 'mostly optimal' },
    { id: 2, name: `Option B: ${o} → ${d}`, score: preferSafer.value ? 78 : 72, distanceKm: preferShorter.value ? 6.8 : 8.0, status: 'medium, some potholes' },
  ]
  selected.value = results.value[0]
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
</script>

<style scoped>
.sub { color: #555; margin-top: 8px; }
.grid { display: grid; grid-template-columns: 1fr 1.2fr; gap: 14px; margin-top: 14px; }
.card { border: 1px solid #eee; border-radius: 12px; padding: 14px; }
label { display:block; margin-top: 10px; margin-bottom: 6px; color:#444; font-size: 14px; }
input { width:100%; padding: 10px; border: 1px solid #ddd; border-radius: 10px; }
button { margin-top: 12px; padding: 10px 12px; border: 0; border-radius: 10px; cursor:pointer; background:#f2f2f2; }
.small { margin-top: 0; padding: 8px 10px; border-radius: 10px; }
.row { display:flex; gap: 12px; margin-top: 10px; flex-wrap: wrap; }
.chk { display:flex; gap: 8px; align-items:center; margin: 0; }
.mapbox { height: 360px; border: 1px dashed #bbb; border-radius: 12px; display:flex; align-items:center; justify-content:center; margin-top: 10px; }
.hint { color:#666; }
.ok { color:#0a7a2f; margin-top: 10px; }
.err { color:#b00020; margin-top: 10px; }
.list { display:flex; flex-direction: column; gap: 10px; margin-top: 10px; }
.item { display:flex; justify-content: space-between; align-items:center; border: 1px solid #eee; border-radius: 12px; padding: 12px; }
.title { font-weight: 700; }
.meta { color:#666; font-size: 14px; margin-top: 4px; }
.mini { margin-top: 12px; border-top: 1px solid #eee; padding-top: 12px; }
code { background:#fafafa; padding: 2px 6px; border-radius: 8px; }
hr { border: none; border-top: 1px solid #eee; margin: 14px 0; }
@media (max-width: 1000px) { .grid { grid-template-columns: 1fr; } }
</style>
