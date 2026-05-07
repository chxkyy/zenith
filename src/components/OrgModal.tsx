import React from 'react';
import { Modal, Form, Input, InputNumber, Select } from 'antd';

interface Org {
  id: number;
  name: string;
  parentId: number | null;
  sort: number;
  status: number;
  children?: Org[];
}

interface OrgModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (org: Partial<Org>) => void;
  org?: Org;
  mode: 'add' | 'add-sub' | 'edit';
  allOrgs: Org[];
  hideParentSelect?: boolean;
}

const OrgModal: React.FC<OrgModalProps> = ({ isOpen, onClose, onSave, org, mode, allOrgs, hideParentSelect }) => {
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (isOpen) {
      form.setFieldsValue({
        name: org?.name || '',
        parentId: mode === 'add-sub' && org?.id ? org.id : (org?.parentId || null),
        sort: org?.sort || 1,
        status: org?.status ?? 1
      });
    }
  }, [isOpen, org, mode]);

  const getTitle = () => {
    switch (mode) {
      case 'add': return '新增组织';
      case 'add-sub': return '新增子组织';
      case 'edit': return '编辑组织';
      default: return '组织管理';
    }
  };

  const handleOk = () => {
    form.validateFields().then(values => {
      const orgData: Partial<Org> = {
        id: org?.id,
        ...values
      };
      onSave(orgData);
    });
  };

  return (
    <Modal
      title={getTitle()}
      open={isOpen}
      onCancel={onClose}
      onOk={handleOk}
      okText={mode === 'edit' ? '更新组织' : '保存组织'}
      destroyOnHidden
    >
      <Form form={form} layout="vertical" preserve={false}>
        <Form.Item label="组织名称" name="name" rules={[{ required: true, message: '请输入组织名称' }]}>
          <Input placeholder="请输入组织名称" maxLength={50} />
        </Form.Item>
        {!hideParentSelect && (
          <Form.Item label="上级组织" name="parentId">
            <Select placeholder="顶级组织" allowClear
              options={allOrgs
                .filter(o => !org?.id || o.id !== org.id)
                .map(parentOrg => ({ value: parentOrg.id, label: parentOrg.name }))}
            />
          </Form.Item>
        )}
        {hideParentSelect && org && (
          <Form.Item label="上级组织">
            <Input value={org.name} disabled />
          </Form.Item>
        )}
        <Form.Item label="排序" name="sort" rules={[{ required: true }]}>
          <InputNumber min={0} placeholder="请输入排序数字，数字越小越靠前" style={{ width: '100%' }} />
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

export default OrgModal;
