package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.OrgService;
import com.zenith.admin.dto.IdQuery;
import com.zenith.admin.dto.OrgDTO;
import com.zenith.admin.dto.OrgPageQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrgController {

    private final OrgService orgService;

    @PostMapping("/page")
    public PageResponse<OrgDTO> page(@RequestBody @Valid OrgPageQuery query) {
        return orgService.page(query);
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

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<OrgDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(orgService.getById(id));
    }
}
