package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.OperLogService;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.OperLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oper-logs")
@RequiredArgsConstructor
public class OperLogController {

    private final OperLogService operLogService;

    @GetMapping
    public PageResponse<OperLogDTO> list(
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String result) {
        return operLogService.listByPage(pageIndex, pageSize, operator, module, result);
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        operLogService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
