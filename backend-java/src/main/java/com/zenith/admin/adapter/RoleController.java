package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.app.RoleService;
import com.zenith.admin.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @DeleteMapping("/{id}")
    public com.alibaba.cola.dto.Response delete(@PathVariable Long id) {
        roleService.delete(id);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<RoleDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(roleService.getById(id));
    }

    @GetMapping
    public MultiResponse<RoleDTO> list() {
        return roleService.listAll();
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
}
