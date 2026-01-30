<template>
  <div class="page-bg">
    <div class="page">
      <div class="head">
        <div>
          <h1>Report Path</h1>
          <p class="sub">Manual insertion of bike path information (prototype).</p>
        </div>
        <div class="chips">
          <span class="chip" :class="isLoggedIn ? 'ok' : ''">
            {{ isLoggedIn ? 'REGISTERED' : 'GUEST' }}
          </span>
          <span class="chip soft">draft</span>
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
          <!-- Form -->
          <section class="card">
            <div class="card-title">
              <h3>New path report</h3>
              <span class="badge">form</span>
            </div>

            <label>Trip</label>
            <input
              v-model="tripLabel"
              placeholder="Select a trip from history..."
              readonly
              class="readonly"
            />
            <div class="two">
              <div>
                <label>Condition (1–5)</label>
                <select v-model.number="condition">
                  <option :value="0" disabled>Select rating</option>
                  <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
                </select>
              </div>

              <div>
                <label>Safety (1–5)</label>
                <select v-model.number="safety">
                  <option :value="0" disabled>Select rating</option>
                  <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
                </select>
              </div>
            </div>

            <div>
              <label>Notes</label>
              <textarea v-model="notes" rows="4" placeholder="e.g., potholes near the intersection..."></textarea>
            </div>
            <div>
              <button class="btn primary" @click="submit">Submit</button>
            </div>
            <p v-if="msg" class="state okText">{{ msg }}</p>
          </section>

          <!-- List -->
          <section class="card">
            <div class="card-title">
              <h3>My reports</h3>
              <span class="badge">{{ reports.length }}</span>
            </div>

            <div v-if="reports.length === 0" class="empty fill">
              create a report to see it here.
            </div>

            <!-- <div class="list" v-else>
              <div class="item" v-for="r in reports" :key="r.id">
                <div class="left">
                  <div class="title">{{ r.pathName }}</div>
                  <div class="meta">
                    {{ r.publishStatus }} • Condition {{ r.condition }}/5 • Safety {{ r.safety }}/5 • {{ r.createdAt }}
                  </div>
                  <div class="muted" style="margin-top:8px;">{{ r.notes }}</div>
                </div>

                <span class="pill" :class="r.publishStatus === 'PUBLIC' ? 'pub' : 'pri'">
                  {{ r.publishStatus }}
                </span>
              </div>
            </div> -->
            <div class="list" v-else>
              <div class="item" v-for="r in reports" :key="r.id">
                <div class="left">
                  <!-- 标题：起点 - 终点 -->
                  <div class="title">
                    {{ r.startPlaceShort }} - {{ r.endPlaceShort }}
                  </div>

                  <!-- meta：PUBLIC + 评分 + 日期 -->
                  <div class="meta">
                    Condition {{ r.conditionRating }}/5
                    • Safety {{ r.safetyRating }}/5
                    • {{ formatDistance(r.distanceKm) }}
                    • {{ formatDuration(r.durationSec) }}
                    • {{ r.date }}
                  </div>

                  <!-- notes（可能为空） -->
                  <div class="muted" style="margin-top:8px;" v-if="r.notes">
                    {{ r.notes }}
                  </div>
                  <div class="muted" style="margin-top:8px;" v-else>
                    (no notes)
                  </div>
                </div>

                <span class="pill pub">PUBLIC</span>
              </div>
            </div>

          </section>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { isLoggedIn } from '../store/session'
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchTripById, updateTripReport, fetchReportsByUser } from '../api/trips'

const pathName = ref('')
const condition = ref(0)
const safety = ref(0)
const notes = ref('')
const isPublic = ref(false)
const msg = ref('')
const reports = ref([])

const route = useRoute()
const tripId = ref(null)
const tripLabel = ref('')

async function loadTripLabel(id) {
  if (!id) { tripLabel.value = ''; return }

  try {
    const trip = await fetchTripById(id)

    // 后端保存的字段名（看你 MyTrips stop() payload）
    const o = trip?.startPlaceShort || 'Origin'
    const d = trip?.endPlaceShort || 'Destination'
    tripLabel.value = `${o} - ${d}`
  } catch (e) {
    console.error('loadTripLabel failed', e)
    tripLabel.value = ''
  }
}

async function submit() {
  if (!tripId.value) {
    alert('No trip selected')
    return
  }

  if (condition.value === 0 || safety.value === 0) {
    alert('Please select condition and safety (1–5)')
    return
  }

  const payload = {
    conditionRating: condition.value,
    safetyRating: safety.value,
    notes: notes.value || '',
    isPublic: 1,
  }

  try {
    await updateTripReport(tripId.value, payload)
    await loadReports()
    alert('Report saved')
  } catch (e) {
    console.error(e)
    alert('Failed to save report')
  }
}

async function loadReports() {
  const userId = Number(localStorage.getItem('bbp_userId'))
  if (!userId) {
    reports.value = []
    return
  }
  try {
    reports.value = await fetchReportsByUser(userId)
  } catch (e) {
    console.error('loadReports failed', e)
    reports.value = []
  }
}

