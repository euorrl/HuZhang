import axios from 'axios'

const API_BASE = 'https://huzhang-production.up.railway.app'

export function gpsStart() {
  return axios.get(`${API_BASE}/api/gps/start`).then(r => r.data)
}

export function gpsNext() {
  return axios.get(`${API_BASE}/api/gps/next`).then(r => r.data)
}

export function gpsStop() {
  return axios.get(`${API_BASE}/api/gps/stop`).then(r => r.data)
}
