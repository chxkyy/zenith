package com.zenith.admin.service.inst.executor.qry;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.data.InstInstitutionDTO;
import com.zenith.admin.dto.inst.data.SimplePoolDTO;
import com.zenith.admin.dataobject.InstInstitutionDO;
import com.zenith.admin.dataobject.InstPoolDO;
import com.zenith.admin.dataobject.InstPoolInstitutionDO;
import com.zenith.admin.dataobject.InstProductDO;
import com.zenith.admin.mapper.InstInstitutionMapper;
import com.zenith.admin.mapper.InstPoolInstitutionMapper;
import com.zenith.admin.mapper.InstPoolMapper;
import com.zenith.admin.mapper.InstProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 机构详情查询执行器
 */
@Component
@RequiredArgsConstructor
public class InstInstitutionDetailQryExe {

    private final InstInstitutionMapper instInstitutionMapper;
    private final InstPoolInstitutionMapper instPoolInstitutionMapper;
    private final InstPoolMapper instPoolMapper;
    private final InstProductMapper instProductMapper;

    /**
     * 执行详情查询
     *
     * @param id 机构ID
     * @return 机构详情
     */
    public SingleResponse<InstInstitutionDTO> execute(Long id) {
        // 查询机构基本信息
        InstInstitutionDO institutionDO = instInstitutionMapper.selectById(id);
        if (institutionDO == null) {
            throw new RuntimeException("机构不存在");
        }

        // 查询所属池列表（通过PoolInstitutionMapper）
        List<SimplePoolDTO> pools = getPoolsByInstitutionId(id);

        // 统计产品数量
        Long productCount = countProductsByInstitutionId(id);

        // 组装为DTO
        InstInstitutionDTO dto = new InstInstitutionDTO();
        dto.setId(institutionDO.getId());
        dto.setFullName(institutionDO.getFullName());
        dto.setShortName(institutionDO.getShortName());
        dto.setCreditCode(institutionDO.getCreditCode());
        dto.setInstType(institutionDO.getInstType());
        dto.setEstablishDate(institutionDO.getEstablishDate());
        dto.setRegisteredCapital(institutionDO.getRegisteredCapital());
        dto.setLegalRepresentative(institutionDO.getLegalRepresentative());
        dto.setRegisteredAddress(institutionDO.getRegisteredAddress());
        dto.setContactPhone(institutionDO.getContactPhone());
        dto.setContactEmail(institutionDO.getContactEmail());
        dto.setLogoUrl(institutionDO.getLogoUrl());
        dto.setCooperationStatus(institutionDO.getCooperationStatus());
        dto.setCreatedTime(institutionDO.getCreatedTime());
        dto.setUpdateTime(institutionDO.getUpdateTime());
        dto.setCreateUserId(institutionDO.getCreateUserId());
        dto.setUpdateUserId(institutionDO.getUpdateUserId());
        dto.setPools(pools);
        dto.setProductCount(productCount != null ? productCount.intValue() : 0);

        return SingleResponse.of(dto);
    }

    /**
     * 根据机构ID获取所属池列表
     */
    private List<SimplePoolDTO> getPoolsByInstitutionId(Long institutionId) {
        // 查询关联记录
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InstPoolInstitutionDO> relationWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        relationWrapper.eq(InstPoolInstitutionDO::getInstitutionId, institutionId);
        List<InstPoolInstitutionDO> relations = instPoolInstitutionMapper.selectList(relationWrapper);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有池ID
        List<Long> poolIds = relations.stream()
                .map(InstPoolInstitutionDO::getPoolId)
                .collect(Collectors.toList());

        // 批量查询池信息
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InstPoolDO> poolWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        poolWrapper.in(InstPoolDO::getId, poolIds);
        List<InstPoolDO> pools = instPoolMapper.selectList(poolWrapper);

        // 构建池ID到池信息的映射
        Map<Long, InstPoolDO> poolMap = pools.stream()
                .collect(Collectors.toMap(InstPoolDO::getId, p -> p));

        // 转换为SimplePoolDTO列表
        return relations.stream()
                .map(relation -> {
                    InstPoolDO poolDO = poolMap.get(relation.getPoolId());
                    if (poolDO == null) {
                        return null;
                    }
                    SimplePoolDTO dto = new SimplePoolDTO();
                    dto.setId(poolDO.getId());
                    dto.setName(poolDO.getName());
                    dto.setPoolType(poolDO.getPoolType());
                    return dto;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * 根据机构ID统计产品数量
     */
    private Long countProductsByInstitutionId(Long institutionId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InstProductDO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(InstProductDO::getInstitutionId, institutionId);
        return instProductMapper.selectCount(wrapper);
    }
}
