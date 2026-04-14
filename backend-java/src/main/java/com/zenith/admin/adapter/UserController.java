package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.UserService;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.UserDTO;
import com.zenith.admin.dto.UserPageQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/page")
    public PageResponse<UserDTO> page(@RequestBody @Valid UserPageQuery query) {
        return userService.listByPage(query);
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

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<UserDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(userService.getById(id));
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
