import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Table, Button, Tag, Input, Space, Popconfirm, App, Modal, Form, Select } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  BookOutlined,
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePaginatedQuery, useCrudModal, useCrudOperations, createAuditColumns } from '../lib/useCrudTable';
import { post, del, get } from '../lib/apiClient';

interface DictType {
  id: number;
  name: string;
  type: string;
  status: number;
  remark: string;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

interface DictItem {
  id: number;
  type: string;
  label: string;
  dictValue: string;
  sort: number;
  status: number;
  remark: string;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

export default function DictTable() {
  const { message, modal } = App.useApp();
  const [dictTypeForm] = Form.useForm();
  const [dictItemForm] = Form.useForm();

  const [selectedDictType, setSelectedDictType] = useState<DictType | null>(null);
  const [searchText, setSearchText] = useState('');

  // ─── Dual paginated queries ────────────────────────────────

  /** Left table: DictType list — auto-fetches on mount */
  const typeQuery = usePaginatedQuery<DictType>({
    apiUrl: '/api/dicts/page',
    autoFetch: true,
  });

  /** Right table: DictItem list — fetched manually when a type is selected */
  const itemQuery = usePaginatedQuery<DictItem>({
    apiUrl: '/api/dict/items/page',
    autoFetch: false,
  });

  // ─── Modal state via useCrudModal ──────────────────────────

  const typeModal = useCrudModal<DictType>({
    onOpenAdd: () => {
      dictTypeForm.resetFields();
      dictTypeForm.setFieldsValue({ status: 1 });
    },
    onOpenEdit: (record) => {
      dictTypeForm.setFieldsValue({
        name: record.name,
        type: record.type,
        status: record.status,
        remark: record.remark,
      });
    },
  });

  const itemModal = useCrudModal<DictItem>({
    onOpenAdd: () => {
      dictItemForm.resetFields();
      dictItemForm.setFieldsValue({
        type: selectedDictType?.type,
        sort: 1,
        status: 1,
      });
    },
    onOpenEdit: (record) => {
      dictItemForm.setFieldsValue({
        type: record.type,
        label: record.label,
        dictValue: record.dictValue,
        sort: record.sort,
        status: record.status,
        remark: record.remark,
      });
    },
  });

  // ─── ResizeObserver scrollY calculation ───────────────────

  const dictTypeTableRef = useRef<HTMLDivElement>(null);
  const dictItemTableRef = useRef<HTMLDivElement>(null);
  const [dictTypeScrollY, setDictTypeScrollY] = useState<number>(300);
  const [dictItemScrollY, setDictItemScrollY] = useState<number>(300);

  const calcScrollY = useCallback((containerRef: React.RefObject<HTMLDivElement | null>, setter: (val: number) => void) => {
    const container = containerRef.current;
    if (!container) return;
    const tableEl = container.querySelector('.ant-table');
    if (!tableEl) return;
    const headerHeight = container.querySelector('.ant-table-header')?.getBoundingClientRect().height ?? 39;
    const paginationHeight = container.querySelector('.ant-table-pagination')?.getBoundingClientRect().height ?? 56;
    const titleHeight = container.querySelector('.ant-table-title')?.getBoundingClientRect().height ?? 46;
    const scrollY = container.clientHeight - headerHeight - paginationHeight - titleHeight - 16;
    setter(Math.max(scrollY, 100));
  }, []);

  useEffect(() => {
    const container = dictTypeTableRef.current;
    if (!container) return;
    const observer = new ResizeObserver(() => calcScrollY(dictTypeTableRef, setDictTypeScrollY));
    observer.observe(container);
    return () => observer.disconnect();
  }, [calcScrollY]);

  useEffect(() => {
    const container = dictItemTableRef.current;
    if (!container) return;
    const observer = new ResizeObserver(() => calcScrollY(dictItemTableRef, setDictItemScrollY));
    observer.observe(container);
    return () => observer.disconnect();
  }, [calcScrollY]);

  useEffect(() => {
    if (!typeQuery.data.length) return;
    requestAnimationFrame(() => calcScrollY(dictTypeTableRef, setDictTypeScrollY));
  }, [typeQuery.data, calcScrollY]);

  useEffect(() => {
    if (!selectedDictType) return;
    requestAnimationFrame(() => calcScrollY(dictItemTableRef, setDictItemScrollY));
  }, [selectedDictType, calcScrollY]);

  // ─── Master-detail selection logic ─────────────────────────

  /** Auto-select the first dict type when data loads */
  useEffect(() => {
    if (typeQuery.data.length > 0 && !selectedDictType) {
      setSelectedDictType(typeQuery.data[0]);
    }
  }, [typeQuery.data, selectedDictType]);

  /** Fetch items when a different dict type is selected */
  useEffect(() => {
    if (selectedDictType) {
      itemQuery.refetchWithQuery({ type: selectedDictType.type });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedDictType]);

  // ─── Search ───────────────────────────────────────────────

  const handleSearch = () => {
    typeQuery.setKeyword(searchText);
    typeQuery.search();
    if (selectedDictType) {
      itemQuery.setKeyword(searchText);
      itemQuery.search();
    }
  };

  const handleDictTypeSelect = (record: DictType) => {
    setSelectedDictType(record);
    // itemQuery.refetchWithQuery will fire via the useEffect above
  };

  // ─── CRUD operations via hooks ─────────────────────────────

  const { save: saveType } = useCrudOperations<DictType>(
    {
      createUrl: '/api/dicts',
      updateUrl: '/api/dicts/update',
      beforeCreate: (values) => ({ ...values }),
      beforeUpdate: (values) => ({
        ...values,
        id: typeModal.editingRecord?.id,
      }),
      successMessages: { create: '字典类型新增成功', update: '字典类型编辑成功' },
    },
    { refresh: typeQuery.refresh },
  );

  const { save: saveItem, remove: handleDictItemDelete } = useCrudOperations<DictItem>(
    {
      createUrl: '/api/dict/items',
      updateUrl: '/api/dict/items/update',
      deleteUrl: '/api/dict/items/delete',
      beforeCreate: (values) => ({ ...values }),
      beforeUpdate: (values) => ({
        ...values,
        id: itemModal.editingRecord?.id,
      }),
      successMessages: { create: '字典项新增成功', update: '字典项编辑成功', delete: '字典项删除成功' },
    },
    { refresh: itemQuery.refresh },
  );

  // ─── DictType CRUD ────────────────────────────────────────

  const handleDictTypeSave = async () => {
    try {
      const values = await dictTypeForm.validateFields();
      await saveType(values, typeModal.modalMode, typeModal.editingRecord);
      typeModal.closeModal();
    } catch {
      // useCrudOperations already handles message.error
    }
  };

  const handleDictTypeDelete = async (record: DictType) => {
    try {
      const checkData = await get<{ data: DictItem[] }>('/api/dict/items/list', { type: record.type });
      if (checkData.data && checkData.data.length > 0) {
        message.error('该字典类型下存在字典项，请先删除所有字典项后再删除类型');
        return;
      }
    } catch (error) {
      console.error('检查字典项失败:', error);
      message.error('检查字典项失败，请重试');
      return;
    }

    try {
      await del('/api/dicts', { id: record.id });
      typeQuery.refresh();
      if (selectedDictType && selectedDictType.id === record.id) {
        setSelectedDictType(null);
      }
      message.success('字典类型删除成功');
    } catch (error) {
      console.error('删除字典类型失败:', error);
      message.error('删除失败，请重试');
    }
  };

  const confirmDictTypeDelete = (record: DictType) => {
    modal.confirm({
      title: '确认删除',
      content: `您确定要删除字典类型「${record.name}」吗？删除前请确保该字典类型下没有字典项。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => handleDictTypeDelete(record),
    });
  };

  // ─── DictItem CRUD ─────────────────────────────────────────

  const handleDictItemSave = async () => {
    try {
      const values = await dictItemForm.validateFields();
      await saveItem(values, itemModal.modalMode, itemModal.editingRecord);
      itemModal.closeModal();
    } catch {
      // useCrudOperations already handles message.error
    }
  };

  const confirmDictItemDelete = (record: DictItem) => {
    modal.confirm({
      title: '确认删除',
      content: `您确定要删除字典项「${record.label}」吗？`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => handleDictItemDelete(record.id),
    });
  };

  // ─── Column definitions ────────────────────────────────────

  const dictTypeColumns: ColumnsType<DictType> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 50,
    },
    {
      title: '类型名称',
      dataIndex: 'name',
      key: 'name',
      ellipsis: true,
      render: (name: string, record: DictType) => (
        <a onClick={() => handleDictTypeSelect(record)} style={{ fontWeight: 600 }}>
          {name}
        </a>
      ),
    },
    {
      title: '编码',
      dataIndex: 'type',
      key: 'type',
      ellipsis: true,
      render: (type: string) => <code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{type}</code>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 70,
      align: 'center' as const,
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'default'}>
          {status === 1 ? '正常' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      ellipsis: true,
    },
    ...createAuditColumns<DictType>(),
    {
      title: '操作',
      key: 'action',
      width: 70,
      align: 'center' as const,
      render: (_: unknown, record: DictType) => (
        <Space size={4}>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => typeModal.openEditModal(record)} />
          <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => confirmDictTypeDelete(record)} />
        </Space>
      ),
    },
  ];

  const dictItemColumns: ColumnsType<DictItem> = [
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      ellipsis: true,
      render: (type: string) => <code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{type}</code>,
    },
    {
      title: '标签',
      dataIndex: 'label',
      key: 'label',
      ellipsis: true,
      render: (label: string) => <span style={{ fontWeight: 600 }}>{label}</span>,
    },
    {
      title: '值',
      dataIndex: 'dictValue',
      key: 'dictValue',
      ellipsis: true,
      render: (val: string) => <code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{val}</code>,
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 60,
      align: 'center' as const,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 70,
      align: 'center' as const,
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'default'}>
          {status === 1 ? '正常' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      ellipsis: true,
    },
    ...createAuditColumns<DictItem>(),
    {
      title: '操作',
      key: 'action',
      width: 70,
      align: 'center' as const,
      render: (_: unknown, record: DictItem) => (
        <Space size={4}>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => itemModal.openEditModal(record)} />
          <Popconfirm
            title={`确定要删除字典项「${record.label}」吗？`}
            onConfirm={() => handleDictItemDelete(record.id)}
            okText="确定"
            cancelText="取消"
            okButtonProps={{ danger: true }}
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // ─── Render ───────────────────────────────────────────────

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      height: 'calc(100vh - 80px)',
      padding: 24,
      overflow: 'hidden',
      boxSizing: 'border-box',
    }}>
      <div style={{ flexShrink: 0, marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索字典名称或编码..."
          allowClear
          style={{ width: 300 }}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          onSearch={handleSearch}
          enterButton
        />
      </div>

      <div ref={dictTypeTableRef} style={{ flex: 1, overflow: 'hidden', minHeight: 0 }}>
        <Table<DictType>
          className="dict-table"
          columns={dictTypeColumns}
          dataSource={typeQuery.data}
          rowKey="id"
          loading={typeQuery.loading}
          size="small"
          tableLayout="fixed"
          scroll={{ y: dictTypeScrollY }}
          pagination={{
            current: typeQuery.currentPage,
            pageSize: typeQuery.pageSize,
            total: typeQuery.totalCount,
            showTotal: (total) => `共 ${total} 条`,
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '50', '100'],
            onChange: (page, size) => typeQuery.goToPage(page, size),
          }}
          onRow={(record) => ({
            onClick: () => handleDictTypeSelect(record),
            style: {
              cursor: 'pointer',
              background: selectedDictType?.id === record.id ? '#e6f4ff' : undefined,
            },
          })}
          title={() => (
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <BookOutlined style={{ color: '#4f46e5' }} />
                <span style={{ fontWeight: 600 }}>字典类型管理</span>
              </div>
              <Button type="primary" size="small" icon={<PlusOutlined />} onClick={() => typeModal.openAddModal()}>
                新增字典类型
              </Button>
            </div>
          )}
        />
      </div>

      <div ref={dictItemTableRef} style={{ flex: 1, overflow: 'hidden', minHeight: 0, marginTop: 16 }}>
        {selectedDictType ? (
          <Table<DictItem>
            className="dict-table"
            columns={dictItemColumns}
            dataSource={itemQuery.data}
            rowKey="id"
            loading={itemQuery.loading}
            size="small"
            tableLayout="fixed"
            scroll={{ y: dictItemScrollY }}
            pagination={{
              current: itemQuery.currentPage,
              pageSize: itemQuery.pageSize,
              total: itemQuery.totalCount,
              showTotal: (total) => `共 ${total} 条`,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50', '100'],
              onChange: (page, size) => itemQuery.goToPage(page, size),
            }}
            title={() => (
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <BookOutlined style={{ color: '#4f46e5' }} />
                  <span style={{ fontWeight: 600 }}>{selectedDictType.name} - 字典项管理</span>
                </div>
                <Button type="primary" size="small" icon={<PlusOutlined />} onClick={() => itemModal.openAddModal()}>
                  新增字典项
                </Button>
              </div>
            )}
          />
        ) : (
          <div style={{ textAlign: 'center', padding: 40, color: '#999', background: '#fafafa', borderRadius: 8, height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            请先选择一个字典类型查看字典项
          </div>
        )}
      </div>

      <Modal
        title={typeModal.modalMode === 'add' ? '新增字典类型' : '编辑字典类型'}
        open={typeModal.modalOpen}
        onOk={handleDictTypeSave}
        onCancel={() => typeModal.closeModal()}
        okText="保存"
        cancelText="取消"
        destroyOnHidden
      >
        <Form
          form={dictTypeForm}
          layout="vertical"
          style={{ marginTop: 16 }}
        >
          <Form.Item
            label="类型名称"
            name="name"
            rules={[{ required: true, message: '请输入类型名称' }]}
          >
            <Input placeholder="请输入类型名称" />
          </Form.Item>
          <Form.Item
            label="编码"
            name="type"
            rules={[{ required: true, message: '请输入编码' }]}
          >
            <Input placeholder="请输入编码" disabled={typeModal.modalMode === 'edit'} />
          </Form.Item>
          {typeModal.modalMode === 'edit' && (
            <p style={{ color: '#8c8c8c', fontSize: 12, marginTop: -12, marginBottom: 16 }}>编码创建后不可编辑</p>
          )}
          <Form.Item label="状态" name="status">
            <Select
              options={[
                { value: 1, label: '正常' },
                { value: 0, label: '禁用' },
              ]}
            />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={itemModal.modalMode === 'add' ? '新增字典项' : '编辑字典项'}
        open={itemModal.modalOpen}
        onOk={handleDictItemSave}
        onCancel={() => itemModal.closeModal()}
        okText="保存"
        cancelText="取消"
        destroyOnHidden
      >
        <Form
          form={dictItemForm}
          layout="vertical"
          style={{ marginTop: 16 }}
        >
          <Form.Item label="类型" name="type">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="标签"
            name="label"
            rules={[{ required: true, message: '请输入标签' }]}
          >
            <Input placeholder="请输入标签" />
          </Form.Item>
          <Form.Item
            label="值"
            name="dictValue"
            rules={[{ required: true, message: '请输入值' }]}
          >
            <Input placeholder="请输入值" />
          </Form.Item>
          <Form.Item label="排序" name="sort">
            <Input type="number" placeholder="请输入排序" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select
              options={[
                { value: 1, label: '正常' },
                { value: 0, label: '禁用' },
              ]}
            />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
