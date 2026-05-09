import React from 'react';
import { Card, Statistic, Progress, Descriptions, Button, Row, Col, Tag } from 'antd';
import { DashboardOutlined, ReloadOutlined } from '@ant-design/icons';

const metrics = [
  { label: 'CPU 使用率', value: '12.5%', status: 'success' as const, percent: 12.5 },
  { label: '内存使用率', value: '45.2%', status: 'warning' as const, percent: 45.2 },
  { label: '磁盘使用率', value: '68.1%', status: 'warning' as const, percent: 68.1 },
  { label: '系统负载', value: '0.85', status: 'success' as const, percent: 85 },
];

const statusColorMap: Record<string, string> = {
  success: 'green',
  warning: 'orange',
};

const progressStatusMap: Record<string, 'success' | 'normal' | 'exception'> = {
  success: 'success',
  warning: 'normal',
};

export default function MonitoringTable() {
  return (
    <div style={{ padding: 32, display: 'flex', flexDirection: 'column', gap: 32 }}>
      {/* 页面标题与刷新按钮 */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: 24, fontWeight: 700, color: '#1e293b', margin: 0 }}>数据监控</h2>
          <p style={{ color: '#94a3b8', marginTop: 4 }}>实时监控系统核心资源运行状态，保障服务稳定性。</p>
        </div>
        <Button icon={<ReloadOutlined />} size="middle">
          刷新数据
        </Button>
      </div>

      {/* 监控指标卡片 */}
      <Row gutter={[24, 24]}>
        {metrics.map((metric, index) => (
          <Col key={index} xs={24} sm={12} lg={6}>
            <Card
              style={{ height: '100%' }}
              styles={{ body: { display: 'flex', flexDirection: 'column', gap: 16 } }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <DashboardOutlined style={{ fontSize: 28, color: statusColorMap[metric.status] }} />
                <Tag color={statusColorMap[metric.status]} style={{ fontWeight: 600, margin: 0 }}>
                  {metric.status === 'success' ? '正常' : '警告'}
                </Tag>
              </div>
              <Statistic
                title={metric.label}
                value={metric.value}
                valueStyle={{ fontSize: 28, fontWeight: 700, color: '#1e293b' }}
              />
              <Progress
                percent={metric.percent}
                status={progressStatusMap[metric.status]}
                showInfo={false}
                size="small"
              />
            </Card>
          </Col>
        ))}
      </Row>

      {/* 服务器信息 */}
      <Card
        title={
          <span style={{ display: 'inline-flex', alignItems: 'center', gap: 8, fontWeight: 700 }}>
            <DashboardOutlined style={{ color: '#94a3b8' }} />
            服务器信息
          </span>
        }
      >
        <Descriptions column={{ xs: 1, sm: 2 }} bordered size="middle">
          <Descriptions.Item label="服务器名称">Zenith-Prod-01</Descriptions.Item>
          <Descriptions.Item label="操作系统">Linux (Ubuntu 22.04.3 LTS)</Descriptions.Item>
          <Descriptions.Item label="服务器IP">172.16.0.10</Descriptions.Item>
          <Descriptions.Item label="Java版本">OpenJDK 17.0.8</Descriptions.Item>
          <Descriptions.Item label="启动时间">2026-03-01 00:00:00</Descriptions.Item>
          <Descriptions.Item label="运行时间">22天 13小时 23分</Descriptions.Item>
        </Descriptions>
      </Card>
    </div>
  );
}
