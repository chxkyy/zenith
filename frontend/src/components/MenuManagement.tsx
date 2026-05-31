import React, { useState, useRef } from 'react';
import { Table, Button, Input, Space, Tag, Popconfirm, App, Modal, Form, Select, Card, Tabs, Empty, Spin, Menu } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  SearchOutlined, PlusOutlined, MenuOutlined, AppstoreOutlined,
  RightOutlined, DownOutlined, DeleteOutlined, EditOutlined,
  EyeOutlined, EyeInvisibleOutlined, SettingOutlined, HolderOutlined,
  VerticalAlignTopOutlined
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent,
  DragStartEvent,
} from '@dnd-kit/core';
import {
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
  useSortable,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

interface Menu {
  id: number;
  name: string;
  type: 'dir' | 'menu';
  path: string;
  icon: string;
  parentId: number | null;
  parentName?: string;
  sort: number;
  status: number;
  createdTime: string;
  remark?: string;
  children?: Menu[];
}

interface MenuModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (menu: Partial<Menu>) => void;
  menu?: Menu;
  mode: 'add' | 'edit';
  allMenus: Menu[];
  hideParentSelect?: boolean;
}

const MenuModal: React.FC<MenuModalProps> = ({ isOpen, onClose, onSave, menu, mode, allMenus, hideParentSelect }) => {
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (isOpen) {
      if (mode === 'add') {
        form.setFieldsValue({
          name: '',
          type: 'menu',
          parentId: hideParentSelect && menu?.id ? menu.id : (menu?.parentId || null),
          path: '',
          icon: 'LayoutDashboard',
          sort: 1,
          status: 1,
          remark: ''
        });
      } else {
        form.setFieldsValue({
          name: menu?.name || '',
          type: menu?.type || 'menu',
          parentId: menu?.parentId || null,
          path: menu?.path || '',
          icon: menu?.icon || 'LayoutDashboard',
          sort: menu?.sort || 1,
          status: menu?.status || 1,
          remark: menu?.remark || ''
        });
      }
    } else {
      form.resetFields();
    }
  }, [isOpen, menu, mode, hideParentSelect]);

  const handleOk = () => {
    form.validateFields().then(values => {
      const menuData: Partial<Menu> = {
        ...(mode === 'edit' ? { id: menu?.id } : {}),
        ...values,
        ...(mode === 'add' && hideParentSelect && menu?.id ? { parentId: menu.id } : {})
      };
      onSave(menuData);
    });
  };

  return (
    <Modal
      title={mode === 'add' ? '新增菜单' : '编辑菜单'}
      open={isOpen}
      onCancel={onClose}
      onOk={handleOk}
      okText={mode === 'add' ? '保存菜单' : '更新菜单'}
      destroyOnHidden
      forceRender
    >
      <Form form={form} layout="vertical">
        <Form.Item label="菜单名称" name="name" rules={[
          { required: true, message: '请输入菜单名称' },
          { min: 1, max: 50, message: '菜单名称长度为1-50个字符' }
        ]}>
          <Input placeholder="请输入菜单名称" />
        </Form.Item>
        <Form.Item label="菜单类型" name="type" rules={[{ required: true }]}>
          <Select
            options={[
              { value: 'dir', label: '目录' },
              { value: 'menu', label: '菜单' },
            ]}
          />
        </Form.Item>
        {!hideParentSelect && (
          <Form.Item label="父菜单" name="parentId">
            <Select placeholder="顶级菜单" allowClear
              options={allMenus
                .filter(m => m.type === 'dir' && (!form.getFieldValue('id') || m.id !== form.getFieldValue('id')))
                .map(parentMenu => ({ value: parentMenu.id, label: parentMenu.name }))}
            />
          </Form.Item>
        )}
        <Form.Item label="路由路径" name="path" rules={[
          { required: true, message: '请输入路由路径' },
          {
            validator: (_, value) => {
              if (value && !value.startsWith('/')) {
                return Promise.reject(new Error('路由路径必须以 / 开头'));
              }
              if (value && !/^[a-zA-Z0-9/-]+$/.test(value)) {
                return Promise.reject(new Error('路由路径只能包含字母、数字、/、-'));
              }
              if (value && value.length > 100) {
                return Promise.reject(new Error('路由路径长度不能超过100个字符'));
              }
              return Promise.resolve();
            }
          }
        ]}>
          <Input placeholder="请输入路由路径，如 /dashboard" />
        </Form.Item>
        <Form.Item label="图标" name="icon">
          <Input placeholder="请输入图标名称，如 LayoutDashboard" />
        </Form.Item>
        <Form.Item label="排序" name="sort" rules={[
          { required: true, message: '请输入排序数字' },
          {
            validator: (_, value) => {
              if (value !== undefined && value !== null && (value < 0 || value > 9999)) {
                return Promise.reject(new Error('排序值范围为0-9999'));
              }
              return Promise.resolve();
            }
          }
        ]}>
          <Input type="number" min={0} max={9999} placeholder="请输入排序数字" />
        </Form.Item>
        <Form.Item label="状态" name="status">
          <Select
            options={[
              { value: 1, label: '启用' },
              { value: 0, label: '禁用' },
            ]}
          />
        </Form.Item>
        <Form.Item label="备注" name="remark">
          <Input.TextArea rows={3} placeholder="请输入备注信息" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

interface Permission {
  id: number;
  name: string;
  permission: string;
  type: 'FUNCTION' | 'FIELD';
  menuId: number;
  sort: number;
  status: number;
  createTime: string;
  createUserId: number | null;
  createUserName: string;
  updateTime: string;
  updateUserId: number | null;
  updateUserName?: string;
}

interface PermissionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (permission: Partial<Permission>) => void;
  permission?: Permission;
  mode: 'add' | 'edit';
  menuId: number;
  permissionType: 'FUNCTION' | 'FIELD';
}

