import axios from 'axios'

// 走 Vite proxy：/api -> http://localhost:9090/api
const http = axios.create({
    baseURL: '/api',
    timeout: 8000,
})

export default http
