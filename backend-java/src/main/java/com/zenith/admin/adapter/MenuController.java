package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.MenuService;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.MenuDTO;
import com.zenith.admin.dto.MenuPageQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public MultiResponse<MenuDTO> list() {
        return menuService.listAll();
    }

    @PostMapping("/page")
    public PageResponse<MenuDTO> page(@RequestBody @Valid MenuPageQuery query) {
        return menuService.page(query);
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

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        menuService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<MenuDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(menuService.getById(id));
    }
}
