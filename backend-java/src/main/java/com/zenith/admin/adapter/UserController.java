package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.UserService;
import com.zenith.admin.dto.UserDTO;
import com.zenith.admin.dto.UserPageQuery;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/page")
    public PageResponse<UserDTO> page(@RequestBody @Valid UserPageQuery query) {
        return userService.listByPage(query);
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody UserDTO userDTO) {
        userService.save(userDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PutMapping
    public com.alibaba.cola.dto.Response update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @DeleteMapping("/{id}")
    public com.alibaba.cola.dto.Response delete(@PathVariable Long id) {
        userService.delete(id);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<UserDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(userService.getById(id));
    }
}