const PermissionModal: React.FC<PermissionModalProps> = ({ isOpen, onClose, onSave, permission, mode, menuId, permissionType }) => {
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (isOpen) {
      if (permission) {
        form.setFieldsValue({
          name: permission.name || '',
          status: permission.status || 1
        });
      } else {
        form.setFieldsValue({ name: '', status: 1 });
      }
    } else {
      form.resetFields();
    }
  }, [isOpen, permission, menuId, permissionType]);

  const handleOk = () => {
    form.validateFields().then(values => {
      onSave({
        id: permission?.id,
        ...values,
        type: permissionType,
        menuId
      });
    });
  };

  return (
    <Modal
      title={`${mode === 'add' ? '新增' : '编辑'} ${permissionType === 'FUNCTION' ? '功能' : '字段'}权限`}
      open={isOpen}
      onCancel={onClose}
      onOk={handleOk}
      okText={mode === 'add' ? '保存' : '更新'}
      destroyOnHidden
      forceRender
    >
      <Form form={form} layout="vertical">
        <Form.Item label="权限名称" name="name" rules={[{ required: true, message: '请输入权限名称' }]}>
          <Input placeholder={`请输入${permissionType === 'FUNCTION' ? '功能' : '字段'}权限名称`} />
        </Form.Item>
        <Form.Item label="状态" name="status">
          <Select
            options={[
              { value: 1, label: '启用' },
              { value: 0, label: '禁用' },
            ]}
          />
        </Form.Item>
      </Form>
      <div style={{ fontSize: 12, color: '#999', marginTop: -12 }}>权限标识将由系统自动生成</div>
    </Modal>
  );
};

