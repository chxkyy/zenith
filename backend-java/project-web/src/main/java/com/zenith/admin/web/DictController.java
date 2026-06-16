package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.system.DictService;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.system.cmd.DictAddCmd;
import com.zenith.admin.dto.system.data.DictDTO;
import com.zenith.admin.dto.system.cmd.DictItemAddCmd;
import com.zenith.admin.dto.system.data.DictItemDTO;
import com.zenith.admin.dto.system.qry.DictItemPageQuery;
import com.zenith.admin.dto.system.cmd.DictItemUpdateCmd;
import com.zenith.admin.dto.system.qry.DictPageQuery;
import com.zenith.admin.dto.system.cmd.DictUpdateCmd;
import com.zenith.admin.dto.system.qry.IdQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {
    private final DictService dictService;

    @PostMapping("/delete")
    public Response delete(@RequestBody IdQuery query) {
        dictService.delete(query.getId());
        return Response.buildSuccess();
    }

    @PostMapping("/items/delete")
    public Response deleteItem(@RequestBody IdQuery query) {
        dictService.deleteItem(query.getId());
        return Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<DictDTO> get(@RequestParam Long id) {
        return SingleResponse.of(dictService.getById(id));
    }

    @GetMapping("/items/get")
    public SingleResponse<DictItemDTO> getItemById(@RequestParam Long id) {
        return SingleResponse.of(dictService.getItemById(id));
    }

    @GetMapping("/list")
    public MultiResponse<DictDTO> list() {
        List<DictDTO> list = dictService.listAll();
        return MultiResponse.of(list);
    }

    @GetMapping("/list-by-type")
    public MultiResponse<DictDTO> listByType(@RequestParam String type) {
        List<DictDTO> list = dictService.listByType(type);
        return MultiResponse.of(list);
    }

    @GetMapping("/items/list-by-type")
    public MultiResponse<DictItemDTO> listItemsByType(@RequestParam String type) {
        List<DictItemDTO> list = dictService.listItemsByType(type);
        return MultiResponse.of(list);
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<DictDTO> page(@RequestBody @Valid DictPageQuery query) {
        PageInfo<DictDTO> pageInfo = dictService.page(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/items/page")
    public com.alibaba.cola.dto.PageResponse<DictItemDTO> pageItems(@RequestBody @Valid DictItemPageQuery query) {
        PageInfo<DictItemDTO> pageInfo = dictService.pageItems(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public Response save(@RequestBody @Valid DictAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        dictService.save(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/items/save")
    public Response saveItem(@RequestBody @Valid DictItemAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        dictService.saveItem(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid DictUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        dictService.update(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/items/update")
    public Response updateItem(@RequestBody @Valid DictItemUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        dictService.updateItem(cmd, currentUserId);
        return Response.buildSuccess();
    }
}
