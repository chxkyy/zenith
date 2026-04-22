package com.zenith.admin.api;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.data.OrgPageQuery;

public interface OrgService {
    MultiResponse<OrgDTO> listAll();
    PageInfo<OrgDTO> page(OrgPageQuery query);
    void save(OrgDTO orgDTO);
    void update(OrgDTO orgDTO);
    void delete(Long id);
    OrgDTO getById(Long id);
}
