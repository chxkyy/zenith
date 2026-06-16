package com.zenith.admin.web.inst.controller;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstProductCreateCmd;
import com.zenith.admin.dto.inst.data.InstProductDTO;
import com.zenith.admin.dto.inst.qry.InstProductPageQuery;
import com.zenith.admin.api.inst.InstProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 产品管理控制器
 *
 * <p>提供产品的添加、编辑、删除和分页查询功能。</p>
 */
@RestController
@RequestMapping("/api/inst/products")
@RequiredArgsConstructor
public class InstProductController {

    private final InstProductService instProductService;

    /**
     * 添加产品
     *
     * @param cmd 创建命令
     * @return 产品ID
     */
    @PostMapping
    public SingleResponse<Long> create(@RequestBody @Valid InstProductCreateCmd cmd) {
        return instProductService.create(cmd);
    }

    /**
     * 编辑产品
     *
     * @param id  产品ID
     * @param cmd 编辑命令
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public SingleResponse<Boolean> update(@PathVariable Long id, @RequestBody @Valid InstProductCreateCmd cmd) {
        // 将路径参数 id 设置到 cmd 中
        cmd.setId(id);
        return instProductService.update(cmd);
    }

    /**
     * 删除产品
     *
     * @param id 产品ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public SingleResponse<Boolean> delete(@PathVariable Long id) {
        return instProductService.delete(id);
    }

    /**
     * 分页查询产品列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public com.alibaba.cola.dto.PageResponse<InstProductDTO> page(InstProductPageQuery query) {
        return instProductService.page(query);
    }
}
