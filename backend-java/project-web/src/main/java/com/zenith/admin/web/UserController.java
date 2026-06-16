package com.zenith.admin.web;

import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.dto.Response;
import com.zenith.admin.api.system.UserService;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.system.qry.IdQuery;
import com.zenith.admin.dto.system.cmd.UserAddCmd;
import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.dto.system.qry.UserPageQuery;
import com.zenith.admin.dto.system.cmd.UserUpdateCmd;
import com.zenith.admin.dto.system.qry.StatusUpdateQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<UserDTO> page(@RequestBody @Valid UserPageQuery query) {
        query.setCurrentUserId(UserContext.getUserId());
        PageInfo<UserDTO> pageInfo = userService.listByPage(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public Response save(@RequestBody @Valid UserAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        userService.save(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid UserUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        userService.update(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        userService.delete(query.getId(), currentUserId);
        return Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<UserDTO> get(@RequestParam Long id) {
        return SingleResponse.of(userService.getById(id));
    }

    @PostMapping("/password")
    public Response resetPassword(@RequestBody IdQuery query) {
        userService.resetPassword(query.getId());
        return Response.buildSuccess();
    }

    @PostMapping("/status")
    public Response changeStatus(@RequestBody StatusUpdateQuery query) {
        Long currentUserId = UserContext.getUserId();
        userService.changeStatus(query.getId(), query.getStatus(), currentUserId);
        return Response.buildSuccess();
    }
}
