package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.LoginLogService;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.LoginLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login-logs")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    @GetMapping
    public PageResponse<LoginLogDTO> list(
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ip) {
        return loginLogService.listByPage(pageIndex, pageSize, username, status, ip);
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        loginLogService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
