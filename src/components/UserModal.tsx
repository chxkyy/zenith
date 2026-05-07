import React from 'react';
import { Modal, Form, Input, Select } from 'antd';

interface User {
  id: number;
  username: string;
  nickname: string;
  email: string;
  phone: string;
  status: number;
  password?: string;
}

interface UserModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (user: Partial<User>) => void;
  user?: User;
  mode: 'add' | 'edit';
}

const UserModal: React.FC<UserModalProps> = ({ isOpen, onClose, onSave, user, mode }) => {
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (isOpen) {
      form.setFieldsValue({
        username: user?.username || '',
        nickname: user?.nickname || '',
        email: user?.email || '',
        phone: user?.phone || '',
        status: user?.status ?? 1,
        password: ''
      });
    }
  }, [isOpen, user]);

  const handleOk = () => {
    form.validateFields().then(values => {
      const userData: Partial<User> = {
        id: user?.id,
        ...values
      };
      if (mode === 'edit' && !values.password) {
        delete userData.password;
      }
      onSave(userData);
    });
  };

  return (
    <Modal
      title={mode === 'add' ? '新增用户' : '编辑用户'}
      open={isOpen}
      onCancel={onClose}
      onOk={handleOk}
      okText={mode === 'add' ? '保存用户' : '更新用户'}
      destroyOnClose
    >
      <Form form={form} layout="vertical" preserve={false}>
        <Form.Item label="用户名" name="username" rules={[{ required: true, message: '请输入用户名' }]}>
          <Input placeholder="请输入用户名" disabled={mode === 'edit'} />
        </Form.Item>
        <Form.Item label="昵称" name="nickname">
          <Input placeholder="请输入昵称" />
        </Form.Item>
        <Form.Item
          label="密码"
          name="password"
          rules={mode === 'add' ? [{ required: true, message: '请输入密码' }] : []}
        >
          <Input.Password placeholder={mode === 'add' ? '请输入密码' : '留空则不修改密码'} />
        </Form.Item>
        <Form.Item label="邮箱" name="email">
          <Input placeholder="请输入邮箱" />
        </Form.Item>
        <Form.Item label="手机号" name="phone">
          <Input placeholder="请输入手机号" />
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
    </Modal>
  );
};

export default UserModal;
