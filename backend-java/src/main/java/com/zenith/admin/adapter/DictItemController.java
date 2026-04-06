package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.DictService;
import com.zenith.admin.dto.DictItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dict/items")
public class DictItemController {

    @Autowired
    private DictService dictService;

    @GetMapping("/list")
    public MultiResponse<DictItemDTO> list(@RequestParam String type) {
        return dictService.listItemsByType(type);
    }

    @GetMapping("/page")
    public PageResponse<DictItemDTO> page(
            @RequestParam String type,
            @RequestParam Integer pageIndex,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return dictService.pageItems(type, pageIndex, pageSize, keyword);
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody DictItemDTO dictItemDTO) {
        dictService.saveItem(dictItemDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PutMapping
    public com.alibaba.cola.dto.Response update(@RequestBody DictItemDTO dictItemDTO) {
        dictService.updateItem(dictItemDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @DeleteMapping
    public com.alibaba.cola.dto.Response delete(@RequestParam Long id) {
        dictService.deleteItem(id);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public com.alibaba.cola.dto.SingleResponse<DictItemDTO> get(@RequestParam Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(dictService.getItemById(id));
    }
}
