package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.RoleService;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.RoleDTO;
import com.zenith.admin.dto.RolePageQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

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

    @PostMapping("/update")
    public com.alibaba.cola.dto.Response update(@RequestBody RoleDTO roleDTO) {
        roleService.update(roleDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        roleService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<RoleDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(roleService.getById(id));
    }

    @PostMapping("/status")
    public com.alibaba.cola.dto.Response changeStatus(@RequestBody IdQuery query, @RequestParam Integer status) {
        roleService.changeStatus(query.getId(), status);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
