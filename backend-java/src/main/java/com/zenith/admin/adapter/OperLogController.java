package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.zenith.admin.app.OperLogService;
import com.zenith.admin.dto.OperLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs/oper")
public class OperLogController {

    @Autowired
    private OperLogService operLogService;

    @GetMapping
    public PageResponse<OperLogDTO> list(@RequestParam(defaultValue = "1") int pageIndex,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(required = false) String operator,
                                         @RequestParam(required = false) String module,
                                         @RequestParam(required = false) String result) {
        return operLogService.listByPage(pageIndex, pageSize, operator, module, result);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        operLogService.delete(id);
        return Response.buildSuccess();
    }
}
