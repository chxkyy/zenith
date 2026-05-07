import React, { useState, useEffect } from 'react';
import { Modal, Transfer, App, Spin } from 'antd';
import type { TransferProps } from 'antd';

interface Role {
  id: number;
  name: string;
  code: string;
}

interface RoleAssignModalProps {
  isOpen: boolean;
  onClose: () => void;
  userId: number;
  username: string;
}

export default function RoleAssignModal({ isOpen, onClose, userId, username }: RoleAssignModalProps) {
  const { message } = App.useApp();
  const [allRoles, setAllRoles] = useState<Role[]>([]);
  const [targetKeys, setTargetKeys] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (isOpen && userId) {
      fetchData();
    }
  }, [isOpen, userId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [rolesRes, userRolesRes] = await Promise.all([
        fetch('/api/roles'),
        fetch(`/api/user-roles?userId=${userId}`)
      ]);

      const rolesData = await rolesRes.json();
      const userRolesData = await userRolesRes.json();

      if (rolesData.success) setAllRoles(rolesData.data || []);
      if (userRolesData.success) {
        setTargetKeys((userRolesData.data || []).map((r: any) => String(r.roleId || r.id)));
      }
    } catch (error) {
      console.error('Error fetching role data:', error);
      message.error('获取角色数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleChange: TransferProps['onChange'] = (nextTargetKeys) => {
    setTargetKeys(nextTargetKeys as string[]);
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const roleIds = targetKeys.map(key => Number(key));
      const response = await fetch('/api/user-roles/assign', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, roleIds })
      });
      const data = await response.json();
      if (data.success) {
        message.success('角色分配成功');
        onClose();
      } else {
        throw new Error(data.errMessage || '分配失败');
      }
    } catch (error: any) {
      console.error('Error assigning roles:', error);
      message.error(error.message || '角色分配失败');
    } finally {
      setSaving(false);
    }
  };

  const transferDataSource = allRoles.map(role => ({
    key: String(role.id),
    title: role.name,
    description: role.code,
  }));

  return (
    <Modal
      title={`分配角色 - ${username}`}
      open={isOpen}
      onCancel={onClose}
      onOk={handleSave}
      okText="保存分配"
      confirmLoading={saving}
      width={700}
      destroyOnClose
    >
      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}><Spin tip="加载角色数据..." /></div>
      ) : (
        <Transfer
          dataSource={transferDataSource}
          targetKeys={targetKeys}
          onChange={handleChange}
          render={(item) => item.title || ''}
          titles={['可选角色', '已分配角色']}
          listStyle={{ width: 280, height: 400 }}
          style={{ marginTop: 16 }}
          showSearch
          filterOption={(inputValue, item) =>
            (item.title || '').toLowerCase().includes(inputValue.toLowerCase()) ||
            (item.description || '').toLowerCase().includes(inputValue.toLowerCase())
          }
        />
      )}
    </Modal>
  );
}
