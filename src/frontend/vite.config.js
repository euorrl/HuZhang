import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // 前端请求 /api/... 会转发到后端 9090
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
      },
    },
  },
})
