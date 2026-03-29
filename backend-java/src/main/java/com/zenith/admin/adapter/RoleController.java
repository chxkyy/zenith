package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.RoleService;
import com.zenith.admin.dto.RoleDTO;
import com.zenith.admin.dto.RolePageQuery;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public MultiResponse<RoleDTO> list() {
        return roleService.listAll();
    }

    @PostMapping("/page")
    public PageResponse<RoleDTO> page(@RequestBody @Valid RolePageQuery query) {
        return roleService.listByPage(query);
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody RoleDTO roleDTO) {
        roleService.save(roleDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PutMapping
    public com.alibaba.cola.dto.Response update(@RequestBody RoleDTO roleDTO) {
        roleService.update(roleDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @DeleteMapping
    public com.alibaba.cola.dto.Response delete(@RequestParam Long roleId) {
        roleService.delete(roleId);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/detail")
    public com.alibaba.cola.dto.SingleResponse<RoleDTO> get(@RequestParam Long roleId) {
        return com.alibaba.cola.dto.SingleResponse.of(roleService.getById(roleId));
    }

    @PutMapping("/status")
    public com.alibaba.cola.dto.Response changeStatus(@RequestParam Long roleId, @RequestParam Integer status) {
        roleService.changeStatus(roleId, status);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}