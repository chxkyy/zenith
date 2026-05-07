import React, { useState, useEffect, useRef } from 'react';
import { Card, Descriptions, Avatar, Tag, Button, Modal, Form, Input, App, Spin, Row, Col } from 'antd';
import { UserOutlined, EditOutlined, MailOutlined, PhoneOutlined, LockOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';

interface UserProfile {
  id: number;
  username: string;
  nickname: string;
  email: string;
  phone: string;
  status: number;
  roles: string[];
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

export default function Profile() {
  const { message } = App.useApp();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
  const [editForm] = Form.useForm();
  const [passwordForm] = Form.useForm();
  const hasFetched = useRef(false);

  useEffect(() => {
    if (hasFetched.current) return;
    hasFetched.current = true;
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/auth/me');
      if (!response.ok) throw new Error('Failed to fetch profile');
      const data = await response.json();
      if (data.success) {
        setProfile(data.data);
      } else {
        message.error(data.errMessage || '获取个人信息失败');
      }
    } catch (error: any) {
      console.error('Error fetching profile:', error);
      message.error(error.message || '获取个人信息失败');
    } finally {
      setLoading(false);
    }
  };

  const handleEditProfile = async () => {
    try {
      const values = await editForm.validateFields();
      const response = await fetch('/api/auth/profile', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(values)
      });
      const data = await response.json();
      if (data.success) {
        message.success('个人信息更新成功');
        setIsEditModalOpen(false);
        fetchProfile();
      } else {
        throw new Error(data.errMessage || '更新失败');
      }
    } catch (error: any) {
      console.error('Error updating profile:', error);
      message.error(error.message || '更新失败');
    }
  };

  const handleChangePassword = async () => {
    try {
      const values = await passwordForm.validateFields();
      if (values.newPassword !== values.confirmPassword) {
        message.error('两次输入的密码不一致');
        return;
      }
      const response = await fetch('/api/auth/password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ oldPassword: values.oldPassword, newPassword: values.newPassword })
      });
      const data = await response.json();
      if (data.success) {
        message.success('密码修改成功');
        setIsPasswordModalOpen(false);
        passwordForm.resetFields();
      } else {
        throw new Error(data.errMessage || '修改密码失败');
      }
    } catch (error: any) {
      console.error('Error changing password:', error);
      message.error(error.message || '修改密码失败');
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '60vh' }}>
        <Spin size="large" description="加载中..." />
      </div>
    );
  }

  if (!profile) {
    return <div style={{ padding: 32, textAlign: 'center', color: '#999' }}>无法加载个人信息</div>;
  }

  return (
    <div style={{ padding: 32, maxWidth: 900, margin: '0 auto' }}>
      <Row gutter={[24, 24]}>
        <Col span={24}>
          <Card style={{ borderRadius: 12 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 24, marginBottom: 24 }}>
              <Avatar size={80} icon={<UserOutlined />} style={{ backgroundColor: '#2563eb', fontSize: 36 }} />
              <div>
                <h2 style={{ margin: 0, fontSize: 24, fontWeight: 700 }}>{profile.nickname || profile.username}</h2>
                <div style={{ marginTop: 8, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                  {profile.roles?.map((role, index) => (
                    <Tag key={index} color="blue">{role}</Tag>
                  ))}
                </div>
              </div>
              <div style={{ marginLeft: 'auto', display: 'flex', gap: 12 }}>
                <Button icon={<EditOutlined />} onClick={() => {
                  editForm.setFieldsValue({
                    nickname: profile.nickname,
                    email: profile.email,
                    phone: profile.phone
                  });
                  setIsEditModalOpen(true);
                }}>编辑资料</Button>
                <Button icon={<LockOutlined />} onClick={() => setIsPasswordModalOpen(true)}>修改密码</Button>
              </div>
            </div>

            <Descriptions column={2} bordered size="small">
              <Descriptions.Item label="用户名">{profile.username}</Descriptions.Item>
              <Descriptions.Item label="昵称">{profile.nickname || '-'}</Descriptions.Item>
              <Descriptions.Item label="邮箱">
                <span><MailOutlined style={{ marginRight: 8 }} />{profile.email || '-'}</span>
              </Descriptions.Item>
              <Descriptions.Item label="手机号">
                <span><PhoneOutlined style={{ marginRight: 8 }} />{profile.phone || '-'}</span>
              </Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={profile.status === 1 ? 'success' : 'default'}>{profile.status === 1 ? '启用' : '禁用'}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="角色">{profile.roles?.join(', ') || '-'}</Descriptions.Item>
              <Descriptions.Item label="创建人">{profile.createUserName || '-'}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{formatDateTime(profile.createdTime)}</Descriptions.Item>
              <Descriptions.Item label="修改人">{profile.updateUserName || '-'}</Descriptions.Item>
              <Descriptions.Item label="修改时间">{formatDateTime(profile.updateTime)}</Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
      </Row>

      {/* 编辑资料弹窗 */}
      <Modal
        title="编辑个人资料"
        open={isEditModalOpen}
        onCancel={() => setIsEditModalOpen(false)}
        onOk={handleEditProfile}
        okText="保存"
        destroyOnHidden
      >
        <Form form={editForm} layout="vertical" preserve={false}>
          <Form.Item label="昵称" name="nickname">
            <Input placeholder="请输入昵称" />
          </Form.Item>
          <Form.Item label="邮箱" name="email">
            <Input placeholder="请输入邮箱" prefix={<MailOutlined />} />
          </Form.Item>
          <Form.Item label="手机号" name="phone">
            <Input placeholder="请输入手机号" prefix={<PhoneOutlined />} />
          </Form.Item>
        </Form>
      </Modal>

      {/* 修改密码弹窗 */}
      <Modal
        title="修改密码"
        open={isPasswordModalOpen}
        onCancel={() => setIsPasswordModalOpen(false)}
        onOk={handleChangePassword}
        okText="确认修改"
        destroyOnHidden
      >
        <Form form={passwordForm} layout="vertical" preserve={false}>
          <Form.Item label="当前密码" name="oldPassword" rules={[{ required: true, message: '请输入当前密码' }]}>
            <Input.Password placeholder="请输入当前密码" prefix={<LockOutlined />} />
          </Form.Item>
          <Form.Item label="新密码" name="newPassword" rules={[{ required: true, message: '请输入新密码' }]}>
            <Input.Password placeholder="请输入新密码" prefix={<LockOutlined />} />
          </Form.Item>
          <Form.Item label="确认新密码" name="confirmPassword" rules={[{ required: true, message: '请确认新密码' }]}>
            <Input.Password placeholder="请再次输入新密码" prefix={<LockOutlined />} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
