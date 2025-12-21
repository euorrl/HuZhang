<template>
  <div>
    <h1>Community</h1>
    <p class="sub">Public bike path information and reports shared by registered users.</p>

    <div class="card">
      <label>Search by street / area</label>
      <input v-model="q" placeholder="e.g., Via Torino" />
    </div>

    <div class="grid">
      <div class="card">
        <h3>Bike paths (PUBLIC)</h3>

        <div class="list">
          <div class="item" v-for="p in filteredPaths" :key="p.id">
            <div class="title">{{ p.name }}</div>
            <div class="meta">{{ p.summary }} • reports: {{ p.reports.length }}</div>
            <button class="small" @click="selectedPath = p">See reports</button>
          </div>
        </div>

        <div v-if="filteredPaths.length === 0" class="hint">No matching paths.</div>
      </div>

      <div class="card">
        <h3>Reports</h3>

        <div v-if="!selectedPath" class="hint">Select a bike path to see reports.</div>

        <div v-else class="reports">
          <div class="rep" v-for="r in selectedPath.reports" :key="r.id">
            <div class="top">
              <b>{{ r.publishStatus }}</b> • {{ r.createdAt }}
            </div>
            <div class="meta">Condition: {{ r.conditionRating }} / 5 • Safety: {{ r.safetyRating }} / 5</div>
            <div class="notes">{{ r.notes }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const q = ref('')
const selectedPath = ref(null)

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

const filteredPaths = computed(() => {
  const s = q.value.trim().toLowerCase()
  if (!s) return paths.value
  return paths.value.filter(p => p.name.toLowerCase().includes(s) || p.summary.toLowerCase().includes(s))
})
</script>

<style scoped>
.sub { color: #555; margin-top: 8px; }
.card { border: 1px solid #eee; border-radius: 12px; padding: 14px; margin-top: 14px; }
label { display:block; margin-bottom: 6px; color:#444; font-size: 14px; }
input { width:100%; padding: 10px; border: 1px solid #ddd; border-radius: 10px; }
.grid { display:grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.list { display:flex; flex-direction: column; gap: 10px; margin-top: 10px; }
.item { border: 1px solid #eee; border-radius: 12px; padding: 12px; }
.title { font-weight: 800; }
.meta { color:#666; font-size: 14px; margin-top: 6px; }
.small { margin-top: 10px; padding: 8px 10px; border: 0; border-radius: 10px; background:#f2f2f2; cursor:pointer; }
.hint { color:#666; margin-top: 10px; }
.reports { display:flex; flex-direction: column; gap: 10px; margin-top: 10px; }
.rep { border: 1px solid #eee; border-radius: 12px; padding: 12px; }
.top { color:#333; }
.notes { margin-top: 8px; }
@media (max-width: 1000px) { .grid { grid-template-columns: 1fr; } }
</style>
