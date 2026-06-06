import { useState, useEffect, useRef, useCallback } from 'react';
import { App } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { pageQuery, post, del } from './apiClient';
import type { PaginatedResponse } from './types';
import { formatDateTime } from './utils';

// ─── 类型定义 ──────────────────────────────────────────────

/** 分页查询参数（传给后端的） */
export interface CrudPageQuery {
  pageIndex: number;
  pageSize: number;
  keyword?: string;
  [key: string]: unknown;
}

/** usePaginatedQuery 配置 */
export interface UsePaginatedQueryConfig<T> {
  /** 分页列表 API 端点 */
  apiUrl: string;
  /** 构建查询参数，接收当前页码、每页条数、搜索关键词 */
  buildQuery?: (page: number, size: number, keyword: string) => Record<string, unknown>;
  /** 初始页码，默认 1 */
  initialPage?: number;
  /** 初始每页条数，默认 10 */
  initialPageSize?: number;
  /** 是否在挂载时自动加载，默认 true */
  autoFetch?: boolean;
  /** 数据转换函数（后端字段 → 前端字段） */
  transform?: (item: T) => T;
}

/** usePaginatedQuery 返回值 */
export interface UsePaginatedQueryReturn<T> {
  data: T[];
  loading: boolean;
  currentPage: number;
  pageSize: number;
  totalCount: number;
  keyword: string;
  setKeyword: (kw: string) => void;
  /** 跳到指定页并重新查询 */
  goToPage: (page: number, size?: number) => void;
  /** 搜索（重置到第 1 页） */
  search: () => void;
  /** 刷新当前页 */
  refresh: () => void;
  /** 手动设置额外查询参数并刷新 */
  refetchWithQuery: (extraQuery: Record<string, unknown>) => void;
}

/** useCrudOperations 配置 */
export interface UseCrudOperationsConfig<T> {
  /** 创建 API 端点 */
  createUrl?: string;
  /** 更新 API 端点 */
  updateUrl?: string;
  /** 删除 API 端点 */
  deleteUrl?: string;
  /** 创建前的数据转换（如添加额外字段） */
  beforeCreate?: (values: Record<string, unknown>) => Record<string, unknown>;
  /** 更新前的数据转换 */
  beforeUpdate?: (values: Record<string, unknown>, record: T | null) => Record<string, unknown>;
  /** 操作成功后的回调 */
  onSuccess?: (action: 'create' | 'update' | 'delete') => void;
  /** 成功提示消息模板 */
  successMessages?: {
    create?: string;
    update?: string;
    delete?: string;
  };
}

/** useCrudOperations 返回值 */
export interface UseCrudOperationsReturn<T> {
  /** 创建记录 */
  create: (values: Record<string, unknown>) => Promise<void>;
  /** 更新记录 */
  update: (values: Record<string, unknown>, record: T) => Promise<void>;
  /** 根据模式自动选择创建或更新 */
  save: (values: Record<string, unknown>, mode: 'add' | 'edit', record: T | null) => Promise<void>;
  /** 删除记录 */
  remove: (id: number) => Promise<void>;
  /** 操作中（用于按钮 loading） */
  submitting: boolean;
}

/** useCrudModal 配置 */
export interface UseCrudModalConfig<T> {
  /** 打开新增回调（可自定义初始化逻辑） */
  onOpenAdd?: () => void;
  /** 打开编辑回调（可预填表单） */
  onOpenEdit?: (record: T) => void;
}

/** useCrudModal 返回值 */
export interface UseCrudModalReturn<T> {
  modalOpen: boolean;
  modalMode: 'add' | 'edit';
  editingRecord: T | null;
  openAddModal: () => void;
  openEditModal: (record: T) => void;
  closeModal: () => void;
}

// ─── Hook 1: 分页查询 ───────────────────────────────────────

/**
 * 通用的分页列表查询 hook。
 *
 * 封装了：data / loading / 分页状态 / 搜索 / 刷新。
 * 绝大多数表格组件只需调用此 hook + 定义 columns 即可完成列表展示。
 */
