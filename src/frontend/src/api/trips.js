import axios from 'axios'

const API_BASE = 'https://huzhang-production.up.railway.app'

// 创建一条 trip（入库）
export async function createTrip(tripPayload) {
  const res = await axios.post(`${API_BASE}/trips`, tripPayload, {
    headers: { 'Content-Type': 'application/json' },
  })
  return res.data // { id: ... }
}

// 拉取某个用户的 trip 列表（History）
export async function fetchTripsByUser(userId) {
  const res = await axios.get(`${API_BASE}/trips`, { params: { userId } })
  return res.data // [{...}]
}

// （后面点开某条 trip 画轨迹再用）
export async function fetchTripById(id) {
  const res = await axios.get(`${API_BASE}/trips/${id}`)
  return res.data
}

// 更新 trip 的 report 信息
export async function updateTripReport(tripId, payload) {
  const res = await axios.put(
    `${API_BASE}/trips/${tripId}/report`,
    payload,
    { headers: { 'Content-Type': 'application/json' } }
  )
  return res.data
}

export async function fetchReportsByUser(userId) {
  const res = await axios.get(`${API_BASE}/trips/reports`, { params: { userId } })
  return res.data
}

export async function fetchPublicTrips() {
  const res = await axios.get(`${API_BASE}/trips/public`)
  return res.data
}

