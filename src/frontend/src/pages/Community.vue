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
          <span class="chip soft">{{ filteredPaths.length }} paths</span>
        </div>
      </div>

      <section class="card searchCard">
        <label>Search by street / area</label>
        <input v-model="q" placeholder="e.g., Via Torino" />
      </section>

      <div class="grid">
        <section class="card">
          <div class="card-title">
            <h3>Bike paths</h3>
            <span class="badge">{{ filteredPaths.length }}</span>
          </div>

          <div v-if="filteredPaths.length === 0" class="empty">No matching paths.</div>

          <div class="list" v-else>
            <div class="item" v-for="p in filteredPaths" :key="p.id">
              <div class="title">{{ p.name }}</div>
              <div class="meta">{{ p.summary }} • reports: {{ p.reports.length }}</div>
              <button class="btn tiny" @click="selectedPath = p">See reports</button>
            </div>
          </div>
        </section>

        <section class="card">
          <div class="card-title">
            <h3>Reports</h3>
            <span class="badge soft" v-if="selectedPath">{{ selectedPath.reports.length }}</span>
            <span class="badge" v-else>0</span>
          </div>

          <div v-if="!selectedPath" class="empty">
            Select a bike path to see reports.
          </div>

          <div v-else class="reports">
            <div class="rep" v-for="r in selectedPath.reports" :key="r.id">
              <div class="repTop">
                <span class="pill">{{ r.publishStatus }}</span>
                <span class="muted">{{ r.createdAt }}</span>
              </div>
              <div class="repMeta">
                Condition: <b>{{ r.conditionRating }}</b> / 5 • Safety: <b>{{ r.safetyRating }}</b> / 5
              </div>
              <div class="notes">{{ r.notes }}</div>
            </div>
          </div>
        </section>
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

.card {
  overflow: hidden;      /* ✅ 防止阴影/内部元素造成“视觉重叠” */
}
.searchCard{ margin-top: 16px; }

.card-title{
  display:flex;
  align-items:center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}
.card h3{ margin: 0; color: rgba(15,23,42,0.90); }
.badge{
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.55);
  border: 1px solid rgba(15,23,42,0.10);
  font-size: 12px;
  font-weight: 900;
  color: rgba(15,23,42,0.70);
}
.badge.soft{ background: rgba(147,197,253,0.16); border-color: rgba(147,197,253,0.28); }

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
</style>
