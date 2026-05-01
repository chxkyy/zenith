package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.data.OrgPageQuery;

import java.util.List;

public interface OrgService {
    List<OrgDTO> listAll();
    PageInfo<OrgDTO> page(OrgPageQuery query);
    void save(OrgDTO orgDTO);
    void update(OrgDTO orgDTO);
    void delete(Long id);
    OrgDTO getById(Long id);
}
