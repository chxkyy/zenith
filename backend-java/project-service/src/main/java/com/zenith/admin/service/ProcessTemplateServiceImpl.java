package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.ProcessTemplateService;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dto.data.*;
import com.zenith.admin.enums.NodeTypeEnum;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessTemplateServiceImpl implements ProcessTemplateService {

    private final ProcessTemplateMapper processTemplateMapper;
    private final NodeTemplateMapper nodeTemplateMapper;

    @Override
    public PageInfo<ProcessTemplateDTO> page(ProcessTemplatePageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        
        LambdaQueryWrapper<ProcessTemplateDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getName() != null && !query.getName().isEmpty()) {
            queryWrapper.like(ProcessTemplateDO::getName, query.getName());
        }
        if (query.getStatus() != null) {
            queryWrapper.eq(ProcessTemplateDO::getStatus, query.getStatus());
        }
        queryWrapper.orderByDesc(ProcessTemplateDO::getCreatedTime);
        
        List<ProcessTemplateDO> list = processTemplateMapper.selectList(queryWrapper);
        PageInfo<ProcessTemplateDO> pageInfo = new PageInfo<>(list);
        
        List<ProcessTemplateDTO> dtos = pageInfo.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        PageInfo<ProcessTemplateDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public ProcessTemplateDTO getById(Long id) {
        ProcessTemplateDO templateDO = processTemplateMapper.selectById(id);
        if (templateDO == null) {
            return null;
        }
        
        ProcessTemplateDTO dto = convertToDTO(templateDO);
        
        List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(id);
        List<NodeTemplateDTO> nodeDTOs = nodes.stream()
                .map(this::convertNodeToDTO)
                .collect(Collectors.toList());
        dto.setNodes(nodeDTOs);
        
        return dto;
    }

    @Override
    public List<ProcessTemplateDTO> listActive() {
        LambdaQueryWrapper<ProcessTemplateDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessTemplateDO::getStatus, 1);
        queryWrapper.orderByAsc(ProcessTemplateDO::getName);
        
        List<ProcessTemplateDO> list = processTemplateMapper.selectList(queryWrapper);
        return list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(ProcessTemplateCreateCmd cmd) {
        ProcessTemplateDO templateDO = new ProcessTemplateDO();
        templateDO.setCode(cmd.getCode());
        templateDO.setName(cmd.getName());
        templateDO.setDescription(cmd.getDescription());
        templateDO.setFormSchema(cmd.getFormSchema());
        templateDO.setStatus(1);
        templateDO.setVersion(1);
        processTemplateMapper.insert(templateDO);
        
        if (cmd.getNodes() != null && !cmd.getNodes().isEmpty()) {
            for (NodeTemplateCreateCmd nodeCmd : cmd.getNodes()) {
                NodeTemplateDO nodeDO = new NodeTemplateDO();
                nodeDO.setProcessTemplateId(templateDO.getId());
                nodeDO.setNodeOrder(nodeCmd.getNodeOrder());
                nodeDO.setNodeName(nodeCmd.getNodeName());
                nodeDO.setNodeType(nodeCmd.getNodeType());
                nodeDO.setApproverType(nodeCmd.getApproverType());
                nodeDO.setApproverValue(nodeCmd.getApproverValue());
                nodeDO.setOpinionRequired(nodeCmd.getOpinionRequired());
                nodeTemplateMapper.insert(nodeDO);
            }
        }
    }

    @Override
    @Transactional
    public void update(ProcessTemplateUpdateCmd cmd) {
        ProcessTemplateDO templateDO = processTemplateMapper.selectById(cmd.getId());
        if (templateDO == null) {
            throw new RuntimeException("流程模板不存在");
        }
        
        templateDO.setName(cmd.getName());
        templateDO.setDescription(cmd.getDescription());
        templateDO.setFormSchema(cmd.getFormSchema());
        templateDO.setVersion(templateDO.getVersion() + 1);
        processTemplateMapper.updateById(templateDO);
        
        nodeTemplateMapper.deleteByProcessTemplateId(cmd.getId());
        
        if (cmd.getNodes() != null && !cmd.getNodes().isEmpty()) {
            for (NodeTemplateCreateCmd nodeCmd : cmd.getNodes()) {
                NodeTemplateDO nodeDO = new NodeTemplateDO();
                nodeDO.setProcessTemplateId(cmd.getId());
                nodeDO.setNodeOrder(nodeCmd.getNodeOrder());
                nodeDO.setNodeName(nodeCmd.getNodeName());
                nodeDO.setNodeType(nodeCmd.getNodeType());
                nodeDO.setApproverType(nodeCmd.getApproverType());
                nodeDO.setApproverValue(nodeCmd.getApproverValue());
                nodeDO.setOpinionRequired(nodeCmd.getOpinionRequired());
                nodeTemplateMapper.insert(nodeDO);
            }
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        ProcessTemplateDO templateDO = processTemplateMapper.selectById(id);
        if (templateDO != null) {
            templateDO.setStatus(status);
            processTemplateMapper.updateById(templateDO);
        }
    }

    private ProcessTemplateDTO convertToDTO(ProcessTemplateDO dO) {
        ProcessTemplateDTO dto = new ProcessTemplateDTO();
        dto.setId(dO.getId());
        dto.setCode(dO.getCode());
        dto.setName(dO.getName());
        dto.setDescription(dO.getDescription());
        dto.setFormSchema(dO.getFormSchema());
        dto.setStatus(dO.getStatus());
        dto.setStatusName(dO.getStatus() == 1 ? "启用" : "停用");
        dto.setVersion(dO.getVersion());
        if (dO.getCreatedTime() != null) {
            dto.setCreatedTime(dO.getCreatedTime().toEpochSecond(ZoneOffset.ofHours(8)));
        }
        return dto;
    }

    private NodeTemplateDTO convertNodeToDTO(NodeTemplateDO dO) {
        NodeTemplateDTO dto = new NodeTemplateDTO();
        dto.setId(dO.getId());
        dto.setProcessTemplateId(dO.getProcessTemplateId());
        dto.setNodeOrder(dO.getNodeOrder());
        dto.setNodeName(dO.getNodeName());
        dto.setNodeType(dO.getNodeType());
        dto.setNodeTypeName(NodeTypeEnum.getByCode(dO.getNodeType()) != null 
                ? NodeTypeEnum.getByCode(dO.getNodeType()).getName() : "");
        dto.setApproverType(dO.getApproverType());
        dto.setOpinionRequired(dO.getOpinionRequired());
        return dto;
    }
}
