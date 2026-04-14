package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.DictService;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.DictDTO;
import com.zenith.admin.dto.DictPageQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

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

    @PostMapping("/update")
    public com.alibaba.cola.dto.Response update(@RequestBody DictDTO dictDTO) {
        dictService.update(dictDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        dictService.delete(query.getId());
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
