import React, { useState, useEffect } from 'react';
import { Table, Button, Tag, Modal, Form, Input, Select, Space, App, Popconfirm, Switch, Card } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';
import Notification from './Notification';

interface ProcessTemplate {
  id: number;
  code: string;
  name: string;
  description: string;
  formSchema: string;
  status: number;
  statusName: string;
  version: number;
  nodeCount: number;
  createdTime: number;
  nodes: NodeTemplate[];
}

interface NodeTemplate {
  id: number;
  nodeOrder: number;
  nodeName: string;
  nodeType: number;
  nodeTypeName: string;
  approverType: number;
  approverTypeName: string;
  approverValue: string;
  opinionRequired: number;
}

export default function ProcessTemplateTable() {
  const { message } = App.useApp();
  const { hasPermission } = usePermission();
  const [templates, setTemplates] = useState<ProcessTemplate[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useState({
    name: '',
    status: null as number | null,
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalCount, setTotalCount] = useState(0);

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [formModalType, setFormModalType] = useState<'add' | 'edit'>('add');
  const [form] = Form.useForm();
  const [nodes, setNodes] = useState<NodeTemplate[]>([]);
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info'; key: number } | null>(null);

  const fetchTemplates = async () => {
    setLoading(true);
    try {
      const query = {
        pageIndex: currentPage,
        pageSize: pageSize,
        name: searchParams.name || undefined,
        status: searchParams.status,
      };

      const response = await fetch('/api/workflow/process-templates/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(query),
      });
      const data = await response.json();
      if (data.success && data.data) {
        setTemplates(data.data);
        setTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('获取流程模板列表失败:', error);
      setNotification({ message: '获取流程模板列表失败', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTemplates();
  }, [currentPage, pageSize]);

  const handleSearch = () => {
    setCurrentPage(1);
    fetchTemplates();
  };

  const handleAddTemplate = () => {
    setFormModalType('add');
    form.resetFields();
    setNodes([]);
    setFormModalOpen(true);
  };

  const handleEditTemplate = async (template: ProcessTemplate) => {
    setFormModalType('edit');
    setLoading(true);
    try {
      const response = await fetch(`/api/workflow/process-templates/detail?id=${template.id}`);
      const data = await response.json();
      if (data.success && data.data) {
        const detail = data.data;
        form.setFieldsValue({
          id: detail.id,
          code: detail.code,
          name: detail.name,
          description: detail.description,
          formSchema: detail.formSchema,
        });
        setNodes(detail.nodes || []);
        setFormModalOpen(true);
      }
    } catch (error) {
      setNotification({ message: '获取流程模板详情失败', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  const handleSaveTemplate = async () => {
    try {
      const values = await form.validateFields();
      const formData = {
        ...values,
        nodes: nodes.map((n, index) => ({
          ...n,
          nodeOrder: index + 1,
        })),
      };

      const url = formModalType === 'edit' 
        ? '/api/workflow/process-templates/update' 
        : '/api/workflow/process-templates';
      
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      const data = await response.json();
      if (data.success) {
        setFormModalOpen(false);
        fetchTemplates();
        setNotification({ 
          message: formModalType === 'edit' ? '流程模板更新成功' : '流程模板创建成功', 
          type: 'success', 
          key: Date.now() 
        });
      } else {
        setNotification({ message: data.errMessage || '保存失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      setNotification({ message: '保存失败，请重试', type: 'error', key: Date.now() });
    }
  };

  const handleUpdateStatus = async (id: number, status: number) => {
    try {
      const response = await fetch('/api/workflow/process-templates/update-status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id, status }),
      });
      const data = await response.json();
      if (data.success) {
        fetchTemplates();
        setNotification({ message: status === 1 ? '流程模板已启用' : '流程模板已停用', type: 'success', key: Date.now() });
      } else {
        setNotification({ message: data.errMessage || '操作失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      setNotification({ message: '操作失败，请重试', type: 'error', key: Date.now() });
    }
  };

  const handleAddNode = () => {
    const newNode: NodeTemplate = {
      id: 0,
      nodeOrder: nodes.length + 1,
      nodeName: '',
      nodeType: 1,
      nodeTypeName: '普通审批',
      approverType: 1,
      approverTypeName: '角色',
      approverValue: '[]',
      opinionRequired: 0,
    };
    setNodes([...nodes, newNode]);
  };

  const handleRemoveNode = (index: number) => {
    setNodes(nodes.filter((_, i) => i !== index));
  };

  const handleNodeChange = (index: number, field: string, value: any) => {
    const newNodes = [...nodes];
    (newNodes[index] as any)[field] = value;
    if (field === 'nodeType') {
      newNodes[index].nodeTypeName = value === 1 ? '普通审批' : '会签';
    }
    if (field === 'approverType') {
      newNodes[index].approverTypeName = value === 1 ? '角色' : value === 2 ? '用户' : '发起人上级';
    }
    setNodes(newNodes);
  };

  const columns: ColumnsType<ProcessTemplate> = [
    {
      title: '流程编码',
      dataIndex: 'code',
      key: 'code',
      width: 120,
    },
    {
      title: '流程名称',
      dataIndex: 'name',
      key: 'name',
      width: 150,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '节点数',
      dataIndex: 'nodeCount',
      key: 'nodeCount',
      width: 80,
      align: 'center',
    },
    {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 70,
      align: 'center',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      align: 'center',
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'default'}>
          {status === 1 ? '启用' : '停用'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 170,
      render: (val: number) => formatDateTime(val),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: unknown, record: ProcessTemplate) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditTemplate(record)}>
            编辑
          </Button>
          <Switch
            checked={record.status === 1}
            onChange={(checked) => handleUpdateStatus(record.id, checked ? 1 : 0)}
            checkedChildren="启用"
            unCheckedChildren="停用"
            size="small"
          />
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <div>
          <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>流程模板管理</h2>
          <p style={{ color: '#64748b', marginTop: 4 }}>配置审批流程模板，定义审批节点和审批人。</p>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAddTemplate}>
          新增流程模板
        </Button>
      </div>

      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
        <Input
          placeholder="搜索流程名称..."
          value={searchParams.name}
          onChange={(e) => setSearchParams((prev) => ({ ...prev, name: e.target.value }))}
          onPressEnter={handleSearch}
          allowClear
          style={{ width: 200 }}
        />
        <Select
          value={searchParams.status}
          onChange={(val) => setSearchParams((prev) => ({ ...prev, status: val }))}
          placeholder="状态"
          allowClear
          style={{ width: 120 }}
          options={[
            { value: 1, label: '启用' },
            { value: 0, label: '停用' },
          ]}
        />
        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
          查询
        </Button>
      </div>

      <Table<ProcessTemplate>
        columns={columns}
        dataSource={templates}
        rowKey="id"
        loading={loading}
        size="small"
        pagination={{
          current: currentPage,
          pageSize: pageSize,
          total: totalCount,
          showTotal: (total) => `共 ${total} 条`,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '50', '100'],
          size: 'default',
          onChange: (page, size) => {
            setCurrentPage(page);
            setPageSize(size);
          },
        }}
      />

      <Modal
        title={formModalType === 'add' ? '新增流程模板' : '编辑流程模板'}
        open={formModalOpen}
        onOk={handleSaveTemplate}
        onCancel={() => setFormModalOpen(false)}
        okText="保存"
        cancelText="取消"
        width={900}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" preserve={false}>
          {formModalType === 'edit' && (
            <Form.Item name="id" hidden>
              <Input />
            </Form.Item>
          )}
          <div style={{ display: 'flex', gap: 16 }}>
            <Form.Item
              name="code"
              label="流程编码"
              rules={[{ required: true, message: '请输入流程编码' }]}
              style={{ flex: 1 }}
            >
              <Input placeholder="如：LEAVE_APPLY" disabled={formModalType === 'edit'} />
            </Form.Item>
            <Form.Item
              name="name"
              label="流程名称"
              rules={[{ required: true, message: '请输入流程名称' }]}
              style={{ flex: 1 }}
            >
              <Input placeholder="如：请假申请" />
            </Form.Item>
          </div>
          <Form.Item name="description" label="流程描述">
            <Input.TextArea rows={2} placeholder="请输入流程描述" />
          </Form.Item>
          <Form.Item name="formSchema" label="表单模板（JSON Schema）">
            <Input.TextArea rows={4} placeholder="请输入表单模板JSON" />
          </Form.Item>

          <Card 
            title="审批节点配置" 
            size="small" 
            style={{ marginTop: 16 }}
            extra={<Button type="link" size="small" icon={<PlusOutlined />} onClick={handleAddNode}>添加节点</Button>}
          >
            {nodes.length === 0 ? (
              <div style={{ color: '#999', textAlign: 'center', padding: 20 }}>暂无审批节点，请点击"添加节点"按钮</div>
            ) : (
              nodes.map((node, index) => (
                <Card key={index} size="small" style={{ marginBottom: 8 }} title={`节点 ${index + 1}`}>
                  <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                    <Form.Item label="节点名称" style={{ marginBottom: 0, width: 150 }}>
                      <Input 
                        value={node.nodeName} 
                        onChange={(e) => handleNodeChange(index, 'nodeName', e.target.value)} 
                        placeholder="节点名称"
                      />
                    </Form.Item>
                    <Form.Item label="节点类型" style={{ marginBottom: 0, width: 120 }}>
                      <Select 
                        value={node.nodeType} 
                        onChange={(val) => handleNodeChange(index, 'nodeType', val)}
                        options={[
                          { value: 1, label: '普通审批' },
                          { value: 2, label: '会签' },
                        ]}
                      />
                    </Form.Item>
                    <Form.Item label="审批人类型" style={{ marginBottom: 0, width: 120 }}>
                      <Select 
                        value={node.approverType} 
                        onChange={(val) => handleNodeChange(index, 'approverType', val)}
                        options={[
                          { value: 1, label: '角色' },
                          { value: 2, label: '用户' },
                          { value: 3, label: '发起人上级' },
                        ]}
                      />
                    </Form.Item>
                    {node.approverType !== 3 && (
                      <Form.Item label="审批人ID（JSON数组）" style={{ marginBottom: 0, flex: 1 }}>
                        <Input 
                          value={node.approverValue} 
                          onChange={(e) => handleNodeChange(index, 'approverValue', e.target.value)} 
                          placeholder='如：[1, 2, 3]'
                        />
                      </Form.Item>
                    )}
                    <Form.Item label="意见必填" style={{ marginBottom: 0, width: 100 }}>
                      <Select 
                        value={node.opinionRequired} 
                        onChange={(val) => handleNodeChange(index, 'opinionRequired', val)}
                        options={[
                          { value: 0, label: '否' },
                          { value: 1, label: '是' },
                        ]}
                      />
                    </Form.Item>
                    <Button type="link" danger onClick={() => handleRemoveNode(index)} style={{ marginTop: 30 }}>
                      删除
                    </Button>
                  </div>
                </Card>
              ))
            )}
          </Card>
        </Form>
      </Modal>

      {notification && (
        <Notification key={notification.key} message={notification.message} type={notification.type} />
      )}
    </div>
  );
}
