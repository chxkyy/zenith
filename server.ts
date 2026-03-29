import express from "express";
import path from "path";
import fs from "fs";
import { createServer as createViteServer } from "vite";
import { createProxyMiddleware } from "http-proxy-middleware";

async function startServer() {
  const app = express();
  const PORT = 3000;

  app.use(express.json());

  // 1. 系统日志 API (本地处理)
  const LOG_FILE = path.join(process.cwd(), "java_backend.log");
  app.get("/api/system/logs", (req, res) => {
    if (fs.existsSync(LOG_FILE)) {
      const logs = fs.readFileSync(LOG_FILE, 'utf8');
      res.send(logs);
    } else {
      res.send("日志文件尚未生成...");
    }
  });

  // 2. 代理 API 请求到 Java 后端
  app.use("/api", createProxyMiddleware({
    target: "http://127.0.0.1:8080",
    changeOrigin: true,
    on: {
      proxyReq: (proxyReq: any, req: any) => {
        /**
         * 强制补齐路径修复：
         * 既然 Java 端需要 http://127.0.0.1:8080/api/users/page
         * 而目前它报错说找不到 users/page，说明转发的是 /users/page。
         * 我们在这里强制把 /api 塞回去。
         */
        if (!proxyReq.path.startsWith('/api')) {
          proxyReq.path = '/api' + proxyReq.path;
        }

        console.log(`[Proxy] Final URL: http://127.0.0.1:8080${proxyReq.path}`);

        // 重新写入 Body
        if (req.body && (req.method === 'POST' || req.method === 'PUT')) {
          const bodyData = JSON.stringify(req.body);
          proxyReq.setHeader('Content-Type', 'application/json');
          proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
          proxyReq.write(bodyData);
        }
      },
      proxyRes: (proxyRes: any) => {
        console.log(`[Proxy] Status: ${proxyRes.statusCode}`);
      },
      error: (err: any, req: any, res: any) => {
        console.error("[Proxy Error]", err.message);
        if (!res.headersSent) {
          res.status(503).json({ success: false, errMessage: "连接 Java 后端失败" });
        }
      }
    }
  }));

  // 3. 兜底 API 404
  app.all("/api/*", (req, res) => {
    res.status(404).json({ success: false, errMessage: "API route not found" });
  });

  // Vite 开发环境配置
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use((req, res, next) => {
      if (req.path.startsWith('/api')) {
        next();
      } else {
        vite.middlewares(req, res, next);
      }
    });
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  // 4. 错误处理
  app.use((err: any, req: any, res: any, next: any) => {
    if (req.path.startsWith('/api')) {
      return res.status(500).json({ success: false, errMessage: err.message });
    }
    next(err);
  });

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

startServer();