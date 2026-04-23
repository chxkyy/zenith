package com.zenith.admin.web;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.UserService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.data.UserPageQuery;
import com.zenith.admin.dto.data.StatusUpdateQuery;
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
        PageInfo<UserDTO> pageInfo = userService.listByPage(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody UserDTO userDTO) {
        userService.save(userDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/update")
    public com.alibaba.cola.dto.Response update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        userService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<UserDTO> get(@RequestParam Long id) {
        return SingleResponse.of(userService.getById(id));
    }

    @PostMapping("/password")
    public com.alibaba.cola.dto.Response resetPassword(@RequestBody IdQuery query) {
        userService.resetPassword(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/status")
    public com.alibaba.cola.dto.Response changeStatus(@RequestBody StatusUpdateQuery query) {
        userService.changeStatus(query.getId(), query.getStatus());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
