package com.zenith.admin.api.inst;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstPoolCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolStatusCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolUpdateCmd;
import com.zenith.admin.dto.inst.data.InstPoolDTO;
import com.zenith.admin.dto.inst.qry.InstPoolPageQuery;

/**
 * 机构池服务接口
 */
public interface InstPoolService {

    /**
     * 创建机构池
     *
     * @param cmd 创建命令
     * @return 池ID
     */
    SingleResponse<Long> create(InstPoolCreateCmd cmd);

    /**
     * 编辑机构池
     *
     * @param cmd 编辑命令
     * @return 是否成功
     */
    SingleResponse<Boolean> update(InstPoolUpdateCmd cmd);

    /**
     * 启用/停用机构池
     *
     * @param cmd 状态命令
     * @return 是否成功
     */
    SingleResponse<Boolean> toggleStatus(InstPoolStatusCmd cmd);

    /**
     * 删除空池（仅当池内无机构时允许删除）
     *
     * @param poolId 池ID
     * @return 是否成功
     */
    SingleResponse<Boolean> deleteIfEmpty(Long poolId);

    /**
     * 分页查询机构池列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResponse<InstPoolDTO> page(InstPoolPageQuery query);

    /**
     * 查询机构池详情
     *
     * @param id 池ID
     * @return 机构池详情
     */
    SingleResponse<InstPoolDTO> detail(Long id);
}
