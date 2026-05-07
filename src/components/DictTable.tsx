import React, { useState, useEffect, useRef } from 'react';
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

  const [dictTypes, setDictTypes] = useState<DictType[]>([]);
  const [selectedDictType, setSelectedDictType] = useState<DictType | null>(null);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(5);
  const [totalCount, setTotalCount] = useState(0);

  const [dictItems, setDictItems] = useState<DictItem[]>([]);
  const [itemsLoading, setItemsLoading] = useState(false);
  const [itemsCurrentPage, setItemsCurrentPage] = useState(1);
  const [itemsPageSize, setItemsPageSize] = useState(10);
  const [itemsTotalCount, setItemsTotalCount] = useState(0);

  const [searchText, setSearchText] = useState('');

  const [dictTypeModalOpen, setDictTypeModalOpen] = useState(false);
  const [dictTypeModalMode, setDictTypeModalMode] = useState<'add' | 'edit'>('add');
  const [editingDictType, setEditingDictType] = useState<DictType | null>(null);

  const [dictItemModalOpen, setDictItemModalOpen] = useState(false);
  const [dictItemModalMode, setDictItemModalMode] = useState<'add' | 'edit'>('add');
  const [editingDictItem, setEditingDictItem] = useState<DictItem | null>(null);

  const hasFetchedRef = useRef(false);

  const fetchDictTypes = async (page?: number, size?: number, keyword?: string) => {
    setLoading(true);
    try {
      const response = await fetch('/api/dicts/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          pageIndex: page ?? currentPage,
          pageSize: size ?? pageSize,
          keyword: keyword ?? searchText,
        }),
      });
      if (!response.ok) throw new Error('Failed to fetch dict types');
      const data = await response.json();
      if (data.success && data.data) {
        setDictTypes(data.data);
        setTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('获取字典类型列表失败:', error);
      message.error('获取字典类型列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchDictItems = async (type: string, page?: number, size?: number, keyword?: string) => {
    setItemsLoading(true);
    try {
      const response = await fetch('/api/dict/items/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          type,
          pageIndex: page ?? itemsCurrentPage,
          pageSize: size ?? itemsPageSize,
          keyword: keyword ?? searchText,
        }),
      });
      if (!response.ok) throw new Error('Failed to fetch dict items');
      const data = await response.json();
      if (data.success && data.data) {
        setDictItems(data.data);
        setItemsTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('获取字典项列表失败:', error);
      message.error('获取字典项列表失败');
    } finally {
      setItemsLoading(false);
    }
  };

  useEffect(() => {
    if (hasFetchedRef.current) return;
    hasFetchedRef.current = true;
    fetchDictTypes();
  }, []);

  useEffect(() => {
    if (selectedDictType) {
      fetchDictItems(selectedDictType.type);
    } else {
      setDictItems([]);
      setItemsTotalCount(0);
    }
  }, [selectedDictType, itemsCurrentPage, itemsPageSize]);

  const handleSearch = () => {
    setCurrentPage(1);
    fetchDictTypes(1, pageSize, searchText);
    if (selectedDictType) {
      setItemsCurrentPage(1);
      fetchDictItems(selectedDictType.type, 1, itemsPageSize, searchText);
    }
  };

  const handleDictTypeSelect = (record: DictType) => {
    setSelectedDictType(record);
    setItemsCurrentPage(1);
    fetchDictItems(record.type, 1, itemsPageSize);
  };

  const openDictTypeModal = (mode: 'add' | 'edit', record?: DictType) => {
    setDictTypeModalMode(mode);
    setEditingDictType(record ?? null);
    if (mode === 'edit' && record) {
      dictTypeForm.setFieldsValue({
        name: record.name,
        type: record.type,
        status: record.status,
        remark: record.remark,
      });
    } else {
      dictTypeForm.resetFields();
      dictTypeForm.setFieldsValue({ status: 1 });
    }
    setDictTypeModalOpen(true);
  };

  const handleDictTypeSave = async () => {
    try {
      const values = await dictTypeForm.validateFields();
      const url = dictTypeModalMode === 'add' ? '/api/dicts' : '/api/dicts/update';
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...values,
          id: dictTypeModalMode === 'edit' && editingDictType ? editingDictType.id : null,
        }),
      });
      if (!response.ok) throw new Error('Failed to save dict type');
      const data = await response.json();
      if (data.success) {
        setDictTypeModalOpen(false);
        setCurrentPage(1);
        fetchDictTypes(1, pageSize, searchText);
        message.success(dictTypeModalMode === 'add' ? '字典类型新增成功' : '字典类型编辑成功');
      } else {
        message.error(data.errMessage || '保存失败');
      }
    } catch (error) {
      if (error instanceof Error) {
        console.error('保存字典类型失败:', error);
        message.error('保存失败，请重试');
      }
    }
  };

  const handleDictTypeDelete = async (record: DictType) => {
    try {
      const checkResponse = await fetch(
        `/api/dict/items/list?` + new URLSearchParams({ type: record.type })
      );
      if (!checkResponse.ok) throw new Error('Failed to check dict items');
      const checkData = await checkResponse.json();
      if (checkData.success && checkData.data && checkData.data.length > 0) {
        message.error('该字典类型下存在字典项，请先删除所有字典项后再删除类型');
        return;
      }
    } catch (error) {
      console.error('检查字典项失败:', error);
      message.error('检查字典项失败，请重试');
      return;
    }

    try {
      const response = await fetch(
        `/api/dicts?` + new URLSearchParams({ id: record.id.toString() }),
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ id: record.id }),
        }
      );
      if (!response.ok) throw new Error('Failed to delete dict type');
      const data = await response.json();
      if (data.success) {
        setCurrentPage(1);
        fetchDictTypes(1, pageSize, searchText);
        if (selectedDictType && selectedDictType.id === record.id) {
          setSelectedDictType(null);
        }
        message.success('字典类型删除成功');
      } else {
        message.error(data.errMessage || '删除失败');
      }
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

  const openDictItemModal = (mode: 'add' | 'edit', record?: DictItem) => {
    setDictItemModalMode(mode);
    setEditingDictItem(record ?? null);
    if (mode === 'edit' && record) {
      dictItemForm.setFieldsValue({
        type: record.type,
        label: record.label,
        dictValue: record.dictValue,
        sort: record.sort,
        status: record.status,
        remark: record.remark,
      });
    } else {
      dictItemForm.resetFields();
      dictItemForm.setFieldsValue({
        type: selectedDictType?.type,
        sort: 1,
        status: 1,
      });
    }
    setDictItemModalOpen(true);
  };

  const handleDictItemSave = async () => {
    try {
      const values = await dictItemForm.validateFields();
      const url = dictItemModalMode === 'add' ? '/api/dict/items' : '/api/dict/items/update';
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...values,
          id: dictItemModalMode === 'edit' && editingDictItem ? editingDictItem.id : null,
        }),
      });
      if (!response.ok) throw new Error('Failed to save dict item');
      const data = await response.json();
      if (data.success) {
        setDictItemModalOpen(false);
        if (selectedDictType) {
          fetchDictItems(selectedDictType.type, itemsCurrentPage, itemsPageSize);
        }
        message.success(dictItemModalMode === 'add' ? '字典项新增成功' : '字典项编辑成功');
      } else {
        message.error(data.errMessage || '保存失败');
      }
    } catch (error) {
      if (error instanceof Error) {
        console.error('保存字典项失败:', error);
        message.error('保存失败，请重试');
      }
    }
  };

  const handleDictItemDelete = async (record: DictItem) => {
    try {
      const response = await fetch('/api/dict/items/delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: record.id }),
      });
      if (!response.ok) throw new Error('Failed to delete dict item');
      const data = await response.json();
      if (data.success) {
        if (selectedDictType) {
          fetchDictItems(selectedDictType.type, itemsCurrentPage, itemsPageSize);
        }
        message.success('字典项删除成功');
      } else {
        message.error(data.errMessage || '删除失败');
      }
    } catch (error) {
      console.error('删除字典项失败:', error);
      message.error('删除失败，请重试');
    }
  };

  const confirmDictItemDelete = (record: DictItem) => {
    modal.confirm({
      title: '确认删除',
      content: `您确定要删除字典项「${record.label}」吗？`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => handleDictItemDelete(record),
    });
  };

  const dictTypeColumns: ColumnsType<DictType> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: '类型名称',
      dataIndex: 'name',
      key: 'name',
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
      render: (type: string) => <code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{type}</code>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
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
    {
      title: '创建人',
      dataIndex: 'createUserName',
      key: 'createUserName',
      width: 90,
      render: (val: string) => val || '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 170,
      render: (val: string) => formatDateTime(val),
    },
    {
      title: '修改人',
      dataIndex: 'updateUserName',
      key: 'updateUserName',
      width: 90,
      render: (val: string) => val || '-',
    },
    {
      title: '修改时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 170,
      render: (val: string) => formatDateTime(val),
    },
    {
      title: '操作',
      key: 'action',
      width: 140,
      align: 'right' as const,
      render: (_: unknown, record: DictType) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openDictTypeModal('edit', record)}>
            编辑
          </Button>
          <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => confirmDictTypeDelete(record)}>
            删除
          </Button>
        </Space>
      ),
    },
  ];

  const dictItemColumns: ColumnsType<DictItem> = [
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => <code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{type}</code>,
    },
    {
      title: '标签',
      dataIndex: 'label',
      key: 'label',
      render: (label: string) => <span style={{ fontWeight: 600 }}>{label}</span>,
    },
    {
      title: '值',
      dataIndex: 'dictValue',
      key: 'dictValue',
      render: (val: string) => <code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{val}</code>,
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 70,
      align: 'center' as const,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
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
    {
      title: '创建人',
      dataIndex: 'createUserName',
      key: 'createUserName',
      width: 90,
      render: (val: string) => val || '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 170,
      render: (val: string) => formatDateTime(val),
    },
    {
      title: '修改人',
      dataIndex: 'updateUserName',
      key: 'updateUserName',
      width: 90,
      render: (val: string) => val || '-',
    },
    {
      title: '修改时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 170,
      render: (val: string) => formatDateTime(val),
    },
    {
      title: '操作',
      key: 'action',
      width: 140,
      align: 'right' as const,
      render: (_: unknown, record: DictItem) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openDictItemModal('edit', record)}>
            编辑
          </Button>
          <Popconfirm
            title={`确定要删除字典项「${record.label}」吗？`}
            onConfirm={() => handleDictItemDelete(record)}
            okText="确定"
            cancelText="取消"
            okButtonProps={{ danger: true }}
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <div>
          <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>字典管理</h2>
          <p style={{ color: '#64748b', marginTop: 4 }}>管理系统字典类型及字典项数据。</p>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => openDictTypeModal('add')}>
          新增字典类型
        </Button>
      </div>

      <div style={{ marginBottom: 16 }}>
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

      <Table<DictType>
        columns={dictTypeColumns}
        dataSource={dictTypes}
        rowKey="id"
        loading={loading}
        size="small"
        pagination={{
          current: currentPage,
          pageSize: pageSize,
          total: totalCount,
          showSizeChanger: true,
          pageSizeOptions: ['5', '10', '20', '50'],
          showTotal: (total) => `共 ${total} 条记录`,
          onChange: (page, size) => {
            setCurrentPage(page);
            setPageSize(size);
            fetchDictTypes(page, size, searchText);
          },
        }}
        onRow={(record) => ({
          onClick: () => handleDictTypeSelect(record),
          style: {
            cursor: 'pointer',
            background: selectedDictType?.id === record.id ? '#e6f4ff' : undefined,
          },
        })}
        title={() => (
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <BookOutlined style={{ color: '#4f46e5' }} />
            <span style={{ fontWeight: 600 }}>字典类型管理</span>
          </div>
        )}
      />

      {selectedDictType && (
        <div style={{ marginTop: 24 }}>
          <Table<DictItem>
            columns={dictItemColumns}
            dataSource={dictItems}
            rowKey="id"
            loading={itemsLoading}
            size="small"
            pagination={{
              current: itemsCurrentPage,
              pageSize: itemsPageSize,
              total: itemsTotalCount,
              showSizeChanger: true,
              pageSizeOptions: ['5', '10', '20', '50'],
              showTotal: (total) => `共 ${total} 条记录`,
              onChange: (page, size) => {
                setItemsCurrentPage(page);
                setItemsPageSize(size);
              },
            }}
            title={() => (
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <BookOutlined style={{ color: '#4f46e5' }} />
                  <span style={{ fontWeight: 600 }}>{selectedDictType.name} - 字典项管理</span>
                </div>
                <Button type="primary" size="small" icon={<PlusOutlined />} onClick={() => openDictItemModal('add')}>
                  新增字典项
                </Button>
              </div>
            )}
          />
        </div>
      )}

      <Modal
        title={dictTypeModalMode === 'add' ? '新增字典类型' : '编辑字典类型'}
        open={dictTypeModalOpen}
        onOk={handleDictTypeSave}
        onCancel={() => setDictTypeModalOpen(false)}
        okText="保存"
        cancelText="取消"
        destroyOnClose
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
            <Input placeholder="请输入编码" disabled={dictTypeModalMode === 'edit'} />
          </Form.Item>
          {dictTypeModalMode === 'edit' && (
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
        title={dictItemModalMode === 'add' ? '新增字典项' : '编辑字典项'}
        open={dictItemModalOpen}
        onOk={handleDictItemSave}
        onCancel={() => setDictItemModalOpen(false)}
        okText="保存"
        cancelText="取消"
        destroyOnClose
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
