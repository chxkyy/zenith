import React, { useEffect, useRef, useState } from 'react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts';
import { UserOutlined, ShoppingOutlined, MonitorOutlined, WarningOutlined, ExportOutlined } from '@ant-design/icons';
import { Card, Button, Select, Spin, Result, Row, Col, Avatar, Typography } from 'antd';
import StatsCard from './StatsCard';

const { Title, Text, Paragraph } = Typography;

export default function Dashboard() {
  const [stats, setStats] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const hasFetchedStats = useRef(false);

  useEffect(() => {
    if (hasFetchedStats.current) return;
    hasFetchedStats.current = true;
    fetch('/api/stats/overview')
      .then(async res => {
        const text = await res.text();
        if (res.status === 503) {
          throw new Error('Java 后端正在启动，请稍候...');
        }
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}. ${text}`);
        }
        if (!text) {
          throw new Error('服务器返回了空响应');
        }
        try {
          return JSON.parse(text);
        } catch (e) {
          console.error('Failed to parse JSON:', text);
          throw new Error('服务器返回了非 JSON 格式的数据');
        }
      })
      .then(data => {
        if (data.success) {
          setStats(data);
        } else {
          setError(data.errMessage || '获取统计数据失败');
        }
      })
      .catch(err => {
        console.error('Error fetching stats:', err);
        setError(err.message || '网络错误');
      });
  }, []);

  if (error) return (
    <div style={{ padding: 32, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 'calc(100vh - 64px)' }}>
      <Result
        status="error"
        title={error}
        extra={<Button type="primary" onClick={() => window.location.reload()}>刷新页面重试</Button>}
      />
    </div>
  );

  if (!stats || !stats.success) return (
    <div style={{ padding: 32, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: 'calc(100vh - 64px)' }}>
      <Spin size="large" tip="正在加载统计数据..." />
    </div>
  );

  const data = stats.data;

  const chartData = data.chartData && data.chartData.length > 0 ? data.chartData : [
    { name: '1月', value: 0 },
    { name: '2月', value: 0 },
    { name: '3月', value: 0 },
    { name: '4月', value: 0 },
    { name: '5月', value: 0 },
    { name: '6月', value: 0 }
  ];

  return (
    <div style={{ padding: 32, display: 'flex', flexDirection: 'column', gap: 32 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <Title level={2} style={{ margin: 0 }}>欢迎回来, 管理员</Title>
          <Paragraph type="secondary" style={{ marginTop: 4 }}>系统已准备就绪。后端已升级为阿里 COLA 分层架构。</Paragraph>
        </div>
        <Button type="primary" icon={<ExportOutlined />} size="large">
          导出报告
        </Button>
      </div>

      <Row gutter={[24, 24]}>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="总用户数"
            value={data.totalUsers.toLocaleString()}
            change={12.5}
            icon={<UserOutlined />}
            color="#2563eb"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="系统角色"
            value={data.totalRoles.toLocaleString()}
            change={0}
            icon={<ShoppingOutlined />}
            color="#9333ea"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="操作日志"
            value={data.operLogs.toLocaleString()}
            change={5.4}
            icon={<MonitorOutlined />}
            color="#d97706"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="异常日志"
            value={data.errorLogs.toLocaleString()}
            change={-10.2}
            icon={<WarningOutlined />}
            color="#dc2626"
          />
        </Col>
      </Row>

      <Row gutter={[32, 32]}>
        <Col xs={24} lg={16}>
          <Card style={{ borderRadius: 12 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 32 }}>
              <Title level={4} style={{ margin: 0 }}>收入趋势</Title>
              <Select defaultValue="6" size="small" style={{ width: 140 }}
                options={[
                  { value: '6', label: '最近 6 个月' },
                  { value: '12', label: '最近 12 个月' },
                ]}
              />
            </div>
            <div style={{ height: 300, width: '100%', minWidth: 200, minHeight: 200 }}>
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                  <defs>
                    <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.1} />
                      <stop offset="95%" stopColor="#3b82f6" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                  <XAxis
                    dataKey="name"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: '#64748b', fontSize: 12 }}
                    dy={10}
                  />
                  <YAxis
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: '#64748b', fontSize: 12 }}
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#fff',
                      borderRadius: 12,
                      border: '1px solid #e2e8f0',
                      boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'
                    }}
                  />
                  <Area
                    type="monotone"
                    dataKey="value"
                    stroke="#3b82f6"
                    strokeWidth={3}
                    fillOpacity={1}
                    fill="url(#colorValue)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Col>

        <Col xs={24} lg={8}>
          <Card style={{ borderRadius: 12 }}>
            <Title level={4} style={{ marginBottom: 24 }}>最近动态</Title>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
              {[1, 2, 3, 4].map((i) => (
                <div key={i} style={{ display: 'flex', gap: 16 }}>
                  <Avatar icon={<UserOutlined />} style={{ backgroundColor: '#f1f5f9', color: '#475569', flexShrink: 0 }} />
                  <div>
                    <Text strong style={{ display: 'block', fontSize: 14 }}>新用户注册</Text>
                    <Text type="secondary" style={{ fontSize: 12, marginTop: 2, display: 'block' }}>用户 ID #120{i} 刚刚加入了系统。</Text>
                    <Text type="secondary" style={{ fontSize: 10, marginTop: 4, display: 'block', letterSpacing: 1, fontWeight: 700 }}>2 分钟前</Text>
                  </div>
                </div>
              ))}
            </div>
            <Button type="link" block style={{ marginTop: 32, fontWeight: 600 }}>
              查看全部动态
            </Button>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
