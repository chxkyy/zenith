package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.FunctionService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.dto.data.FunctionPageQuery;
import com.zenith.admin.dto.data.IdQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/functions")
@RequiredArgsConstructor
public class FunctionController {

    private final FunctionService functionService;

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        functionService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<FunctionDTO> get(@RequestParam Long id) {
        return SingleResponse.of(functionService.getById(id));
    }

    @GetMapping("/list")
    public MultiResponse<FunctionDTO> list(@RequestParam Long menuId) {
        return functionService.listByMenuId(menuId);
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<FunctionDTO> page(@RequestBody @Valid FunctionPageQuery query) {
        PageInfo<FunctionDTO> pageInfo = functionService.page(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody FunctionDTO functionDTO) {
        functionService.save(functionDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/update")
    public com.alibaba.cola.dto.Response update(@RequestBody FunctionDTO functionDTO) {
        functionService.update(functionDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
