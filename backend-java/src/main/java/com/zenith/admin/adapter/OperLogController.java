package com.zenith.admin.adapter;

import com.zenith.admin.app.OperLogService;
import com.zenith.admin.common.utils.PageResponseUtils;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.OperLogDTO;
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
