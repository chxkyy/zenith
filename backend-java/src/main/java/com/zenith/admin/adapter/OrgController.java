package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.app.OrgService;
import com.zenith.admin.dto.OrgDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orgs")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @GetMapping
    public MultiResponse<OrgDTO> list() {
        return orgService.listAll();
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody OrgDTO orgDTO) {
        orgService.save(orgDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PutMapping
    public com.alibaba.cola.dto.Response update(@RequestBody OrgDTO orgDTO) {
        orgService.update(orgDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @DeleteMapping("/{id}")
    public com.alibaba.cola.dto.Response delete(@PathVariable Long id) {
        orgService.delete(id);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<OrgDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(orgService.getById(id));
    }
}
