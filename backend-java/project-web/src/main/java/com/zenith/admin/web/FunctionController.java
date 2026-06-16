package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.system.FunctionService;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.system.cmd.FunctionAddCmd;
import com.zenith.admin.dto.system.data.FunctionDTO;
import com.zenith.admin.dto.system.qry.FunctionPageQuery;
import com.zenith.admin.dto.system.cmd.FunctionUpdateCmd;
import com.zenith.admin.dto.system.qry.IdQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/functions")
@RequiredArgsConstructor
public class FunctionController {

    private final FunctionService functionService;

    @PostMapping("/delete")
    public Response delete(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        functionService.delete(query.getId(), currentUserId);
        return Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<FunctionDTO> get(@RequestParam Long id) {
        return SingleResponse.of(functionService.getById(id));
    }

    @GetMapping("/list")
    public MultiResponse<FunctionDTO> list(@RequestParam Long menuId) {
        List<FunctionDTO> list = functionService.listByMenuId(menuId);
        return MultiResponse.of(list);
    }

    @GetMapping("/list-all")
    public MultiResponse<FunctionDTO> listAll() {
        List<FunctionDTO> list = functionService.listAll();
        return MultiResponse.of(list);
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<FunctionDTO> page(@RequestBody @Valid FunctionPageQuery query) {
        PageInfo<FunctionDTO> pageInfo = functionService.page(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public Response save(@RequestBody @Valid FunctionAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        functionService.save(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid FunctionUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        functionService.update(cmd, currentUserId);
        return Response.buildSuccess();
    }
}
