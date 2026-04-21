package com.zenith.admin.web;

import com.zenith.admin.service.ErrorLogService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.ErrorLogDTO;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/error-logs")
@RequiredArgsConstructor
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    @GetMapping
    public com.alibaba.cola.dto.PageResponse<ErrorLogDTO> list(
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String ip) {
        PageInfo<ErrorLogDTO> pageInfo = errorLogService.listByPage(pageIndex, pageSize, module, ip);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        errorLogService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/clear")
    public com.alibaba.cola.dto.Response clear(@RequestParam(defaultValue = "3") int months) {
        errorLogService.clear(months);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
