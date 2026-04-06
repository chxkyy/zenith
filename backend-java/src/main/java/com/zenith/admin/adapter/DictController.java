package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.DictService;
import com.zenith.admin.dto.DictDTO;
import com.zenith.admin.dto.DictPageQuery;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dicts")
public class DictController {

    @Autowired
    private DictService dictService;

    @GetMapping("/list")
    public MultiResponse<DictDTO> list() {
        return dictService.listAll();
    }

    @GetMapping("/type/{type}")
    public MultiResponse<DictDTO> listByType(@PathVariable String type) {
        return dictService.listByType(type);
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody DictDTO dictDTO) {
        dictService.save(dictDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PutMapping
    public com.alibaba.cola.dto.Response update(@RequestBody DictDTO dictDTO) {
        dictService.update(dictDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @DeleteMapping
    public com.alibaba.cola.dto.Response delete(@RequestParam Long id) {
        dictService.delete(id);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public com.alibaba.cola.dto.SingleResponse<DictDTO> get(@RequestParam Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(dictService.getById(id));
    }

    @PostMapping("/page")
    public PageResponse<DictDTO> page(@RequestBody @Valid DictPageQuery query) {
        return dictService.page(query);
    }
}
