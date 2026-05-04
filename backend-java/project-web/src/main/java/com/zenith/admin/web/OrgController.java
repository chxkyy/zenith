package com.zenith.admin.web;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.OrgService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.OrgAddCmd;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.data.OrgPageQuery;
import com.zenith.admin.dto.data.OrgUpdateCmd;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrgController {

    private final OrgService orgService;

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<OrgDTO> page(@RequestBody @Valid OrgPageQuery query) {
        PageInfo<OrgDTO> pageInfo = orgService.page(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public Response save(@RequestBody @Valid OrgAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        orgService.save(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid OrgUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        orgService.update(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        orgService.delete(query.getId(), currentUserId);
        return Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<OrgDTO> get(@RequestParam Long id) {
        return SingleResponse.of(orgService.getById(id));
    }
}
