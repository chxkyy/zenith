package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.DataPermissionDO;
import com.zenith.admin.dto.data.DataPermissionDTO;
import com.zenith.admin.dto.query.DataPermissionQuery;
import com.zenith.admin.mapper.DataPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.IdWorker.getId;

/**
 * 数据权限查询执行器
 * <p>
 * 支持两种查询模式：
 * <ul>
 *   <li>按用户查询：查看某用户负责的所有数据</li>
 *   <li>按数据查询：查看某数据的所有负责人</li>
 * </ul>
 * </p>
 */
@Component
@RequiredArgsConstructor
public class DataPermissionListQryExe {

    private final DataPermissionMapper dataPermissionMapper;

    /**
     * 查询某用户负责的数据权限列表
     *
     * @param query 查询条件（需设置 userId）
     * @return 数据权限 DTO 列表
     */
    public List<DataPermissionDTO> listByUser(DataPermissionQuery query) {
        List<DataPermissionDO> list = dataPermissionMapper.selectList(
                new LambdaQueryWrapper<DataPermissionDO>()
                        .eq(DataPermissionDO::getUserId, query.getUserId())
                        .orderByDesc(DataPermissionDO::getCreatedTime)
        );
        return convertToDTO(list);
    }

    /**
     * 查询某数据的负责人列表
     *
     * @param query 查询条件（需设置 dataType 和 dataId）
     * @return 负责人用户 ID 列表
     */
    public List<Long> listOwnersByData(DataPermissionQuery query) {
        return dataPermissionMapper.selectUserIdsByData(query.getDataType(), query.getDataId());
    }

    /**
     * 检查指定数据是否是某个用户的唯一负责人
     * （用于 PRODUCT.md #18 的唯一负责人警告提示）
     *
     * @param dataType 数据类型
     * @param dataId   数据 ID
     * @return 该数据的负责人总数
     */
    public long countOwnersByData(String dataType, Long dataId) {
        return dataPermissionMapper.selectCount(
                new LambdaQueryWrapper<DataPermissionDO>()
                        .eq(DataPermissionDO::getDataType, dataType)
                        .eq(DataPermissionDO::getDataId, dataId)
        );
    }

    private List<DataPermissionDTO> convertToDTO(List<DataPermissionDO> list) {
        return list.stream().map(ado -> {
            DataPermissionDTO dto = new DataPermissionDTO();
            dto.setId(ado.getId());
            dto.setUserId(ado.getUserId());
            dto.setDataType(ado.getDataType());
            dto.setDataId(ado.getDataId());
            dto.setCreatedTime(ado.getCreatedTime() != null ? ado.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond() : null);
            dto.setUpdateTime(ado.getUpdateTime() != null ? ado.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond() : null);
            return dto;
        }).toList();
    }
}
