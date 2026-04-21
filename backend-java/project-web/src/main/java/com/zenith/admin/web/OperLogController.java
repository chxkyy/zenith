package com.zenith.admin.web;

import com.zenith.admin.service.OperLogService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.OperLogDTO;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oper-logs")
@RequiredArgsConstructor
public class OperLogController {

    private final OperLogService operLogService;

    @GetMapping
    public com.alibaba.cola.dto.PageResponse<OperLogDTO> list(
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String result) {
        PageInfo<OperLogDTO> pageInfo = operLogService.listByPage(pageIndex, pageSize, operator, module, result);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        operLogService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
