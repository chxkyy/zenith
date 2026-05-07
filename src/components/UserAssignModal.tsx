import React, { useState, useEffect } from 'react';
import { Modal, Transfer, App, Spin } from 'antd';
import type { TransferProps } from 'antd';

interface User {
  id: number;
  username: string;
  nickname: string;
}

interface UserAssignModalProps {
  isOpen: boolean;
  onClose: () => void;
  roleId: number;
  roleName: string;
}

export default function UserAssignModal({ isOpen, onClose, roleId, roleName }: UserAssignModalProps) {
  const { message } = App.useApp();
  const [allUsers, setAllUsers] = useState<User[]>([]);
  const [targetKeys, setTargetKeys] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (isOpen && roleId) {
      fetchData();
    }
  }, [isOpen, roleId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [usersRes, roleUsersRes] = await Promise.all([
        fetch('/api/users'),
        fetch(`/api/role-users?roleId=${roleId}`)
      ]);

      const usersData = await usersRes.json();
      const roleUsersData = await roleUsersRes.json();

      if (usersData.success) setAllUsers(usersData.data || []);
      if (roleUsersData.success) {
        setTargetKeys((roleUsersData.data || []).map((u: any) => String(u.userId || u.id)));
      }
    } catch (error) {
      console.error('Error fetching user data:', error);
      message.error('获取用户数据失败');
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
      const userIds = targetKeys.map(key => Number(key));
      const response = await fetch('/api/role-users/assign', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ roleId, userIds })
      });
      const data = await response.json();
      if (data.success) {
        message.success('用户分配成功');
        onClose();
      } else {
        throw new Error(data.errMessage || '分配失败');
      }
    } catch (error: any) {
      console.error('Error assigning users:', error);
      message.error(error.message || '用户分配失败');
    } finally {
      setSaving(false);
    }
  };

  const transferDataSource = allUsers.map(user => ({
    key: String(user.id),
    title: user.nickname || user.username,
    description: user.username,
  }));

  return (
    <Modal
      title={`分配用户 - ${roleName}`}
      open={isOpen}
      onCancel={onClose}
      onOk={handleSave}
      okText="保存分配"
      confirmLoading={saving}
      width={700}
      destroyOnClose
    >
      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}><Spin tip="加载用户数据..." /></div>
      ) : (
        <Transfer
          dataSource={transferDataSource}
          targetKeys={targetKeys}
          onChange={handleChange}
          render={(item) => item.title || ''}
          titles={['可选用户', '已分配用户']}
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
