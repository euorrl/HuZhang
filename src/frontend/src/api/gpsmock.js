import axios from 'axios'

const API_BASE = 'https://huzhang-production.up.railway.app'

export async function gpsStart() {
  const res = await axios.get(`${API_BASE}/gps/start`)
  return res.data
}

export async function gpsNext() {
  const res = await axios.get(`${API_BASE}/gps/next`)
  return res.data
}

export async function gpsStop() {
  const res = await axios.get(`${API_BASE}/gps/stop`)
  return res.data
}
