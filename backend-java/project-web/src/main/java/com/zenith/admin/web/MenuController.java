package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.MenuService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.MenuAddCmd;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.dto.data.MenuToggleStatusCmd;
import com.zenith.admin.dto.data.MenuUpdateCmd;
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
    public Response delete(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        menuService.delete(query.getId(), currentUserId);
        return Response.buildSuccess();
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
    public Response save(@RequestBody @Valid MenuAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        menuService.save(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid MenuUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        menuService.update(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update-parent")
    public Response updateParent(@RequestBody MenuUpdateParentCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        menuService.updateParent(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/reorder")
    public Response reorder(@RequestBody MenuReorderCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        menuService.reorder(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/toggle-status")
    public Response toggleStatus(@RequestBody @Valid MenuToggleStatusCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        menuService.toggleStatus(cmd, currentUserId);
        return Response.buildSuccess();
    }
}
