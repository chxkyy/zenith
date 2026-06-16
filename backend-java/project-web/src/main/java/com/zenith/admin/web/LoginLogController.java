package com.zenith.admin.web;

import com.zenith.admin.api.system.LoginLogService;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.dto.system.qry.IdQuery;
import com.zenith.admin.dto.system.data.LoginLogDTO;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login-logs")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    @GetMapping
    public com.alibaba.cola.dto.PageResponse<LoginLogDTO> list(
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ip) {
        PageInfo<LoginLogDTO> pageInfo = loginLogService.listByPage(pageIndex, pageSize, username, status, ip);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        loginLogService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
