package com.zenith.admin.service.inst.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.inst.data.InstPoolDTO;
import com.zenith.admin.dto.inst.qry.InstPoolPageQuery;
import com.zenith.admin.dataobject.InstPoolDO;
import com.zenith.admin.mapper.InstPoolMapper;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机构池分页列表查询执行器
 */
@Component
@RequiredArgsConstructor
public class InstPoolListQryExe {

    private final InstPoolMapper instPoolMapper;

    /**
     * 执行分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageInfo<InstPoolDTO> execute(InstPoolPageQuery query) {
        // 动态构建查询条件
        LambdaQueryWrapper<InstPoolDO> wrapper = new LambdaQueryWrapper<>();

        // 名称模糊搜索
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(InstPoolDO::getName, query.getName());
        }

        // 池类型筛选
        if (StringUtils.hasText(query.getPoolType())) {
            wrapper.eq(InstPoolDO::getPoolType, query.getPoolType());
        }

        // 状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(InstPoolDO::getStatus, query.getStatus());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(InstPoolDO::getCreatedTime);

        // 分页查询
        PageInfo<InstPoolDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> instPoolMapper.selectList(wrapper));

        // 转换为DTO列表
        return PageResponseUtils.convert(pageInfo, this::convertToDTOList);
    }

    /**
     * DO列表转换为DTO列表
     */
    private List<InstPoolDTO> convertToDTOList(List<InstPoolDO> doList) {
        if (doList == null || doList.isEmpty()) {
            return new ArrayList<>();
        }

        return doList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 单个DO转换为DTO
     */
    private InstPoolDTO convertToDTO(InstPoolDO poolDO) {
        InstPoolDTO dto = new InstPoolDTO();
        dto.setId(poolDO.getId());
        dto.setName(poolDO.getName());
        dto.setPoolType(poolDO.getPoolType());
        dto.setDescription(poolDO.getDescription());
        dto.setOwnerId(poolDO.getOwnerId());
        dto.setStatus(poolDO.getStatus());
        dto.setCreatedTime(poolDO.getCreatedTime());
        dto.setUpdateTime(poolDO.getUpdateTime());
        return dto;
    }
}
