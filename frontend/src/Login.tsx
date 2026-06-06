import React from 'react';
import { Form, Input, Button, Card, App } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { post } from './lib/apiClient';

interface LoginProps {
  onLoginSuccess: () => void;
}

interface LoginForm {
  loginId: string;
  password: string;
}

export default function Login({ onLoginSuccess }: LoginProps) {
  const { message } = App.useApp();
  const [form] = Form.useForm<LoginForm>();
  const [loading, setLoading] = React.useState(false);

  const handleSubmit = async (values: LoginForm) => {
    setLoading(true);
    try {
      const data = await post<{ success: boolean; errMessage?: string }>('/api/auth/login', values as unknown as Record<string, unknown>);

      message.success('登录成功');
      setTimeout(() => onLoginSuccess(), 500);
    } catch (error) {
      message.error(error instanceof Error ? error.message : '网络错误，请重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%)',
    }}>
      <Card style={{ width: 400 }} variant="borderless">
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: '#1e293b', margin: 0 }}>
            Zenith 系统
          </h1>
          <p style={{ color: '#64748b', marginTop: 8 }}>请登录以继续</p>
        </div>

        <Form
          form={form}
          onFinish={handleSubmit}
          initialValues={{ loginId: 'admin', password: '000000' }}
          size="large"
        >
          <Form.Item
            name="loginId"
            rules={[{ required: true, message: '请输入登录账号' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="请输入登录账号" />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入密码" />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <Button type="primary" htmlType="submit" loading={loading} block>
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
