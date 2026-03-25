package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.zenith.admin.app.ErrorLogService;
import com.zenith.admin.dto.ErrorLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs/error")
public class ErrorLogController {

    @Autowired
    private ErrorLogService errorLogService;

    @GetMapping
    public PageResponse<ErrorLogDTO> list(@RequestParam(defaultValue = "1") int pageIndex,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(required = false) String module,
                                          @RequestParam(required = false) String ip) {
        return errorLogService.listByPage(pageIndex, pageSize, module, ip);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        errorLogService.delete(id);
        return Response.buildSuccess();
    }

    @PostMapping("/clear")
    public Response clear(@RequestParam(defaultValue = "3") int months) {
        errorLogService.clear(months);
        return Response.buildSuccess();
    }
}
