import React from 'react';
import { Card, Statistic } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons';

interface StatsCardProps {
  title: string;
  value: string;
  change: number;
  icon: React.ReactNode;
  color: string;
}

export default function StatsCard({ title, value, change, icon, color }: StatsCardProps) {
  const isPositive = change > 0;

  return (
    <Card style={{ borderRadius: 12 }} hoverable>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
        <div style={{
          padding: 12,
          borderRadius: 10,
          backgroundColor: color,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}>
          <span style={{ color: '#fff', fontSize: 24, display: 'flex', alignItems: 'center' }}>
            {icon}
          </span>
        </div>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: 4,
          padding: '4px 8px',
          borderRadius: 6,
          fontSize: 12,
          fontWeight: 500,
          backgroundColor: isPositive ? '#f0fdf4' : '#fef2f2',
          color: isPositive ? '#16a34a' : '#dc2626',
        }}>
          {isPositive ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
          {Math.abs(change)}%
        </div>
      </div>
      <Statistic title={title} value={value} />
    </Card>
  );
}
