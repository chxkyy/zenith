package com.zenith.admin.api.inst;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstInstitutionCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolAddInstitutionCmd;
import com.zenith.admin.dto.inst.data.InstInstitutionDTO;
import com.zenith.admin.dto.inst.data.SimpleInstitutionDTO;
import com.zenith.admin.dto.inst.qry.InstitutionPageQuery;

/**
 * 合作机构服务接口
 */
public interface InstInstitutionService {

    /**
     * 创建机构
     *
     * @param cmd 创建命令
     * @return 机构ID
     */
    SingleResponse<Long> create(InstInstitutionCreateCmd cmd);

    /**
     * 编辑机构
     *
     * @param cmd 编辑命令
     * @return 是否成功
     */
    SingleResponse<Boolean> update(InstInstitutionCreateCmd cmd);

    /**
     * 分页查询机构列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResponse<SimpleInstitutionDTO> page(InstitutionPageQuery query);

    /**
     * 查询机构详情
     *
     * @param id 机构ID
     * @return 机构详情
     */
    SingleResponse<InstInstitutionDTO> detail(Long id);

    /**
     * 机构入池
     *
     * @param cmd 入池命令
     * @return 是否成功
     */
    SingleResponse<Void> addToPool(InstPoolAddInstitutionCmd cmd);

    /**
     * 机构移出池
     *
     * @param poolId 池ID
     * @param instId 机构ID
     * @return 是否成功
     */
    SingleResponse<Void> removeFromPool(Long poolId, Long instId);
}