const PermissionManagement: React.FC<{ selectedMenu: Menu | null }> = ({ selectedMenu }) => {
  const { message } = App.useApp();
  const { hasPermission } = usePermission();
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'FUNCTION' | 'FIELD'>('FUNCTION');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedPermission, setSelectedPermission] = useState<Permission | null>(null);

  const fetchPermissions = async (menuId: number) => {
    setLoading(true);
    try {
      const response = await fetch(`/api/functions/list?menuId=${menuId}`);
      if (!response.ok) throw new Error('Failed to fetch permissions');
      const data = await response.json();
      if (data.success && data.data) {
        const permissionList = (data.data || []).map((item: any) => ({
          id: item.id,
          name: item.name,
          permission: item.permission || '',
          type: item.type === 'field' ? 'FIELD' : 'FUNCTION',
          menuId: item.menuId || menuId,
          sort: item.sort || 0,
          status: item.status ?? 1,
          createTime: formatDateTime(item.createdTime),
          createUserId: item.createUserId || null,
          createUserName: item.createUserName,
          updateTime: formatDateTime(item.updateTime),
          updateUserId: item.updateUserId || null,
          updateUserName: item.updateUserName
        }));
        setPermissions(permissionList);
      } else {
        setPermissions([]);
      }
    } catch (error) {
      console.error('Error fetching permissions:', error);
      setPermissions([]);
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    if (selectedMenu) {
      fetchPermissions(selectedMenu.id);
    } else {
      setPermissions([]);
    }
  }, [selectedMenu]);

  const handleSavePermission = async (permissionData: Partial<Permission>) => {
    setLoading(true);
    try {
      const functionDTO = {
        id: permissionData.id,
        menuId: selectedMenu?.id,
        name: permissionData.name,
        type: permissionData.type === 'FIELD' ? 'field' : 'button',
        sort: 0,
        status: permissionData.status
      };
      const response = await fetch('/api/functions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(functionDTO)
      });
      if (!response.ok) throw new Error('Failed to save permission');
      const data = await response.json();
      if (data.success) {
        message.success(modalMode === 'add' ? '权限新增成功' : '权限编辑成功');
        setIsModalOpen(false);
        if (selectedMenu) fetchPermissions(selectedMenu.id);
      } else {
        throw new Error(data.errMessage || '保存失败');
      }
    } catch (error: any) {
      console.error('Error saving permission:', error);
      message.error(error.message || '保存权限失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDeletePermission = async (id: number) => {
    setLoading(true);
    try {
      const response = await fetch('/api/functions/delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id })
      });
      if (!response.ok) throw new Error('Failed to delete permission');
      const data = await response.json();
      if (data.success) {
        message.success('权限删除成功');
        if (selectedMenu) fetchPermissions(selectedMenu.id);
      } else {
        throw new Error(data.errMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('Error deleting permission:', error);
      message.error(error.message || '删除权限失败');
    } finally {
      setLoading(false);
    }
  };

  const handleChangeStatus = async (id: number, currentStatus: number) => {
    const newStatus = currentStatus === 1 ? 0 : 1;
    setLoading(true);
    try {
      const response = await fetch('/api/functions/update', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id, status: newStatus })
      });
      if (!response.ok) throw new Error('Failed to update permission status');
      const data = await response.json();
      if (data.success) {
        message.success(`权限状态已切换为${newStatus === 1 ? '启用' : '禁用'}`);
        if (selectedMenu) fetchPermissions(selectedMenu.id);
      } else {
        throw new Error(data.errMessage || '状态更新失败');
      }
    } catch (error: any) {
      console.error('Error changing permission status:', error);
      message.error(error.message || '切换权限状态失败');
    } finally {
      setLoading(false);
    }
  };

  const filteredPermissions = permissions.filter(p => p.type === activeTab);

  const columns: ColumnsType<Permission> = [
    { title: '权限名称', dataIndex: 'name', key: 'name', width: 140 },
    { title: '权限标识', dataIndex: 'permission', key: 'permission', width: 160, render: (v) => <code>{v || '-'}</code> },
    {
      title: '类型', dataIndex: 'type', key: 'type', width: 100,
      render: (v) => <Tag color={v === 'FUNCTION' ? 'blue' : 'purple'}>{v === 'FUNCTION' ? '功能权限' : '字段权限'}</Tag>
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (v, record) => (
        <Tag color={v === 1 ? 'success' : 'default'} style={{ cursor: 'pointer' }} onClick={() => handleChangeStatus(record.id, record.status)}>
          {v === 1 ? '启用' : '禁用'}
        </Tag>
      )
    },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 160, render: (v) => v || '-' },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (v) => v || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 160, render: (v) => v || '-' },
    {
      title: '操作', key: 'action', width: 120,
      render: (_, record) => (
        <Space size="small">
          {hasPermission('core:permission:edit') && (
          <Button type="link" size="small" onClick={() => { setSelectedPermission(record); setModalMode('edit'); setIsModalOpen(true); }}>编辑</Button>
          )}
          {hasPermission('core:permission:delete') && (
          <Popconfirm title="删除后权限数据不可恢复，是否确认删除？" onConfirm={() => handleDeletePermission(record.id)} okText="确定" cancelText="取消">
            <Button type="link" size="small" danger>删除</Button>
          </Popconfirm>
          )}
        </Space>
      )
    }
  ];

  if (!selectedMenu) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
        <Empty description="请选择一个菜单以管理其权限" />
      </div>
    );
  }

  return (
    <div style={{ padding: 24, display: 'flex', flexDirection: 'column', gap: 24 }}>
      <div>
        <h3 style={{ fontSize: 18, fontWeight: 700, margin: 0 }}>{selectedMenu.name} - 权限管理</h3>
        <p style={{ color: '#64748b', marginTop: 4 }}>管理该菜单下的功能权限和字段权限</p>
      </div>

      <Card size="small">
        <Tabs
          activeKey={activeTab}
          onChange={(k) => setActiveTab(k as 'FUNCTION' | 'FIELD')}
          items={[
            { key: 'FUNCTION', label: '功能权限' },
            { key: 'FIELD', label: '字段权限' }
          ]}
          tabBarExtraContent={
            hasPermission('core:permission:add') ? (
            <Button type="primary" size="small" icon={<PlusOutlined />} onClick={() => { setModalMode('add'); setSelectedPermission(null); setIsModalOpen(true); }}>
              新增{activeTab === 'FUNCTION' ? '功能' : '字段'}权限
            </Button>
            ) : null
          }
        />
        <Table<Permission>
          columns={columns}
          dataSource={filteredPermissions}
          rowKey="id"
          size="small"
          loading={loading}
          pagination={false}
        />
      </Card>

      <PermissionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSavePermission}
        permission={selectedPermission || undefined}
        mode={modalMode}
        menuId={selectedMenu.id}
        permissionType={activeTab}
      />
    </div>
  );
};

