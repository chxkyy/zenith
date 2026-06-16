package com.zenith.admin.api.inst;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstProductCreateCmd;
import com.zenith.admin.dto.inst.data.InstProductDTO;
import com.zenith.admin.dto.inst.qry.InstProductPageQuery;

/**
 * 产品服务接口
 */
public interface InstProductService {

    /**
     * 创建产品
     *
     * @param cmd 创建命令
     * @return 产品ID
     */
    SingleResponse<Long> create(InstProductCreateCmd cmd);

    /**
     * 编辑产品
     *
     * @param cmd 编辑命令
     * @return 是否成功
     */
    SingleResponse<Boolean> update(InstProductCreateCmd cmd);

    /**
     * 删除产品
     *
     * @param id 产品ID
     * @return 是否成功
     */
    SingleResponse<Boolean> delete(Long id);

    /**
     * 分页查询产品列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResponse<InstProductDTO> page(InstProductPageQuery query);
}
