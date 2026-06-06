import type { ApiResponse, PaginatedResponse } from './types';

/** API 客户端错误，统一封装网络层和服务端业务错误 */
export class ApiError extends Error {
  constructor(
    public status: number,
    public statusText: string,
    public businessMessage?: string,
  ) {
    super(businessMessage || `API Error ${status}: ${statusText}`);
    this.name = 'ApiError';
  }
}

/** 请求配置（内部使用） */
interface RequestOptions extends Omit<RequestInit, 'body'> {
  body?: Record<string, unknown> | FormData | string;
}

/**
 * 核心请求函数 — 所有 API 调用的唯一出口。
 *
 * 统一处理：
 * - credentials 注入
 * - Content-Type 自动设置（FormData 除外）
 * - JSON 序列化
 * - 网络错误 / HTTP 错误 / 业务错误 三层异常转换
 * - 标准 { success, data, errMessage } 信封解包
 *
 * 替换此模块即可切换底层实现（axios、mock adapter 等）。
 */
async function request<T>(
  url: string,
  options: RequestOptions = {},
): Promise<T> {
  const { body, headers: customHeaders, ...rest } = options;
  const isFormData = body instanceof FormData;

  let response: Response;
  try {
    response = await fetch(url, {
      credentials: 'include',
      ...rest,
      headers: {
        ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
        ...customHeaders,
      },
      ...(body && !isFormData && typeof body === 'object'
        ? { body: JSON.stringify(body) }
        : { body: body as BodyInit }),
    });
  } catch (networkError) {
    throw new ApiError(0, 'Network Error', '网络连接失败，请检查网络');
  }

  // 文件下载场景：直接返回 Blob，不走 JSON 解析
  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('octet-stream') || contentType.includes('application/pdf')) {
    return response.blob() as unknown as Promise<T>;
  }

  // 空响应
  const text = await response.text();
  if (!text.trim()) {
    if (!response.ok) throw new ApiError(response.status, response.statusText);
    return undefined as unknown as T;
  }

  // JSON 解析
  let json: ApiResponse<T>;
  try {
    json = JSON.parse(text);
  } catch {
    throw new ApiError(response.status, response.statusText, '服务器返回了非 JSON 格式的数据');
  }

  // HTTP 层错误
  if (!response.ok) {
    throw new ApiError(response.status, response.statusText, json.errMessage || `HTTP ${response.status}`);
  }

  // 业务层错误
  if (!json.success) {
    throw new ApiError(response.status, 'Business Error', json.errMessage || '操作失败');
  }

  return json.data as T;
}

// ─── 公开方法 ─────────────────────────────────────────────

/** GET 请求，自动拼接 URL searchParams */
export function get<T>(url: string, params?: Record<string, string | number | undefined>): Promise<T> {
  const sp = new URLSearchParams();
  if (params) {
    for (const [k, v] of Object.entries(params)) {
      if (v !== undefined && v !== '') sp.append(k, String(v));
    }
  }
  const qs = sp.toString();
  return request<T>(qs ? `${url}?${qs}` : url);
}

/** POST 请求（JSON body） */
export function post<T>(url: string, body?: Record<string, unknown>): Promise<T> {
  return request<T>(url, { method: 'POST', body });
}

/** DELETE 语义的 POST 请求（后端统一用 POST） */
export function del<T>(url: string, body?: Record<string, unknown>): Promise<T> {
  return post<T>(url, body);
}

/** 分页列表查询 — POST + 返回 PaginatedResponse<T> */
export function pageQuery<T>(url: string, query: Record<string, unknown>): Promise<PaginatedResponse<T>> {
  return post<PaginatedResponse<T>>(url, query);
}

/** 文件上传（FormData） */
export function uploadFile<T>(url: string, formData: FormData): Promise<T> {
  return request<T>(url, { method: 'POST', body: formData });
}

/** 文件下载（返回 Blob） */
export async function downloadBlob(
  url: string,
  params?: Record<string, string>,
): Promise<Blob> {
  const sp = new URLSearchParams(params);
  const qs = sp.toString();
  const response = await fetch(qs ? `${url}?${qs}` : url, { credentials: 'include' });
  if (!response.ok) throw new ApiError(response.status, response.statusText, '下载失败');
  return response.blob();
}
