package com.zenith.admin.web.inst.controller;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstInstitutionCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolAddInstitutionCmd;
import com.zenith.admin.dto.inst.data.InstInstitutionDTO;
import com.zenith.admin.dto.inst.data.SimpleInstitutionDTO;
import com.zenith.admin.dto.inst.qry.InstitutionPageQuery;
import com.zenith.admin.api.inst.InstInstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 合作机构管理控制器
 *
 * <p>提供机构的创建、编辑、查询、入池和移出池等功能。</p>
 */
@RestController
@RequestMapping("/api/inst/institutions")
@RequiredArgsConstructor
public class InstInstitutionController {

    private final InstInstitutionService instInstitutionService;

    /**
     * 新建机构
     *
     * @param cmd 创建命令
     * @return 机构ID
     */
    @PostMapping
    public SingleResponse<Long> create(@RequestBody @Valid InstInstitutionCreateCmd cmd) {
        return instInstitutionService.create(cmd);
    }

    /**
     * 编辑机构
     *
     * @param cmd 编辑命令
     * @return 是否成功
     */
    @PutMapping
    public SingleResponse<Boolean> update(@RequestBody @Valid InstInstitutionCreateCmd cmd) {
        return instInstitutionService.update(cmd);
    }

    /**
     * 分页查询机构列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public com.alibaba.cola.dto.PageResponse<SimpleInstitutionDTO> page(InstitutionPageQuery query) {
        return instInstitutionService.page(query);
    }

    /**
     * 查询机构详情
     *
     * @param id 机构ID
     * @return 机构详情
     */
    @GetMapping("/{id}")
    public SingleResponse<InstInstitutionDTO> detail(@PathVariable Long id) {
        return instInstitutionService.detail(id);
    }

    /**
     * 机构入池操作
     *
     * <p>将指定机构添加到目标机构池中。</p>
     *
     * @param cmd 入池命令（包含 poolId 和 instId）
     * @return 是否成功
     */
    @PostMapping("/add-to-pool")
    public SingleResponse<Void> addToPool(@RequestBody @Valid InstPoolAddInstitutionCmd cmd) {
        return instInstitutionService.addToPool(cmd);
    }

    /**
     * 机构移出池操作
     *
     * <p>将指定机构从目标机构池中移除。</p>
     *
     * @param poolId 池ID
     * @param instId 机构ID
     * @return 是否成功
     */
    @DeleteMapping("/remove-from-pool")
    public SingleResponse<Void> removeFromPool(@RequestParam Long poolId, @RequestParam Long instId) {
        return instInstitutionService.removeFromPool(poolId, instId);
    }
}
