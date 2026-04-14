package com.zenith.admin.adapter;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.app.UserService;
import com.zenith.admin.common.utils.PageResponseUtils;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.UserDTO;
import com.zenith.admin.dto.UserPageQuery;
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

    @GetMapping
    public SingleResponse<UserDTO> get(@RequestParam Long id) {
        return SingleResponse.of(userService.getById(id));
    }

    @PostMapping("/password")
    public com.alibaba.cola.dto.Response resetPassword(@RequestBody IdQuery query) {
        userService.resetPassword(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/status")
    public com.alibaba.cola.dto.Response changeStatus(@RequestBody IdQuery query, @RequestParam Integer status) {
        userService.changeStatus(query.getId(), status);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
