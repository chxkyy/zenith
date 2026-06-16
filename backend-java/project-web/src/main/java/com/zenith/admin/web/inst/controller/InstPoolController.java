package com.zenith.admin.web.inst.controller;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstPoolCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolStatusCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolUpdateCmd;
import com.zenith.admin.dto.inst.data.InstPoolDTO;
import com.zenith.admin.dto.inst.qry.InstPoolPageQuery;
import com.zenith.admin.api.inst.InstPoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 机构池管理控制器
 *
 * <p>提供机构池的创建、编辑、状态管理、删除和查询功能。</p>
 */
@RestController
@RequestMapping("/api/inst/pools")
@RequiredArgsConstructor
public class InstPoolController {

    private final InstPoolService instPoolService;

    /**
     * 创建机构池
     *
     * @param cmd 创建命令
     * @return 池ID
     */
    @PostMapping
    public SingleResponse<Long> create(@RequestBody @Valid InstPoolCreateCmd cmd) {
        return instPoolService.create(cmd);
    }

    /**
     * 编辑机构池
     *
     * @param cmd 编辑命令
     * @return 是否成功
     */
    @PutMapping
    public SingleResponse<Boolean> update(@RequestBody @Valid InstPoolUpdateCmd cmd) {
        return instPoolService.update(cmd);
    }

    /**
     * 启用/停用机构池
     *
     * @param cmd 状态命令
     * @return 是否成功
     */
    @PostMapping("/status")
    public SingleResponse<Boolean> toggleStatus(@RequestBody @Valid InstPoolStatusCmd cmd) {
        return instPoolService.toggleStatus(cmd);
    }

    /**
     * 删除空池（仅当池内无机构时允许删除）
     *
     * @param poolId 池ID
     * @return 是否成功
     */
    @DeleteMapping("/{poolId}")
    public SingleResponse<Boolean> deleteIfEmpty(@PathVariable Long poolId) {
        return instPoolService.deleteIfEmpty(poolId);
    }

    /**
     * 分页查询机构池列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public com.alibaba.cola.dto.PageResponse<InstPoolDTO> page(InstPoolPageQuery query) {
        return instPoolService.page(query);
    }

    /**
     * 查询机构池详情
     *
     * @param id 池ID
     * @return 机构池详情
     */
    @GetMapping("/{id}")
    public SingleResponse<InstPoolDTO> detail(@PathVariable Long id) {
        return instPoolService.detail(id);
    }
}
