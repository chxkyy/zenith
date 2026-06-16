package com.zenith.admin.service.inst.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.data.InstPoolDTO;
import com.zenith.admin.dataobject.InstPoolDO;
import com.zenith.admin.dataobject.InstPoolInstitutionDO;
import com.zenith.admin.mapper.InstPoolInstitutionMapper;
import com.zenith.admin.mapper.InstPoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 机构池详情查询执行器
 */
@Component
@RequiredArgsConstructor
public class InstPoolDetailQryExe {

    private final InstPoolMapper instPoolMapper;
    private final InstPoolInstitutionMapper instPoolInstitutionMapper;

    /**
     * 执行详情查询
     *
     * @param id 池ID
     * @return 机构池详情
     */
    public SingleResponse<InstPoolDTO> execute(Long id) {
        // 按ID查询DO
        InstPoolDO poolDO = instPoolMapper.selectById(id);
        if (poolDO == null) {
            throw new RuntimeException("机构池不存在");
        }

        // 关联统计机构数量
        LambdaQueryWrapper<InstPoolInstitutionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InstPoolInstitutionDO::getPoolId, id);
        Long institutionCount = instPoolInstitutionMapper.selectCount(wrapper);

        // 组装为DTO
        InstPoolDTO dto = new InstPoolDTO();
        dto.setId(poolDO.getId());
        dto.setName(poolDO.getName());
        dto.setPoolType(poolDO.getPoolType());
        dto.setDescription(poolDO.getDescription());
        dto.setOwnerId(poolDO.getOwnerId());
        dto.setStatus(poolDO.getStatus());
        dto.setInstitutionCount(institutionCount != null ? institutionCount.intValue() : 0);
        dto.setCreatedTime(poolDO.getCreatedTime());
        dto.setUpdateTime(poolDO.getUpdateTime());

        return SingleResponse.of(dto);
    }
}
