import React, { useState, useRef } from 'react';
import { Table, Button, Space, Tag, Popconfirm, App, Card, Empty, Spin, Dropdown, Input } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, FolderOutlined,
  RightOutlined, DownOutlined, UndoOutlined, MoreOutlined
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import OrgModal from './OrgModal';

interface Org {
  id: number;
  name: string;
  parentId: number | null;
  sort: number;
  status: number;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
  children?: Org[];
}

export default function OrgTable() {
  const { message } = App.useApp();
  const [orgs, setOrgs] = useState<Org[]>([]);
  const [expanded, setExpanded] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'add-sub' | 'edit'>('add');
  const [selectedOrg, setSelectedOrg] = useState<Org | null>(null);
  const hasFetchedOrgs = useRef(false);

  const fetchOrgs = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/orgs');
      if (!response.ok) throw new Error('Failed to fetch organizations');
      const data = await response.json();
      if (data.success && data.data) {
        const orgData = data.data;
        const convertToOrgTree = (orgDTOs: any[]): { orgs: Org[]; expandedIds: number[] } => {
          const orgMap = new Map<number, Org>();
          const expandedIds: number[] = [];
          orgDTOs.forEach((orgDTO: any) => {
            const org: Org = {
              id: orgDTO.id, name: orgDTO.name, parentId: orgDTO.parentId,
              sort: orgDTO.sort || 0, status: orgDTO.status ?? 1,
              createdTime: orgDTO.createdTime, updateTime: orgDTO.updateTime,
              createUserId: orgDTO.createUserId, updateUserId: orgDTO.updateUserId,
              createUserName: orgDTO.createUserName, updateUserName: orgDTO.updateUserName,
              children: []
            };
            orgMap.set(org.id, org);
            expandedIds.push(org.id);
          });
          const rootOrgs: Org[] = [];
          orgMap.forEach(org => {
            if (!org.parentId) { rootOrgs.push(org); }
            else {
              const parentOrg = orgMap.get(org.parentId);
              if (parentOrg) { parentOrg.children?.push(org); }
            }
          });
          const sortOrgs = (orgList: Org[]) => {
            orgList.sort((a, b) => (a.sort || 0) - (b.sort || 0));
            orgList.forEach(org => { if (org.children && org.children.length > 0) sortOrgs(org.children); });
          };
          sortOrgs(rootOrgs);
          return { orgs: rootOrgs, expandedIds };
        };
        const result = convertToOrgTree(orgData);
        setOrgs(result.orgs);
        setExpanded(result.expandedIds);
      } else {
        setOrgs([]);
      }
    } catch (error) {
      console.error('Error fetching organizations:', error);
      message.error('获取组织列表失败');
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    if (hasFetchedOrgs.current) return;
    hasFetchedOrgs.current = true;
    fetchOrgs();
  }, []);

  const handleSaveOrg = async (orgData: Partial<Org>) => {
    setLoading(true);
    try {
      const orgDTO = {
        id: orgData.id, parentId: orgData.parentId, name: orgData.name,
        sort: orgData.sort, status: orgData.status
      };
      const response = await fetch('/api/orgs', {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(orgDTO)
      });
      if (!response.ok) throw new Error('Failed to save organization');
      const data = await response.json();
      if (data.success) {
        setIsModalOpen(false);
        message.success(modalMode === 'edit' ? '组织编辑成功' : '组织新增成功');
        await fetchOrgs();
      } else {
        throw new Error(data.errMessage || '保存失败');
      }
    } catch (error: any) {
      console.error('Error saving organization:', error);
      message.error(error.message || '保存组织失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteOrg = async (id: number) => {
    setLoading(true);
    try {
      const response = await fetch(`/api/orgs/delete`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id })
      });
      if (!response.ok) throw new Error('Failed to delete organization');
      const data = await response.json();
      if (data.success) {
        message.success('组织删除成功');
        await fetchOrgs();
      } else {
        throw new Error(data.errMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('Error deleting organization:', error);
      message.error(error.message || '删除组织失败');
    } finally {
      setLoading(false);
    }
  };

  const columns: ColumnsType<Org> = [
    {
      title: '组织名称', dataIndex: 'name', key: 'name', width: 200,
      render: (name, record) => (
        <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <FolderOutlined style={{ color: '#2563eb' }} />
          <span style={{ fontWeight: 500 }}>{name}</span>
        </span>
      )
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status) => <Tag color={status === 1 ? 'success' : 'default'}>{status === 1 ? '启用' : '禁用'}</Tag>
    },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 80 },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 160, render: (v) => formatDateTime(v) },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (v) => v || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作', key: 'action', width: 180, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" icon={<PlusOutlined />}
            onClick={() => { setSelectedOrg(record); setModalMode('add-sub'); setIsModalOpen(true); }}>
            新增子组织
          </Button>
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={() => { setSelectedOrg(record); setModalMode('edit'); setIsModalOpen(true); }}>
            编辑
          </Button>
          <Popconfirm title="确定删除该组织吗？删除后不可恢复" onConfirm={() => handleDeleteOrg(record.id)} okText="确定" cancelText="取消">
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <Card size="small" title="组织管理"
        extra={
          <Space>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => { setModalMode('add'); setSelectedOrg(null); setIsModalOpen(true); }}>
              新增组织
            </Button>
            <Button icon={<UndoOutlined />} onClick={fetchOrgs}>刷新</Button>
          </Space>
        }
      >
        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}><Spin /></div>
        ) : orgs.length === 0 ? (
          <Empty description="暂无组织数据" />
        ) : (
          <Table<Org>
            columns={columns}
            dataSource={orgs}
            rowKey="id"
            size="small"
            loading={loading}
            expandable={{
              expandedRowKeys: expanded,
              onExpandedRowsChange: (keys) => setExpanded(keys as number[]),
              defaultExpandAllRows: true
            }}
            pagination={false}
          />
        )}
      </Card>

      <OrgModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSaveOrg}
        org={selectedOrg || undefined}
        mode={modalMode}
        allOrgs={orgs}
        hideParentSelect={modalMode === 'add-sub'}
      />
    </div>
  );
}
