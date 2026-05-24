import React, { useState, useEffect } from 'react';
import { Card, Select, Button, Form, Input, InputNumber, DatePicker, Upload, Space, App, Steps, Table, Tag } from 'antd';
import { PlusOutlined, SendOutlined, SaveOutlined } from '@ant-design/icons';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { formatDateTime } from '../lib/utils';
import Notification from './Notification';

const { RangePicker } = DatePicker;

interface ProcessTemplate {
  id: number;
  code: string;
  name: string;
  description: string;
  formSchema: string;
  status: number;
  nodes: NodeTemplate[];
}

interface NodeTemplate {
  id: number;
  nodeOrder: number;
  nodeName: string;
  nodeType: number;
  approverType: number;
  approverTypeName: string;
}

interface FormField {
  name: string;
  type: string;
  label: string;
  required?: boolean;
  options?: string[];
  placeholder?: string;
  min?: number;
  max?: number;
  maxLength?: number;
  maxCount?: number;
  accept?: string;
}

export default function StartProcess() {
  const { message } = App.useApp();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [templates, setTemplates] = useState<ProcessTemplate[]>([]);
  const [selectedTemplate, setSelectedTemplate] = useState<ProcessTemplate | null>(null);
  const [formFields, setFormFields] = useState<FormField[]>([]);
  const [formData, setFormData] = useState<Record<string, any>>({});
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info'; key: number } | null>(null);
  const [processInstanceId, setProcessInstanceId] = useState<number | null>(null);

  useEffect(() => {
    fetchTemplates();
  }, []);

  const fetchTemplates = async () => {
    try {
      const response = await fetch('/api/workflow/process-templates/list-active');
      const data = await response.json();
      if (data.success && data.data) {
        setTemplates(data.data);
      }
    } catch (error) {
      console.error('获取流程模板失败:', error);
      setNotification({ message: '获取流程模板失败', type: 'error', key: Date.now() });
    }
  };

  const handleTemplateChange = async (templateId: number) => {
    const template = templates.find(t => t.id === templateId);
    if (!template) return;

    setLoading(true);
    try {
      const response = await fetch(`/api/workflow/process-templates/detail?id=${templateId}`);
      const data = await response.json();
      if (data.success && data.data) {
        const detail = data.data;
        setSelectedTemplate(detail);
        setProcessInstanceId(null);
        
        if (detail.formSchema) {
          try {
            const schema = JSON.parse(detail.formSchema);
            setFormFields(schema.fields || []);
            const initialData: Record<string, any> = {};
            schema.fields?.forEach((field: FormField) => {
              initialData[field.name] = field.type === 'multiSelect' ? [] : 
                                        field.type === 'dateRange' ? null : '';
            });
            setFormData(initialData);
          } catch (e) {
            setFormFields([]);
            setFormData({});
          }
        } else {
          setFormFields([]);
          setFormData({});
        }
      }
    } catch (error) {
      setNotification({ message: '获取流程模板详情失败', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  const handleFieldChange = (fieldName: string, value: any) => {
    setFormData(prev => ({ ...prev, [fieldName]: value }));
  };

  const handleSaveDraft = async () => {
    if (!selectedTemplate) {
      setNotification({ message: '请先选择流程类型', type: 'error', key: Date.now() });
      return;
    }

    setLoading(true);
    try {
      const body: any = {
        processTemplateId: selectedTemplate.id,
        title: generateTitle(),
        formData: JSON.stringify(formData),
      };

      if (formData.amount !== undefined && formData.amount !== '') {
        body.amount = formData.amount;
      }
      if (formData.dateRange && formData.dateRange.length === 2) {
        body.startDate = formData.dateRange[0].valueOf();
        body.endDate = formData.dateRange[1].valueOf();
      }

      const response = await fetch('/api/workflow/process-instances/save-draft', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });

      const data = await response.json();
      if (data.success) {
        setProcessInstanceId(data.data);
        setNotification({ message: '草稿保存成功', type: 'success', key: Date.now() });
      } else {
        setNotification({ message: data.errMessage || '保存失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      setNotification({ message: '保存失败，请重试', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    const instanceId = processInstanceId;
    
    if (!instanceId) {
      await handleSaveDraft();
      if (!processInstanceId) return;
    }

    setLoading(true);
    try {
      const response = await fetch('/api/workflow/process-instances/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: processInstanceId }),
      });

      const data = await response.json();
      if (data.success) {
        setNotification({ message: '流程提交成功', type: 'success', key: Date.now() });
        setTimeout(() => {
          navigate('/workflow/my');
        }, 1500);
      } else {
        setNotification({ message: data.errMessage || '提交失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      setNotification({ message: '提交失败，请重试', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  const generateTitle = () => {
    if (formData.title) return formData.title;
    const templateName = selectedTemplate?.name || '申请';
    return `${templateName}申请`;
  };

  const renderFormField = (field: FormField) => {
    switch (field.type) {
      case 'text':
        return (
          <Input
            placeholder={field.placeholder || `请输入${field.label}`}
            value={formData[field.name] || ''}
            onChange={(e) => handleFieldChange(field.name, e.target.value)}
            maxLength={field.maxLength}
          />
        );
      case 'textarea':
        return (
          <Input.TextArea
            rows={4}
            placeholder={field.placeholder || `请输入${field.label}`}
            value={formData[field.name] || ''}
            onChange={(e) => handleFieldChange(field.name, e.target.value)}
            maxLength={field.maxLength}
          />
        );
      case 'number':
        return (
          <InputNumber
            style={{ width: '100%' }}
            placeholder={field.placeholder || `请输入${field.label}`}
            value={formData[field.name]}
            onChange={(val) => handleFieldChange(field.name, val)}
            min={field.min}
            max={field.max}
          />
        );
      case 'select':
        return (
          <Select
            style={{ width: '100%' }}
            placeholder={field.placeholder || `请选择${field.label}`}
            value={formData[field.name] || undefined}
            onChange={(val) => handleFieldChange(field.name, val)}
            options={field.options?.map(opt => ({ value: opt, label: opt }))}
          />
        );
      case 'multiSelect':
        return (
          <Select
            mode="multiple"
            style={{ width: '100%' }}
            placeholder={field.placeholder || `请选择${field.label}`}
            value={formData[field.name] || []}
            onChange={(val) => handleFieldChange(field.name, val)}
            options={field.options?.map(opt => ({ value: opt, label: opt }))}
          />
        );
      case 'date':
        return (
          <DatePicker
            style={{ width: '100%' }}
            placeholder={field.placeholder || `请选择${field.label}`}
            value={formData[field.name]}
            onChange={(date) => handleFieldChange(field.name, date)}
          />
        );
      case 'dateRange':
        return (
          <RangePicker
            style={{ width: '100%' }}
            placeholder={['开始日期', '结束日期']}
            value={formData[field.name]}
            onChange={(dates) => handleFieldChange(field.name, dates)}
          />
        );
      case 'amount':
        return (
          <InputNumber
            style={{ width: '100%' }}
            placeholder={field.placeholder || `请输入${field.label}`}
            value={formData[field.name]}
            onChange={(val) => handleFieldChange(field.name, val)}
            min={0}
            precision={2}
            formatter={value => `¥ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
            parser={value => value!.replace(/\¥\s?|(,*)/g, '') as any}
          />
        );
      case 'file':
        return (
          <Upload
            maxCount={field.maxCount || 5}
            accept={field.accept}
            listType="text"
          >
            <Button icon={<PlusOutlined />}>上传文件</Button>
          </Upload>
        );
      default:
        return null;
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 16 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>发起申请</h2>
        <p style={{ color: '#64748b', marginTop: 4 }}>选择流程类型并填写申请表单。</p>
      </div>

      <Card style={{ marginBottom: 16 }}>
        <div style={{ marginBottom: 16 }}>
          <label style={{ fontWeight: 500, marginBottom: 8, display: 'block' }}>选择流程类型</label>
          <Select
            style={{ width: 300 }}
            placeholder="请选择流程类型"
            value={selectedTemplate?.id}
            onChange={handleTemplateChange}
            options={templates.map(t => ({ value: t.id, label: t.name }))}
          />
        </div>
      </Card>

      {selectedTemplate && (
        <>
          <Card title="流程预览" style={{ marginBottom: 16 }}>
            <Steps
              current={-1}
              items={selectedTemplate.nodes?.map(node => ({
                title: node.nodeName,
                description: node.approverTypeName === '发起人上级' ? '上级审批' : 
                           node.nodeType === 2 ? '会签' : '审批',
              })) || []}
            />
          </Card>

          <Card title="申请表单">
            <Form layout="vertical">
              <Form.Item label="申请标题" required>
                <Input
                  placeholder="请输入申请标题"
                  value={formData.title || ''}
                  onChange={(e) => handleFieldChange('title', e.target.value)}
                />
              </Form.Item>

              {formFields.map(field => (
                <Form.Item 
                  key={field.name} 
                  label={field.label}
                  required={field.required}
                >
                  {renderFormField(field)}
                </Form.Item>
              ))}
            </Form>

            <div style={{ marginTop: 24, textAlign: 'right' }}>
              <Space>
                <Button icon={<SaveOutlined />} onClick={handleSaveDraft} loading={loading}>
                  保存草稿
                </Button>
                <Button type="primary" icon={<SendOutlined />} onClick={handleSubmit} loading={loading}>
                  提交申请
                </Button>
              </Space>
            </div>
          </Card>
        </>
      )}

      {notification && (
        <Notification key={notification.key} message={notification.message} type={notification.type} />
      )}
    </div>
  );
}
