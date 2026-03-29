import express from "express";
import path from "path";
import fs from "fs";
import { createServer as createViteServer } from "vite";
import { createProxyMiddleware } from "http-proxy-middleware";
import { spawn } from "child_process";

async function startServer() {
  const app = express();
  const PORT = 3000;

  app.use(express.json());

  // 请求日志中间件
  app.use((req, res, next) => {
    console.log(`[Server] Received ${req.method} ${req.path}`);
    if (req.path.startsWith('/api')) {
      console.log(`[API Request] ${req.method} ${req.path}`);
    }
    next();
  });

  // 1. 系统日志 API (非代理)
  const LOG_FILE = path.join(process.cwd(), "java_backend.log");
  app.get("/api/system/logs", (req, res) => {
    console.log("[API] Fetching system logs");
    if (fs.existsSync(LOG_FILE)) {
      const logs = fs.readFileSync(LOG_FILE, 'utf8');
      res.send(logs);
    } else {
      res.send("日志文件尚未生成...");
    }
  });

  // 2. 代理 API 请求到 Java 后端
  app.use("/api", createProxyMiddleware({
    target: "http://localhost:8080",
    changeOrigin: true,
    pathRewrite: {
      '^/api': '/api', 
    },
    on: {
      proxyReq: (proxyReq: any, req: any, res: any) => {
        console.log(`[Proxy] Forwarding ${req.method} ${req.url} to Java backend`);
        console.log(`[Proxy] Request body: ${JSON.stringify(req.body)}`);
        if (req.body && (req.method === 'POST' || req.method === 'PUT')) {
          const bodyData = JSON.stringify(req.body);
          proxyReq.setHeader('Content-Type', 'application/json');
          proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
          proxyReq.write(bodyData);
        }
      },
      proxyRes: (proxyRes: any, req: any, res: any) => {
        console.log(`[Proxy] Received response from Java backend: ${proxyRes.statusCode}`);
      },
      error: (err: any, req: any, res: any) => {
        console.error("[Proxy Error]", err.message);
        if (!res.headersSent) {
          res.status(503).json({ 
            success: false, 
            errMessage: "Java 后端正在启动或不可用，请稍候...",
            error: err.message 
          });
        }
      }
    }
  }));

  // 3. 兜底：如果 /api 请求没被处理，返回 404 JSON 而不是 index.html
  app.all("/api/*", (req, res) => {
    console.log(`[API Fallback] 404 for ${req.method} ${req.originalUrl}`);
    res.status(404).json({ 
      success: false, 
      errMessage: `API route not found: ${req.method} ${req.originalUrl}` 
    });
  });

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

  // Vite 中介软件
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

startServer();
