import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import {defineConfig, loadEnv} from 'vite';

export default defineConfig(({mode}) => {
  const env = loadEnv(mode, '.', '');
  return {
    plugins: [react(), tailwindcss()],
    define: {
      'process.env.GEMINI_API_KEY': JSON.stringify(env.GEMINI_API_KEY),
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, '.'),
      },
    },
    server: {
      hmr: process.env.DISABLE_HMR !== 'true',
      proxy: {
        '/api': {
          target: 'http://127.0.0.1:8080',
          changeOrigin: true,
          rewrite: (path) => {
            // 确保路径始终以 /api 开头
            if (!path.startsWith('/api')) {
              return '/api' + path;
            }
            return path;
          },
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log(`[Proxy] Request: ${req.method} ${req.url}`);
              if (req.body && (req.method === 'POST' || req.method === 'PUT')) {
                const contentType = req.headers['content-type'] || '';
                if (contentType.includes('application/json')) {
                  const bodyData = JSON.stringify(req.body);
                  proxyReq.setHeader('Content-Type', 'application/json');
                  proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
                  proxyReq.write(bodyData);
                }
              }
            });
            proxy.on('proxyRes', (proxyRes) => {
              console.log(`[Proxy] Response status: ${proxyRes.statusCode}`);
            });
            proxy.on('error', (err, req, res) => {
              console.error('[Proxy Error]', err.message);
              if (!res.headersSent) {
                res.status(503).json({ success: false, errMessage: '连接 Java 后端失败' });
              }
            });
          },
        },
      },
    },
    // 优化选项
    optimizeDeps: {
      // 预构建依赖，提升启动速度
      include: ['lucide-react', 'date-fns', 'recharts'],
      // 强制预构建，确保依赖被正确处理
      force: false,
      // 缓存目录，默认在 node_modules/.vite
      // 可以设置为相对路径，如 '.vite-cache'
    },
    // 构建优化
    build: {
      // 启用代码分割
      rollupOptions: {
        output: {
          manualChunks: (id) => {
            if (id.includes('node_modules/react') || id.includes('node_modules/react-dom')) {
              return 'vendor';
            } else if (id.includes('node_modules/recharts')) {
              return 'charts';
            } else if (id.includes('node_modules/motion')) {
              return 'motion';
            }
          },
        },
      },
      // 生成 sourcemap
      sourcemap: false,
    },
    // 缓存配置
    cacheDir: '.vite-cache',
  };
});
