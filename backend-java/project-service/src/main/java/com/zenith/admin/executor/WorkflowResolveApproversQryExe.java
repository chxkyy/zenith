package com.zenith.admin.executor;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.enums.ApproverTypeEnum;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkflowResolveApproversQryExe {

    private final NodeTemplateMapper nodeTemplateMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final OrgMapper orgMapper;

    public List<Long> execute(Long nodeTemplateId, Long initiatorId) {
        NodeTemplateDO node = nodeTemplateMapper.selectById(nodeTemplateId);
        if (node == null) {
            throw new RuntimeException("节点模板不存在");
        }

        List<Long> approverIds = new ArrayList<>();

        if (ApproverTypeEnum.ROLE.getCode().equals(node.getApproverType())) {
            List<Long> roleIds = JSON.parseArray(node.getApproverValue(), Long.class);
            if (roleIds != null && !roleIds.isEmpty()) {
                LambdaQueryWrapper<UserRoleDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(UserRoleDO::getRoleId, roleIds);
                List<UserRoleDO> userRoles = userRoleMapper.selectList(queryWrapper);
                approverIds = userRoles.stream()
                        .map(UserRoleDO::getUserId)
                        .distinct()
                        .collect(Collectors.toList());
            }
        } else if (ApproverTypeEnum.USER.getCode().equals(node.getApproverType())) {
            approverIds = JSON.parseArray(node.getApproverValue(), Long.class);
            if (approverIds == null) {
                approverIds = new ArrayList<>();
            }
        } else if (ApproverTypeEnum.SUPERIOR.getCode().equals(node.getApproverType())) {
            UserDO initiator = userMapper.selectById(initiatorId);
            if (initiator != null && initiator.getOrgId() != null) {
                OrgDO org = orgMapper.selectById(initiator.getOrgId());
                if (org != null && org.getParentId() != null) {
                    OrgDO parentOrg = orgMapper.selectById(org.getParentId());
                    if (parentOrg != null) {
                        LambdaQueryWrapper<UserDO> userQuery = new LambdaQueryWrapper<>();
                        userQuery.eq(UserDO::getOrgId, parentOrg.getId());
                        userQuery.eq(UserDO::getStatus, 1);
                        userQuery.last("LIMIT 1");
                        UserDO superior = userMapper.selectOne(userQuery);
                        if (superior != null) {
                            approverIds.add(superior.getId());
                        }
                    }
                }
            }
        }

        return approverIds;
    }
}
