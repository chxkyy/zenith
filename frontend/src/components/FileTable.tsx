import React, { useState, useRef } from 'react';
import { Table, Button, Input, Popconfirm, Space, App } from 'antd';
import {
  FileOutlined,
  FileImageOutlined,
  FilePdfOutlined,
  FileZipOutlined,
  DownloadOutlined,
  DeleteOutlined,
  UploadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';
import { usePaginatedQuery } from '../lib/useCrudTable';
import { post, del, uploadFile, downloadBlob } from '../lib/apiClient';

interface FileItem {
  id: number;
  name: string;
  originalName: string;
  path: string;
  type: string;
  size: number;
  uploader: string;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

export default function FileTable() {
  const { message } = App.useApp();
  const { hasPermission } = usePermission();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const {
    data: files,
    loading,
    currentPage,
    pageSize,
    totalCount,
    keyword,
    setKeyword,
    goToPage,
    search,
    refresh,
  } = usePaginatedQuery<FileItem>({
    apiUrl: '/api/files/page',
  });

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      await uploadFile('/api/files/upload', formData);
      message.success('上传成功');
      refresh();
    } catch (error) {
      console.error('Error uploading file:', error);
      message.error('上传失败，请重试');
    }

    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await del('/api/files/delete', { id });
      message.success('删除成功');
      refresh();
    } catch (error) {
      console.error('Error deleting file:', error);
      message.error('删除失败，请重试');
    }
  };

  const handleDownload = async (id: number, fileName: string) => {
    try {
      const blob = await downloadBlob('/api/files/download', { id: String(id) });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Error downloading file:', error);
      message.error('下载失败，请重试');
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  };

  const getIcon = (type: string) => {
    const upperType = type.toUpperCase();
    switch (upperType) {
      case 'IMAGE':
      case 'JPG':
      case 'JPEG':
      case 'PNG':
      case 'GIF':
      case 'BMP':
      case 'WEBP':
        return <FileImageOutlined style={{ color: '#3b82f6', fontSize: 20 }} />;
      case 'PDF':
        return <FilePdfOutlined style={{ color: '#ef4444', fontSize: 20 }} />;
      case 'DOC':
      case 'DOCX':
        return <FilePdfOutlined style={{ color: '#2563eb', fontSize: 20 }} />;
      case 'XLS':
      case 'XLSX':
        return <FilePdfOutlined style={{ color: '#059669', fontSize: 20 }} />;
      case 'ZIP':
      case 'RAR':
      case '7Z':
        return <FileZipOutlined style={{ color: '#d97706', fontSize: 20 }} />;
      default:
        return <FileOutlined style={{ color: '#64748b', fontSize: 20 }} />;
    }
  };

  const columns: ColumnsType<FileItem> = [
    {
      title: '文件名',
      dataIndex: 'originalName',
      key: 'originalName',
      render: (text: string, record: FileItem) => (
        <Space>
          {getIcon(record.type)}
          <span>{text}</span>
        </Space>
      ),
    },
    {
      title: '大小',
      dataIndex: 'size',
      key: 'size',
      width: 120,
      render: (size: number) => formatFileSize(size),
    },
    {
      title: '存储路径',
      dataIndex: 'path',
      key: 'path',
      ellipsis: true,
    },
    {
      title: '创建人',
      dataIndex: 'createUserName',
      key: 'createUserName',
      width: 100,
      render: (text: string) => text || '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 180,
      render: (text: string) => formatDateTime(text),
    },
    {
      title: '修改人',
      dataIndex: 'updateUserName',
      key: 'updateUserName',
      width: 100,
      render: (text: string) => text || '-',
    },
    {
      title: '修改时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 180,
      render: (text: string) => formatDateTime(text),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: unknown, record: FileItem) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<DownloadOutlined />}
            onClick={() => handleDownload(record.id, record.originalName)}
          >
            下载
          </Button>
          {hasPermission('file:delete') && (
          <Popconfirm
            title="确定要删除该文件吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <div>
          <h2 style={{ margin: 0, fontSize: 20, fontWeight: 600 }}>文件管理</h2>
          <p style={{ margin: '4px 0 0', color: '#64748b' }}>管理系统上传的所有资源文件，支持预览、下载及删除。</p>
        </div>
        <div>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleUpload}
            style={{ display: 'none' }}
          />
          <Button
            type="primary"
            icon={<UploadOutlined />}
            onClick={() => fileInputRef.current?.click()}
            style={{ display: hasPermission('file:upload') ? undefined : 'none' }}
          >
            上传文件
          </Button>
        </div>
      </div>

      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Input.Search
          placeholder="搜索文件名..."
          allowClear
          prefix={<SearchOutlined />}
          style={{ width: 300 }}
          onSearch={(value) => {
            setKeyword(value);
            search();
          }}
          onChange={(e) => {
            if (!e.target.value) {
              setKeyword('');
              search();
            }
          }}
        />
      </div>

      <Table<FileItem>
        columns={columns}
        dataSource={files}
        rowKey="id"
        loading={loading}
        size="small"
        pagination={{
          current: currentPage,
          pageSize: pageSize,
          total: totalCount,
          showTotal: (total) => `共 ${total} 条`,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '50', '100'],
          size: 'default',
          onChange: (page, size) => goToPage(page, size),
        }}
      />
    </div>
  );
}
