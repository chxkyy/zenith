/** 后端标准响应信封 */
export interface ApiResponse<T = unknown> {
  success: boolean;
  data?: T;
  errMessage?: string;
}

/** 分页请求参数 */
export interface PageQuery {
  pageIndex: number;
  pageSize: number;
  keyword?: string;
  [key: string]: unknown;
}

/** 分页响应 */
export interface PaginatedResponse<T> {
  data: T[];
  totalCount: number;
}

/** 所有实体共有的审计字段 */
export interface BaseEntity {
  id: number;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

/** 带状态开关的实体 */
export interface StatusEntity extends BaseEntity {
  status: number;
}
