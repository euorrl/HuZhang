<template>
  <div>
    <h1>Report Path</h1>
    <p class="sub">Manual insertion of bike path information (prototype).</p>

    <div v-if="!isLoggedIn" class="card">
      <b>Registered users only.</b>
      <p class="hint">Go to Login and enable “Logged-in mode”.</p>
      <RouterLink class="btn" to="/login">Go to Login</RouterLink>
    </div>

    <template v-else>
      <div class="grid">
        <div class="card">
          <h3>New path report</h3>

          <label>Bike path / street</label>
          <input v-model="pathName" placeholder="e.g., Via Torino" />

          <label>Condition rating (1–5)</label>
          <input type="number" min="1" max="5" v-model.number="condition" />

          <label>Safety rating (1–5)</label>
          <input type="number" min="1" max="5" v-model.number="safety" />

          <label>Notes</label>
          <textarea v-model="notes" rows="4" placeholder="e.g., potholes near the intersection..."></textarea>

          <label class="chk">
            <input type="checkbox" v-model="isPublic" />
            Publish to community (PUBLIC)
          </label>

          <button @click="submitMock">Submit (mock)</button>

          <p v-if="msg" class="ok">{{ msg }}</p>
        </div>

        <div class="card">
          <h3>My reports (local mock)</h3>

          <div v-if="reports.length === 0" class="hint">No reports yet.</div>

          <div class="list">
            <div class="item" v-for="r in reports" :key="r.id">
              <div class="title">{{ r.pathName }}</div>
              <div class="meta">
                {{ r.publishStatus }} • Condition {{ r.condition }}/5 • Safety {{ r.safety }}/5 • {{ r.createdAt }}
              </div>
              <div class="notes">{{ r.notes }}</div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { isLoggedIn } from '../store/session'

const pathName = ref('')
const condition = ref(3)
const safety = ref(3)
const notes = ref('')
const isPublic = ref(false)
const msg = ref('')

const reports = ref([])

function submitMock() {
  const record = {
    id: Date.now(),
    pathName: pathName.value || 'Unnamed path',
    condition: condition.value,
    safety: safety.value,
    notes: notes.value || '(no notes)',
    publishStatus: isPublic.value ? 'PUBLIC' : 'PRIVATE',
    createdAt: new Date().toISOString().slice(0, 10),
  }
  reports.value.unshift(record)
  msg.value = `Saved (mock): ${record.pathName} • ${record.publishStatus}`

  // reset a bit
  pathName.value = ''
  notes.value = ''
  isPublic.value = false
}
</script>

<style scoped>
.sub { color:#555; margin-top: 8px; }
.grid { display:grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-top: 14px; }
.card { border: 1px solid #eee; border-radius: 12px; padding: 14px; margin-top: 14px; }
label { display:block; margin-top: 10px; margin-bottom: 6px; color:#444; font-size: 14px; }
input, textarea { width:100%; padding: 10px; border: 1px solid #ddd; border-radius: 10px; }
.chk { display:flex; gap: 8px; align-items:center; margin-top: 12px; }
button { margin-top: 12px; padding: 10px 12px; border: 0; border-radius: 10px; background:#f2f2f2; cursor:pointer; }
.ok { color:#0a7a2f; margin-top: 10px; }
.hint { color:#666; }
.btn { display:inline-block; margin-top: 10px; padding: 10px 12px; background:#f2f2f2; border-radius: 10px; text-decoration:none; color:#111; }
.list { display:flex; flex-direction: column; gap: 10px; margin-top: 10px; }
.item { border: 1px solid #eee; border-radius: 12px; padding: 12px; }
.title { font-weight: 800; }
.meta { color:#666; font-size: 14px; margin-top: 6px; }
.notes { margin-top: 8px; }
@media (max-width: 1000px) { .grid { grid-template-columns: 1fr; } }
</style>