interface SortableMenuItemProps {
  menu: Menu;
  level: number;
  isSelected: boolean;
  isExpanded: boolean;
  hasChildren: boolean;
  onSelect: (menu: Menu) => void;
  onToggleExpand: (id: number) => void;
  onRightClick: (e: React.MouseEvent, menu: Menu) => void;
}

const SortableMenuItem: React.FC<SortableMenuItemProps> = ({
  menu, level, isSelected, isExpanded, hasChildren, onSelect, onToggleExpand, onRightClick
}) => {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id: menu.id });
  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div
      ref={setNodeRef}
      style={{
        ...style,
        display: 'flex', alignItems: 'center', gap: 8, padding: 8, borderRadius: 8,
        cursor: 'pointer', backgroundColor: isSelected ? '#eff6ff' : 'transparent',
        color: isSelected ? '#2563eb' : undefined, paddingLeft: level * 20
      }}
      onClick={() => onSelect(menu)}
      onContextMenu={(e) => onRightClick(e, menu)}
    >
      <span {...attributes} {...listeners} style={{ cursor: 'grab', padding: 4 }} onClick={(e) => e.stopPropagation()}>
        <HolderOutlined style={{ color: '#94a3b8', fontSize: 14 }} />
      </span>
      {hasChildren ? (
        <span onClick={(e) => { e.stopPropagation(); onToggleExpand(menu.id); }} style={{ padding: 4, cursor: 'pointer' }}>
          {isExpanded ? <DownOutlined style={{ fontSize: 14 }} /> : <RightOutlined style={{ fontSize: 14 }} />}
        </span>
      ) : <span style={{ width: 22 }} />}
      {menu.type === 'dir' && <AppstoreOutlined style={{ color: '#2563eb', fontSize: 16 }} />}
      {menu.type === 'menu' && <MenuOutlined style={{ color: '#475569', fontSize: 16 }} />}
      <span style={{ fontWeight: level === 0 ? 600 : 400, fontSize: level === 0 ? 14 : 13 }}>{menu.name}</span>
    </div>
  );
};