export function usePaginatedQuery<T>(config: UsePaginatedQueryConfig<T>): UsePaginatedQueryReturn<T> {
  const { message } = App.useApp();
  const {
    apiUrl,
    buildQuery,
    initialPage = 1,
    initialPageSize = 10,
    autoFetch = true,
    transform,
  } = config;

  const [data, setData] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [totalCount, setTotalCount] = useState(0);
  const [keyword, setKeyword] = useState('');
  const hasFetched = useRef(false);
  const extraQueryRef = useRef<Record<string, unknown>>({});

  const fetchData = useCallback(
    async (page: number, size: number, kw: string) => {
      setLoading(true);
      try {
        const query = buildQuery
          ? buildQuery(page, size, kw)
          : { pageIndex: page, pageSize: size, ...(kw ? { keyword: kw } : {}) };

        const mergedQuery = { ...query, ...extraQueryRef.current };
        const result: PaginatedResponse<T> = await pageQuery<T>(apiUrl, mergedQuery);

        const items = transform ? result.data.map(transform) : result.data;
        setData(items);
        setTotalCount(result.totalCount || 0);
      } catch (err: unknown) {
        const msg = err instanceof Error ? err.message : '获取数据失败';
        message.error(msg);
      } finally {
        setLoading(false);
      }
    },
    [apiUrl, buildQuery, transform, message],
  );

  useEffect(() => {
    if (!autoFetch || hasFetched.current) return;
    hasFetched.current = true;
    fetchData(currentPage, pageSize, keyword);
    // 仅在 mount 时执行，依赖留空
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 页码/每页条数变化时重新查询
  useEffect(() => {
    if (!hasFetched.current) return;
    fetchData(currentPage, pageSize, keyword);
  }, [currentPage, pageSize]); // eslint-disable-line react-hooks/exhaustive-deps

  const goToPage = useCallback(
    (page: number, size?: number) => {
      setCurrentPage(page);
      if (size !== undefined && size !== pageSize) setPageSize(size);
    },
    [pageSize],
  );

  const search = useCallback(() => {
    setCurrentPage(1);
    fetchData(1, pageSize, keyword);
  }, [fetchData, pageSize, keyword]);

  const refresh = useCallback(() => {
    fetchData(currentPage, pageSize, keyword);
  }, [fetchData, currentPage, pageSize, keyword]);

  const refetchWithQuery = useCallback((extra: Record<string, unknown>) => {
    extraQueryRef.current = extra;
    setCurrentPage(1);
    fetchData(1, pageSize, keyword);
  }, [fetchData, pageSize, keyword]);

  return {
    data,
    loading,
    currentPage,
    pageSize,
    totalCount,
    keyword,
    setKeyword,
    goToPage,
    search,
    refresh,
    refetchWithQuery,
  };
}

// ─── Hook 2: CRUD 操作 ───────────────────────────────────────

/**
 * 通用的增删改操作 hook。
 *
 * 封装了：create / update / delete 的完整生命周期，
 * 包括 loading 状态、错误处理、成功提示。
 */
export function useCrudOperations<T>(
  config: UseCrudOperationsConfig<T>,
  deps: { refresh: () => void },
): UseCrudOperationsReturn<T> {
  const { message } = App.useApp();
  const [submitting, setSubmitting] = useState(false);
  const {
    createUrl,
    updateUrl,
    deleteUrl,
    beforeCreate,
    beforeUpdate,
    onSuccess,
    successMessages,
  } = config;

  const create = async (values: Record<string, unknown>) => {
    if (!createUrl) return;
    setSubmitting(true);
    try {
      const payload = beforeCreate ? beforeCreate(values) : values;
      await post(createUrl, payload);
      message.success(successMessages?.create || '新增成功');
      onSuccess?.('create');
      deps.refresh();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : '新增失败';
      message.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const update = async (values: Record<string, unknown>, record: T) => {
    if (!updateUrl) return;
    setSubmitting(true);
    try {
      const payload = beforeUpdate ? beforeUpdate(values, record) : values;
      await post(updateUrl, payload);
      message.success(successMessages?.update || '编辑成功');
      onSuccess?.('update');
      deps.refresh();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : '更新失败';
      message.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const save = async (values: Record<string, unknown>, mode: 'add' | 'edit', record: T | null) => {
    if (mode === 'add') {
      await create(values);
    } else {
      await update(values, record!);
    }
  };

  const remove = async (id: number) => {
    if (!deleteUrl) return;
    setSubmitting(true);
    try {
      await del(deleteUrl, { id });
      message.success(successMessages?.delete || '删除成功');
      onSuccess?.('delete');
      deps.refresh();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : '删除失败';
      message.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return { create, update, save, remove, submitting };
}

// ─── Hook 3: 弹窗状态管理 ──────────────────────────────────

/**
 * 通用的模态框状态管理 hook。
 *
 * 管理 add/edit 模式的打开、关闭、当前编辑记录。
 */
export function useCrudModal<T>(_config?: UseCrudModalConfig<T>): UseCrudModalReturn<T> {
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [editingRecord, setEditingRecord] = useState<T | null>(null);

  const openAddModal = useCallback(() => {
    setModalMode('add');
    setEditingRecord(null);
    setModalOpen(true);
  }, []);

  const openEditModal = useCallback((record: T) => {
    setModalMode('edit');
    setEditingRecord(record);
    setModalOpen(true);
  }, []);

  const closeModal = useCallback(() => {
    setModalOpen(false);
    setEditingRecord(null);
  }, []);

  return {
    modalOpen,
    modalMode,
    editingRecord,
    openAddModal,
    openEditModal,
    closeModal,
  };
}

// ─── 审计列工厂 ────────────────────────────────────────────

export interface AuditColumnOptions {
  /** 创建时间字段名，默认 "createdTime" */
  createdTimeKey?: string;
}

/**
 * 生成标准的审计列定义（创建人、创建时间、修改人、修改时间）。
 *
 * 大多数 CRUD 表格都包含这四列，且 render 逻辑完全一致。
 * 使用此工厂可避免在 8+ 个组件中重复定义相同的列配置。
 *
 * @param options - 可选字段名覆盖
 */
export function createAuditColumns<T>(options?: AuditColumnOptions): ColumnsType<T> {
  const { createdTimeKey = 'createdTime' } = options || {};
  return [
    {
      title: '创建人',
      dataIndex: ('createUserName' as string) as unknown as keyof T,
      key: 'createUserName',
      width: 90,
      render: (v: unknown) => (v ? String(v) : '-'),
    },
    {
      title: '创建时间',
      dataIndex: (createdTimeKey as string) as unknown as keyof T,
      key: createdTimeKey,
      width: 160,
      render: (v: unknown) => formatDateTime(v as string | number | Date),
    },
    {
      title: '修改人',
      dataIndex: ('updateUserName' as string) as unknown as keyof T,
      key: 'updateUserName',
      width: 90,
      render: (v: unknown) => (v ? String(v) : '-'),
    },
    {
      title: '修改时间',
      dataIndex: ('updateTime' as string) as unknown as keyof T,
      key: 'updateTime',
      width: 160,
      render: (v: unknown) => formatDateTime(v as string | number | Date),
    },
  ];
}
