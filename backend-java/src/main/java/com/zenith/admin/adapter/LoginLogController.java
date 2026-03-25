package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.zenith.admin.app.LoginLogService;
import com.zenith.admin.dto.LoginLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs/login")
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;

    @GetMapping
    public PageResponse<LoginLogDTO> list(@RequestParam(defaultValue = "1") int pageIndex,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(required = false) String username,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String ip) {
        return loginLogService.listByPage(pageIndex, pageSize, username, status, ip);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        loginLogService.delete(id);
        return Response.buildSuccess();
    }
}
