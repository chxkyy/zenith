package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.MenuService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.dto.data.MenuUpdateParentCmd;
import com.zenith.admin.dto.data.MenuReorderCmd;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        menuService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<MenuDTO> get(@RequestParam Long id) {
        return SingleResponse.of(menuService.getById(id));
    }

    @GetMapping
    public MultiResponse<MenuDTO> list() {
        List<MenuDTO> list = menuService.listAll();
        return MultiResponse.of(list);
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<MenuDTO> page(@RequestBody @Valid MenuPageQuery query) {
        PageInfo<MenuDTO> pageInfo = menuService.page(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody MenuDTO menuDTO) {
        menuService.save(menuDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody MenuDTO menuDTO) {
        menuService.update(menuDTO);
        return Response.buildSuccess();
    }

    @PostMapping("/update-parent")
    public Response updateParent(@RequestBody MenuUpdateParentCmd cmd) {
        menuService.updateParent(cmd);
        return Response.buildSuccess();
    }

    @PostMapping("/reorder")
    public Response reorder(@RequestBody MenuReorderCmd cmd) {
        menuService.reorder(cmd);
        return Response.buildSuccess();
    }
}
