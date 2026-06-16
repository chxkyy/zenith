package com.zenith.admin.service.inst.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.inst.data.InstProductDTO;
import com.zenith.admin.dto.inst.qry.InstProductPageQuery;
import com.zenith.admin.dataobject.InstInstitutionDO;
import com.zenith.admin.dataobject.InstProductDO;
import com.zenith.admin.mapper.InstInstitutionMapper;
import com.zenith.admin.mapper.InstProductMapper;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品分页列表查询执行器
 */
@Component
@RequiredArgsConstructor
public class InstProductListQryExe {

    private final InstProductMapper instProductMapper;
    private final InstInstitutionMapper instInstitutionMapper;

    /**
     * 执行分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageInfo<InstProductDTO> execute(InstProductPageQuery query) {
        // 动态构建查询条件
        LambdaQueryWrapper<InstProductDO> wrapper = new LambdaQueryWrapper<>();

        // 按机构ID筛选
        if (query.getInstitutionId() != null) {
            wrapper.eq(InstProductDO::getInstitutionId, query.getInstitutionId());
        }

        // 按合作状态筛选
        if (StringUtils.hasText(query.getCooperationStatus())) {
            wrapper.eq(InstProductDO::getCooperationStatus, query.getCooperationStatus());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(InstProductDO::getCreatedTime);

        // 分页查询
        PageInfo<InstProductDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> instProductMapper.selectList(wrapper));

        // 转换为DTO列表（包含机构名称）
        return PageResponseUtils.convert(pageInfo, doList -> convertToDTOList(doList));
    }

    /**
     * DO列表转换为DTO列表
     */
    private List<InstProductDTO> convertToDTOList(List<InstProductDO> doList) {
        if (doList == null || doList.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量获取机构名称映射
        List<Long> institutionIds = doList.stream()
                .map(InstProductDO::getInstitutionId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> institutionNameMap = getInstitutionNameMap(institutionIds);

        // 转换为DTO
        return doList.stream().map(productDO -> {
            InstProductDTO dto = new InstProductDTO();
            dto.setId(productDO.getId());
            dto.setInstitutionId(productDO.getInstitutionId());
            dto.setInstitutionName(institutionNameMap.getOrDefault(productDO.getInstitutionId(), ""));
            dto.setProductName(productDO.getProductName());
            dto.setProductCode(productDO.getProductCode());
            dto.setProductType(productDO.getProductType());
            dto.setCooperationStatus(productDO.getCooperationStatus());
            dto.setCooperationStartDate(productDO.getCooperationStartDate());
            dto.setEndDate(productDO.getEndDate());
            dto.setContactPerson(productDO.getContactPerson());
            dto.setCreatedTime(productDO.getCreatedTime());
            dto.setUpdateTime(productDO.getUpdateTime());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取机构ID到名称的映射
     */
    private Map<Long, String> getInstitutionNameMap(List<Long> institutionIds) {
        if (institutionIds == null || institutionIds.isEmpty()) {
            return Map.of();
        }

        LambdaQueryWrapper<InstInstitutionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(InstInstitutionDO::getId, institutionIds)
               .select(InstInstitutionDO::getId, InstInstitutionDO::getFullName);

        List<InstInstitutionDO> institutions = instInstitutionMapper.selectList(wrapper);
        return institutions.stream()
                .collect(Collectors.toMap(InstInstitutionDO::getId, InstInstitutionDO::getFullName));
    }
}
