package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.service.RoleService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.dataobject.IdQuery;
import com.zenith.admin.dto.dataobject.RoleDTO;
import com.zenith.admin.dto.dataobject.RolePageQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    public MultiResponse<RoleDTO> list() {
        return roleService.listAll();
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<RoleDTO> page(@RequestBody @Valid RolePageQuery query) {
        PageInfo<RoleDTO> pageInfo = roleService.listByPage(query);
        return PageResponseUtils.of(pageInfo);
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

    @GetMapping("/get")
    public SingleResponse<RoleDTO> get(@RequestParam Long id) {
        return SingleResponse.of(roleService.getById(id));
    }

    @PostMapping("/status")
    public com.alibaba.cola.dto.Response changeStatus(@RequestBody IdQuery query, @RequestParam Integer status) {
        roleService.changeStatus(query.getId(), status);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
