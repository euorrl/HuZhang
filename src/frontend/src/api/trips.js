import axios from 'axios'

const API_BASE = 'http://localhost:9090'

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
