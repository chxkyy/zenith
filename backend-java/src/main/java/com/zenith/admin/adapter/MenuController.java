package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.app.MenuService;
import com.zenith.admin.common.utils.PageResponseUtils;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.MenuDTO;
import com.zenith.admin.dto.MenuPageQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return menuService.listAll();
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
    public com.alibaba.cola.dto.Response update(@RequestBody MenuDTO menuDTO) {
        menuService.update(menuDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
