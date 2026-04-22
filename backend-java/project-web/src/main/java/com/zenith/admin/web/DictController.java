package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.DictService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictPageQuery;
import com.github.pagehelper.PageInfo;
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

    @GetMapping("/list-by-type")
    public MultiResponse<DictDTO> listByType(@RequestParam String type) {
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
    public SingleResponse<DictDTO> get(@RequestParam Long id) {
        return SingleResponse.of(dictService.getById(id));
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<DictDTO> page(@RequestBody @Valid DictPageQuery query) {
        PageInfo<DictDTO> pageInfo = dictService.page(query);
        return PageResponseUtils.of(pageInfo);
    }
}
