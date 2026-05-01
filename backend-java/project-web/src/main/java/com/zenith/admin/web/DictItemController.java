package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.DictService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictItemPageQuery;
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
    public com.alibaba.cola.dto.Response save(@RequestBody DictItemDTO dictItemDTO) {
        dictService.saveItem(dictItemDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/update")
    public com.alibaba.cola.dto.Response update(@RequestBody DictItemDTO dictItemDTO) {
        dictService.updateItem(dictItemDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        dictService.deleteItem(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<DictItemDTO> get(@RequestParam Long id) {
        return SingleResponse.of(dictService.getItemById(id));
    }
}