function formatDuration(sec) {
  const s = Number(sec || 0)
  const m = Math.floor(s / 60)
  const r = s % 60
  return `${m}m ${String(r).padStart(2, '0')}s`
}

function formatDistance(km) {
  const x = Number(km || 0)
  return `${x.toFixed(2)} km`
}

onMounted(() => {
  tripId.value = route.query.tripId ? Number(route.query.tripId) : null
  loadTripLabel(tripId.value)
  loadReports()
})

watch(() => route.query.tripId, async(v) => {
  tripId.value = v ? Number(v) : null
  await loadReports()
  loadTripLabel(tripId.value)
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
.grid {
  display: grid;
  grid-template-columns: 1fr 1.15fr;
  gap: 16px;
  margin-top: 16px;
  margin-bottom: 18px;
  align-items: stretch;              /* ✅ 关键：右侧卡也会拉伸到同一行高度 */
}

/* ===== Cards (same “feel”) ===== */
.card{
  padding: 18px;
  border-radius: 22px;
  background: rgba(255,255,255,0.40);
  border: 1px solid rgba(15,23,42,0.10);
  box-shadow: 0 18px 46px rgba(0,0,0,0.14);
  overflow: hidden;
  display: flex;                     /* ✅ 关键：让内部 empty fill 可以撑开 */
  flex-direction: column;
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

/* ===== Form ===== */
label{
  display:block;
  margin-top: 12px;
  margin-bottom: 6px;
  color: rgba(15,23,42,0.78);
  font-weight: 700;
  font-size: 13px;
}
input, textarea{
  width:100%;
  padding: 11px 12px;
  border-radius: 14px;
  border: 1px solid rgba(15,23,42,0.12);
  background: rgba(255,255,255,0.40);
  outline: none;
  transition: box-shadow 160ms ease, background 160ms ease, border-color 160ms ease;
}
textarea{ resize: vertical; min-height: 110px; }
input:focus, textarea:focus{
  background: rgba(255,255,255,0.58);
  border-color: rgba(147,197,253,0.70);
  box-shadow: 0 0 0 4px rgba(147,197,253,0.22);
}

/* ✅ 让两个数字输入框那一行不再撑满、不会挤在一起 */
.two{
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;            /* 🔑 拉开间距，解决“挤到一起像重叠” */
  max-width: 640px;     /* 🔑 限制这一行的最大宽度（和 Login 同逻辑） */
}

/* ✅ 防止 grid 子项溢出导致挤压 */
.two > div{ min-width: 0; }

/* ✅ 让 input 计算宽度更可控（避免边框/内边距造成溢出） */
input, textarea{
  box-sizing: border-box;
}

/* ✅ 小屏直接单列，彻底杜绝任何重叠 */
@media (max-width: 900px){
  .two{
    grid-template-columns: 1fr;
    max-width: 100%;
  }
}


.chk{
  display:flex;
  align-items:center;
  gap: 8px;
  margin-top: 12px;

  padding: 10px 12px;
  border-radius: 14px;

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
.btn:disabled{ opacity: .65; cursor: not-allowed; box-shadow: none; transform: none; }

/* ===== Empty + List (same style) ===== */
.empty{
  padding: 14px;
  border-radius: 18px;
  background: rgba(255,255,255,0.36);
  border: 1px dashed rgba(15,23,42,0.18);
  color: rgba(15,23,42,0.65);
}
.empty.fill{
  flex: 1;                 /* ✅ 关键：空状态把卡片剩余高度吃满 */
  display:flex;
  align-items:flex-start;
}

.list{ display:flex; flex-direction: column; gap: 10px; margin-top: 12px; }
.item{
  display:flex;
  align-items:flex-start;
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
.muted{ color: rgba(15,23,42,0.65); }

/* tag pill on right */
.pill{
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 900;
  font-size: 12px;
  border: 1px solid rgba(15,23,42,0.10);
  background: rgba(255,255,255,0.55);
  color: rgba(15,23,42,0.75);
  white-space: nowrap;
}
.pill.pub{
  background: rgba(16,185,129,0.14);
  border-color: rgba(16,185,129,0.22);
}
.pill.pri{
  background: rgba(148,163,184,0.16);
  border-color: rgba(148,163,184,0.22);
}

.state{ margin-top: 10px; font-weight: 800; }
.okText{ color:#0a7a2f; }
.errText{ color:#b00020; }

@media (max-width: 1000px){
  .grid{ grid-template-columns: 1fr; }
  .two{ grid-template-columns: 1fr; }
}

.readonly{
  background: rgba(193, 193, 193, 0.8);
  cursor: not-allowed;
}

select {
  width: 100%;
  padding: 8px 10px;
  border-radius: 10px;
  border: 1px solid #c5c5c5;
  background: #c6c5c5;
}


</style>
