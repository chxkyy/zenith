package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.app.MenuService;
import com.zenith.admin.dto.MenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @DeleteMapping("/{id}")
    public com.alibaba.cola.dto.Response delete(@PathVariable Long id) {
        menuService.delete(id);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<MenuDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(menuService.getById(id));
    }

    @GetMapping
    public MultiResponse<MenuDTO> list() {
        return menuService.listAll();
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody MenuDTO menuDTO) {
        menuService.save(menuDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PutMapping
    public com.alibaba.cola.dto.Response update(@RequestBody MenuDTO menuDTO) {
        menuService.update(menuDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
