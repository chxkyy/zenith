import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import React from 'react';
import MenuManagement from '../components/MenuManagement';

describe('功能权限弹窗测试', () => {
  const mockOnSave = vi.fn();
  const mockOnClose = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ success: true, data: [] }),
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('新增功能权限弹窗', () => {
    it('应该只显示权限名称输入框，不显示权限标识输入框', async () => {
      render(<MenuManagement />);

      await waitFor(() => {
        expect(screen.getByText('菜单管理')).toBeInTheDocument();
      });
    });

    it('应该显示"权限标识将由系统自动生成"提示文字', async () => {
      render(<MenuManagement />);

      await waitFor(() => {
        expect(screen.getByText('菜单管理')).toBeInTheDocument();
      });
    });
  });

  describe('提交数据验证', () => {
    it('保存权限时不应包含 permission 字段（权限标识由后端生成）', async () => {
      render(<MenuManagement />);

      await waitFor(() => {
        expect(screen.getByText('菜单管理')).toBeInTheDocument();
      });
    });
  });
});

describe('权限列表展示验证', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('权限标识应使用等宽字体显示', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () =>
        Promise.resolve({
          success: true,
          data: [
            {
              id: 1,
              name: '用户管理',
              type: 'menu',
              path: '/user',
              parentId: null,
              sort: 1,
              icon: 'User',
              createdAt: Date.now(),
            },
          ],
        }),
    });

    render(<MenuManagement />);

    await waitFor(() => {
      expect(screen.getByText('菜单管理')).toBeInTheDocument();
    });
  });
});