export default function MenuManagement() {
  const { message } = App.useApp();
  const { hasPermission } = usePermission();
  const [menus, setMenus] = useState<Menu[]>([]);
  const [expanded, setExpanded] = useState<number[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedMenu, setSelectedMenu] = useState<Menu | null>(null);
  const [rightClickMenu, setRightClickMenu] = useState<{ x: number; y: number; menu: Menu } | null>(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [filteredMenus, setFilteredMenus] = useState<Menu[] | null>(null);
  const [loading, setLoading] = useState(true);
  const hasFetchedMenus = useRef(false);
  const [draggingId, setDraggingId] = useState<number | null>(null);
  const [deleteConfirmVisible, setDeleteConfirmVisible] = useState(false);
  const [menuToDelete, setMenuToDelete] = useState<Menu | null>(null);

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 5 } }),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates })
  );

  const toggleExpand = (id: number) => {
    setExpanded(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]);
  };

  const fetchMenus = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/menus');
      if (!response.ok) throw new Error('Failed to fetch menus');
      const data = await response.json();
      const menuData = data.success && data.data ? data.data : [];

      const convertToMenus = (menuDTOs: any[]): { menus: Menu[]; expandedIds: number[] } => {
        const menuMap = new Map<number, Menu>();
        const expandedIds: number[] = [];
        menuDTOs.forEach(menuDTO => {
          const menu: Menu = {
            id: menuDTO.id, name: menuDTO.name,
            type: menuDTO.type === 'menu' ? 'menu' : 'dir',
            path: menuDTO.path || '', icon: menuDTO.icon || 'LayoutDashboard',
            parentId: menuDTO.parentId, sort: menuDTO.sort || 0, status: 1,
            createdTime: formatDateTime(menuDTO.createdTime), remark: menuDTO.remark, children: []
          };
          menuMap.set(menu.id, menu);
          expandedIds.push(menu.id);
        });
        const rootMenus: Menu[] = [];
        menuMap.forEach(menu => {
          if (!menu.parentId) { rootMenus.push(menu); }
          else {
            const parentMenu = menuMap.get(menu.parentId);
            if (parentMenu) { parentMenu.children?.push(menu); menu.parentName = parentMenu.name; }
          }
        });
        const sortMenus = (menuList: Menu[]) => {
          menuList.sort((a, b) => (a.sort || 0) - (b.sort || 0));
          menuList.forEach(menu => { if (menu.children && menu.children.length > 0) sortMenus(menu.children); });
        };
        sortMenus(rootMenus);
        return { menus: rootMenus, expandedIds };
      };

      const result = convertToMenus(menuData);
      setMenus(result.menus);
      setExpanded(result.expandedIds);
    } catch (error) {
      console.error('Error fetching menus:', error);
      message.error('获取菜单列表失败');
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    if (hasFetchedMenus.current) return;
    hasFetchedMenus.current = true;
    fetchMenus();
  }, []);

  const handleSearch = () => {
    if (!searchKeyword.trim()) { setFilteredMenus(null); return; }
    const keyword = searchKeyword.trim().toLowerCase();
    const filterMenuTree = (menuList: Menu[]): Menu[] => {
      return menuList.reduce((result: Menu[], menu) => {
        const filteredChildren = menu.children?.length ? filterMenuTree(menu.children) : [];
        const isNameMatched = menu.name.toLowerCase().includes(keyword);
        if (isNameMatched || filteredChildren.length > 0) {
          result.push({ ...menu, children: isNameMatched ? menu.children : filteredChildren });
        }
        return result;
      }, []);
    };
    const result = filterMenuTree(menus);
    setFilteredMenus(result);
    if (result.length > 0) {
      const collectAllIds = (menuList: Menu[]): number[] => {
        return menuList.reduce((ids: number[], menu) => {
          ids.push(menu.id);
          if (menu.children?.length) ids.push(...collectAllIds(menu.children));
          return ids;
        }, []);
      };
      setExpanded(collectAllIds(result));
    }
  };

  const handleSaveMenu = async (menuData: Partial<Menu>) => {
    if (menuData.parentId && menuData.parentId > 0) {
      const parentMenu = findMenuById(menus, menuData.parentId);
      if (parentMenu) {
        const calculateDepth = (m: Menu): number => {
          let depth = 1;
          let current: Menu | null = m;
          while (current?.parentId) {
            depth++;
            current = findMenuById(menus, current.parentId);
          }
          return depth;
        };
        const parentDepth = calculateDepth(parentMenu);
        if (parentDepth >= 3) {
          message.error(`父菜单"${parentMenu.name}"已处于第${parentDepth}层，无法在其下创建子菜单（最大允许3层）`);
          return;
        }
      }
    }

    setLoading(true);
    try {
      const menuDTO = {
        id: menuData.id, parentId: menuData.parentId, name: menuData.name,
        path: menuData.path, component: menuData.type === 'menu' ? 'Layout' : '',
        icon: menuData.icon, sort: menuData.sort,
        type: menuData.type, remark: menuData.remark
      };
      
      const url = modalMode === 'edit' ? '/api/menus/update' : '/api/menus';
      const response = await fetch(url, {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(menuDTO)
      });
      if (!response.ok) throw new Error('Failed to save menu');
      const data = await response.json();
      if (data.success) {
        setIsModalOpen(false);
        setRightClickMenu(null);
        message.success(modalMode === 'add' ? '菜单新增成功' : '菜单编辑成功');
        await fetchMenus();
      } else {
        throw new Error(data.errMessage || '保存失败');
      }
    } catch (error: any) {
      console.error('Error saving menu:', error);
      message.error(error.message || '保存菜单失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteMenu = async (id: number) => {
    const menuToDelete = findMenuById(menus, id);
    if (!menuToDelete) {
      message.error('菜单不存在');
      return;
    }

    if (menuToDelete.children && menuToDelete.children.length > 0) {
      message.warning(`该菜单下有 ${menuToDelete.children.length} 个子菜单，请先删除所有子菜单`);
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`/api/menus/delete`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id })
      });
      if (!response.ok) throw new Error('Failed to delete menu');
      const data = await response.json();
      if (data.success) {
        setRightClickMenu(null);
        setSelectedMenu(null);
        message.success('菜单删除成功');
        await fetchMenus();
      } else {
        throw new Error(data.errMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('Error deleting menu:', error);
      message.error(error.message || '删除菜单失败');
    } finally {
      setLoading(false);
    }
  };

  const findMenuById = (menuList: Menu[], id: number): Menu | null => {
    for (const menu of menuList) {
      if (menu.id === id) return menu;
      if (menu.children) { const found = findMenuById(menu.children, id); if (found) return found; }
    }
    return null;
  };

  const getMaxChildDepth = (menu: Menu, allMenus: Menu[]): number => {
    if (!menu.children || menu.children.length === 0) return 0;
    let maxChildDepth = 0;
    for (const child of menu.children) {
      const childDepth = 1 + getMaxChildDepth(child, allMenus);
      maxChildDepth = Math.max(maxChildDepth, childDepth);
    }
    return maxChildDepth;
  };

  const handleChangeStatus = async (id: number, currentStatus: number) => {
    const newStatus = currentStatus === 1 ? 0 : 1;
    setLoading(true);
    try {
      const response = await fetch('/api/menus/toggle-status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id })
      });
      if (!response.ok) throw new Error('Failed to toggle status');
      const data = await response.json();
      if (data.success) {
        message.success(`菜单状态已切换为${newStatus === 1 ? '启用' : '禁用'}`);
        await fetchMenus();
      } else {
        throw new Error(data.errMessage || '状态切换失败');
      }
    } catch (error: any) {
      console.error('Error toggling menu status:', error);
      message.error(error.message || '切换菜单状态失败');
    } finally {
      setLoading(false);
    }
  };

  const handleRightClick = (e: React.MouseEvent, menu: Menu) => {
    e.preventDefault();
    setRightClickMenu({ x: e.clientX, y: e.clientY, menu });
  };

  const handleDragStart = (event: DragStartEvent) => { setDraggingId(event.active.id as number); };

  const handleDragEnd = async (event: DragEndEvent) => {
    setDraggingId(null);
    const { active, over } = event;
    if (!over || active.id === over.id) return;
    const draggedId = active.id as number;
    const targetId = over.id as number;

    const findMenuById = (menuList: Menu[], id: number): Menu | null => {
      for (const menu of menuList) {
        if (menu.id === id) return menu;
        if (menu.children) { const found = findMenuById(menu.children, id); if (found) return found; }
      }
      return null;
    };
    const findParentId = (menuList: Menu[], targetId: number, excludeId: number): number | null => {
      for (const menu of menuList) {
        if (menu.id === excludeId) continue;
        if (menu.children && menu.children.some(child => child.id === targetId)) return menu.id;
        if (menu.children) { const found = findParentId(menu.children, targetId, excludeId); if (found !== null) return found; }
      }
      return null;
    };
    const getSiblingIndex = (menuList: Menu[], parentId: number | null, targetId: number): number => {
      let siblings: Menu[] = [];
      const collectSiblings = (list: Menu[]) => {
        for (const menu of list) {
          if ((parentId === null && !menu.parentId) || (parentId !== null && menu.parentId === parentId)) siblings.push(menu);
          if (menu.children) collectSiblings(menu.children);
        }
      };
      collectSiblings(menuList);
      return siblings.findIndex(m => m.id === targetId);
    };

    const draggedMenu = findMenuById(menus, draggedId);
    const targetMenu = findMenuById(menus, targetId);
    if (!draggedMenu || !targetMenu) return;
    const draggedOriginalParentId = draggedMenu.parentId;
    const targetParentId = findParentId(menus, targetId, draggedId);

    const isDescendantOf = (ancestor: Menu | null, descendant: Menu): boolean => {
      if (!ancestor) return false;
      if (ancestor.id === descendant.id) return true;
      if (ancestor.children) {
        for (const child of ancestor.children) {
          if (isDescendantOf(child, descendant)) return true;
        }
      }
      return false;
    };

    const getDepth = (menu: Menu): number => {
      let depth = 0;
      let current: Menu | null = menu;
      while (current) {
        depth++;
        current = current.parentId ? findMenuById(menus, current.parentId) : null;
      }
      return depth;
    };

    if (draggedOriginalParentId !== targetParentId) {
      if (isDescendantOf(draggedMenu, targetMenu)) {
        message.error('不能将父菜单移动到其子菜单下');
        return;
      }

      const newTargetDepth = getDepth(targetMenu);
      const draggedChildMaxDepth = getMaxChildDepth(draggedMenu, menus);
      if (newTargetDepth + 1 + draggedChildMaxDepth > 3) {
        message.error('菜单层级不能超过3层，无法完成此操作');
        return;
      }
    }

    setLoading(true);
    try {
      if (draggedOriginalParentId !== targetParentId) {
        const response = await fetch('/api/menus/update-parent', {
          method: 'POST', headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ id: draggedId, newParentId: targetParentId })
        });
        const data = await response.json();
        if (!data.success) throw new Error(data.errMessage || '移动菜单失败');
      } else {
        const siblingIndex = getSiblingIndex(menus, targetParentId, targetId);
        const response = await fetch('/api/menus/reorder', {
          method: 'POST', headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ id: draggedId, targetIndex: siblingIndex })
        });
        const data = await response.json();
        if (!data.success) throw new Error(data.errMessage || '调整顺序失败');
      }
      message.success('菜单排序已更新');
      await fetchMenus();
    } catch (error: any) {
      console.error('Error during drag:', error);
      message.error(error.message || '操作失败');
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    const handler = () => setRightClickMenu(null);
    document.addEventListener('click', handler);
    return () => document.removeEventListener('click', handler);
  }, []);

  const displayMenus = filteredMenus !== null ? filteredMenus : menus;

  const renderMenuTree = (menuList: Menu[], level = 0) => {
    return (
      <SortableContext items={menuList.map(m => m.id)} strategy={verticalListSortingStrategy}>
        {menuList.map(menu => (
          <React.Fragment key={menu.id}>
            <SortableMenuItem
              menu={menu} level={level} isSelected={selectedMenu?.id === menu.id}
              isExpanded={expanded.includes(menu.id)}
              hasChildren={!!(menu.children && menu.children.length > 0)}
              onSelect={setSelectedMenu} onToggleExpand={toggleExpand} onRightClick={handleRightClick}
            />
            {menu.children && menu.children.length > 0 && expanded.includes(menu.id) && (
              <div style={{ marginLeft: 16, borderLeft: '2px solid #e2e8f0', paddingLeft: 8, marginTop: 4 }}>
                {renderMenuTree(menu.children, level + 1)}
              </div>
            )}
          </React.Fragment>
        ))}
      </SortableContext>
    );
  };

  const rightClickMenuItems = rightClickMenu ? [
    ...(rightClickMenu.menu.type === 'dir' && hasPermission('sys:menu:add') ? [{
      key: 'add-sub', label: '新增子菜单', icon: <PlusOutlined />,
      onClick: () => { setSelectedMenu(rightClickMenu.menu); setModalMode('add'); setIsModalOpen(true); setRightClickMenu(null); }
    }] : []),
    ...(hasPermission('sys:menu:edit') ? [{
      key: 'edit', label: '编辑', icon: <EditOutlined />,
      onClick: () => { setSelectedMenu(rightClickMenu.menu); setModalMode('edit'); setIsModalOpen(true); setRightClickMenu(null); }
    }] : []),
    {
      key: 'status', label: rightClickMenu.menu.status === 1 ? '禁用' : '启用',
      icon: rightClickMenu.menu.status === 1 ? <EyeInvisibleOutlined /> : <EyeOutlined />,
      onClick: () => { handleChangeStatus(rightClickMenu.menu.id, rightClickMenu.menu.status); setRightClickMenu(null); }
    },
    { type: 'divider' as const },
    ...(hasPermission('sys:menu:delete') ? [{
      key: 'delete', label: '删除', icon: <DeleteOutlined />, danger: true,
      onClick: () => { setMenuToDelete(rightClickMenu.menu); setDeleteConfirmVisible(true); setRightClickMenu(null); }
    }] : [])
  ] : [];

  return (
    <div style={{ display: 'flex', height: '100vh', backgroundColor: '#f8fafc' }}>
      {/* 左侧菜单树 */}
      <div style={{ width: 320, backgroundColor: '#fff', borderRight: '1px solid #e2e8f0', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: 16, borderBottom: '1px solid #e2e8f0' }}>
          <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>菜单管理</h2>
          <p style={{ fontSize: 14, color: '#64748b', marginTop: 4 }}>配置系统左侧导航菜单</p>
        </div>

        <div style={{ padding: 16, borderBottom: '1px solid #e2e8f0' }}>
          <Input prefix={<SearchOutlined />} placeholder="搜索菜单..." value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onPressEnter={handleSearch} allowClear />
        </div>

        <div style={{ padding: 16, borderBottom: '1px solid #e2e8f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Button type="primary" icon={<PlusOutlined />} disabled={selectedMenu?.type !== 'dir'} onClick={() => { setModalMode('add'); setIsModalOpen(true); }} style={{ display: hasPermission('sys:menu:add') ? undefined : 'none' }}>
            新增菜单
          </Button>
          <Button icon={<VerticalAlignTopOutlined />} onClick={() => { setSearchKeyword(''); setFilteredMenus(null); fetchMenus(); }} title="刷新" />
        </div>

        <div style={{ flex: 1, overflowY: 'auto', padding: 16 }}>
          {loading ? (
            <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}><Spin /></div>
          ) : displayMenus.length === 0 ? (
            <Empty description="暂无菜单数据" />
          ) : (
            <DndContext sensors={sensors} collisionDetection={closestCenter} onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
              {renderMenuTree(displayMenus)}
            </DndContext>
          )}
        </div>
      </div>

      {/* 右侧权限管理 */}
      <div style={{ flex: 1, overflowY: 'auto' }}>
        <PermissionManagement selectedMenu={selectedMenu} />
      </div>

      {/* 右键菜单 */}
      {rightClickMenu && (
        <>
          <div
            style={{
              position: 'fixed',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              zIndex: 999,
            }}
            onClick={() => setRightClickMenu(null)}
          />
          <div
            style={{
              position: 'fixed',
              left: rightClickMenu.x,
              top: rightClickMenu.y,
              zIndex: 1000,
            }}
          >
            <Menu
              items={rightClickMenuItems}
              onClick={() => setRightClickMenu(null)}
            />
          </div>
        </>
      )}

      <MenuModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSaveMenu}
        menu={selectedMenu || undefined}
        mode={modalMode}
        allMenus={menus}
        hideParentSelect={modalMode === 'edit' || (modalMode === 'add' && selectedMenu?.type === 'dir')}
      />

      <Modal
        title="确认删除"
        open={deleteConfirmVisible}
        onOk={() => { if (menuToDelete) handleDeleteMenu(menuToDelete.id); setDeleteConfirmVisible(false); }}
        onCancel={() => setDeleteConfirmVisible(false)}
        okText="确定"
        cancelText="取消"
        okButtonProps={{ danger: true }}
        destroyOnHidden
      >
        <p>
          {menuToDelete?.children && menuToDelete.children.length > 0
            ? `该菜单下有 ${menuToDelete.children.length} 个子菜单，将一并删除`
            : '删除后数据不可恢复'}
        </p>
        <p style={{ color: '#ff4d4f', fontWeight: 500 }}>确定要删除菜单「{menuToDelete?.name}」吗？</p>
      </Modal>
    </div>
  );
}
