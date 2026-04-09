import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    dedupe: ['axios'],
    alias: {
      '@': resolve(__dirname, 'src'),
      '@sportsant/utils': resolve(__dirname, '../packages/utils/src/index.ts')
    }
  },
  server: {
    port: 5174,
    strictPort: true,
    host: true,
    allowedHosts: true,
    proxy: {
      '/api/iot': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  },
  build: {
    outDir: '../dist',
    emptyOutDir: true
  },
  test: {
    environment: 'jsdom',
    globals: true,
    exclude: ['e2e/**', 'node_modules/**']
  }
})
