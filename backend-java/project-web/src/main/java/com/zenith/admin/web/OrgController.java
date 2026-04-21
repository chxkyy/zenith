package com.zenith.admin.web;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.service.OrgService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.data.OrgPageQuery;
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
    public com.alibaba.cola.dto.Response save(@RequestBody OrgDTO orgDTO) {
        orgService.save(orgDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/update")
    public com.alibaba.cola.dto.Response update(@RequestBody OrgDTO orgDTO) {
        orgService.update(orgDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        orgService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<OrgDTO> get(@RequestParam Long id) {
        return SingleResponse.of(orgService.getById(id));
    }
}
