package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.DictService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.data.DictItemAddCmd;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictItemPageQuery;
import com.zenith.admin.dto.data.DictItemUpdateCmd;
import com.zenith.admin.dto.data.IdQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict/items")
@RequiredArgsConstructor
public class DictItemController {

    private final DictService dictService;

    @GetMapping("/list")
    public MultiResponse<DictItemDTO> list(@RequestParam String type) {
        List<DictItemDTO> list = dictService.listItemsByType(type);
        return MultiResponse.of(list);
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<DictItemDTO> page(@RequestBody @Valid DictItemPageQuery query) {
        PageInfo<DictItemDTO> pageInfo = dictService.pageItems(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public Response save(@RequestBody @Valid DictItemAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        dictService.saveItem(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid DictItemUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        dictService.updateItem(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody IdQuery query) {
        dictService.deleteItem(query.getId());
        return Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<DictItemDTO> get(@RequestParam Long id) {
        return SingleResponse.of(dictService.getItemById(id));
    }
}
