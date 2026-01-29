import http from './http'

export function gpsStart() {
  return http.get('/gps/start').then(r => r.data)
}

export function gpsNext() {
  return http.get('/gps/next').then(r => r.data)
}

export function gpsStop() {
  return http.get('/gps/stop').then(r => r.data)
}
