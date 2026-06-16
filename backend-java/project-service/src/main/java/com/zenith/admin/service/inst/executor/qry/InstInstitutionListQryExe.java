package com.zenith.admin.service.inst.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.inst.data.SimpleInstitutionDTO;
import com.zenith.admin.dto.inst.qry.InstitutionPageQuery;
import com.zenith.admin.dataobject.InstInstitutionDO;
import com.zenith.admin.mapper.InstInstitutionMapper;
import com.zenith.admin.mapper.InstPoolInstitutionMapper;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机构分页列表查询执行器
 */
@Component
@RequiredArgsConstructor
public class InstInstitutionListQryExe {

    private final InstInstitutionMapper instInstitutionMapper;
    private final InstPoolInstitutionMapper instPoolInstitutionMapper;

    /**
     * 执行分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageInfo<SimpleInstitutionDTO> execute(InstitutionPageQuery query) {
        QueryWrapper<InstInstitutionDO> wrapper = new QueryWrapper<>();

        // 如果传了poolId，则JOIN关联表过滤
        if (query.getPoolId() != null) {
            List<Long> institutionIdsByPool = getInstitutionIdsByPoolId(query.getPoolId());
            if (institutionIdsByPool.isEmpty()) {
                wrapper.apply("1 = 0");
            } else {
                wrapper.in("id", institutionIdsByPool);
            }
        }

        // keyword搜索fullName或creditCode（ILIKE）
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like("full_name", query.getKeyword())
                    .or().like("credit_code", query.getKeyword()));
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc("created_time");

        // 分页查询
        PageInfo<InstInstitutionDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> instInstitutionMapper.selectList(wrapper));

        // 转换为DTO列表
        return PageResponseUtils.convert(pageInfo, this::convertToDTOList);
    }

    /**
     * 根据池ID获取关联的机构ID列表
     */
    private List<Long> getInstitutionIdsByPoolId(Long poolId) {
        LambdaQueryWrapper<com.zenith.admin.dataobject.InstPoolInstitutionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.zenith.admin.dataobject.InstPoolInstitutionDO::getPoolId, poolId)
               .select(com.zenith.admin.dataobject.InstPoolInstitutionDO::getInstitutionId);

        List<com.zenith.admin.dataobject.InstPoolInstitutionDO> relations = instPoolInstitutionMapper.selectList(wrapper);
        return relations.stream()
                .map(com.zenith.admin.dataobject.InstPoolInstitutionDO::getInstitutionId)
                .collect(Collectors.toList());
    }

    /**
     * DO列表转换为DTO列表
     */
    private List<SimpleInstitutionDTO> convertToDTOList(List<InstInstitutionDO> doList) {
        if (doList == null || doList.isEmpty()) {
            return new ArrayList<>();
        }

        return doList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 单个DO转换为DTO
     */
    private SimpleInstitutionDTO convertToDTO(InstInstitutionDO institutionDO) {
        SimpleInstitutionDTO dto = new SimpleInstitutionDTO();
        dto.setId(institutionDO.getId());
        dto.setFullName(institutionDO.getFullName());
        dto.setShortName(institutionDO.getShortName());
        dto.setInstType(institutionDO.getInstType());
        dto.setCooperationStatus(institutionDO.getCooperationStatus());
        dto.setCreditCode(institutionDO.getCreditCode());
        return dto;
    }
}
