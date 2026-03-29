import express from "express";
import path from "path";
import fs from "fs";
import { createServer as createViteServer } from "vite";
import { createProxyMiddleware } from "http-proxy-middleware";

async function startServer() {
  const app = express();
  const PORT = 3000;

  // 必须先解析 JSON，否则 proxyReq 里的 req.body 为空
  app.use(express.json());

  // 请求日志中间件
  app.use((req, res, next) => {
    if (req.path.startsWith('/api')) {
      console.log(`[Server] Received API Request: ${req.method} ${req.path}`);
    }
    next();
  });

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
  // 修改点：直接 app.use 不带路径挂载，通过 pathFilter 拦截
  // 这样 req.url 会保持完整的 "/api/users/page" 转发给后端
  app.use(createProxyMiddleware({
    pathFilter: '/api',
    target: "http://127.0.0.1:8080", // 建议用 127.0.0.1 避免 localhost 解析延迟
    changeOrigin: true,
    on: {
      proxyReq: (proxyReq: any, req: any) => {
        // 关键：打印最终发送给 Java 的完整路径，方便你验证
        console.log(`[Proxy] Forwarding: ${req.method} ${req.originalUrl} -> ${proxyReq.protocol}//${proxyReq.host}${proxyReq.path}`);

        // 修复 Body 转发
        if (req.body && (req.method === 'POST' || req.method === 'PUT')) {
          const bodyData = JSON.stringify(req.body);
          proxyReq.setHeader('Content-Type', 'application/json');
          proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
          proxyReq.write(bodyData);
        }
      },
      proxyRes: (proxyRes: any) => {
        console.log(`[Proxy] Response from Java backend: ${proxyRes.statusCode}`);
      },
      error: (err: any, req: any, res: any) => {
        console.error("[Proxy Error]", err.message);
        if (!res.headersSent) {
          res.status(503).json({
            success: false,
            errMessage: "Java 后端连接失败",
            error: err.message
          });
        }
      }
    }
  }));

  // 3. 兜底 API 404
  app.all("/api/*", (req, res) => {
    res.status(404).json({
      success: false,
      errMessage: `API route not found: ${req.method} ${req.originalUrl}`
    });
  });

  // Vite 开发/生产环境配置
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

  // 4. 错误处理中间件
  app.use((err: any, req: any, res: any, next: any) => {
    console.error("[Server Error]", err);
    if (req.path.startsWith('/api')) {
      return res.status(err.status || 500).json({
        success: false,
        errMessage: err.message || "Internal Server Error"
      });
    }
    next(err);
  });

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

startServer();