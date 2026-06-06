package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.cmd.OrgAddCmd;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.query.OrgPageQuery;
import com.zenith.admin.dto.cmd.OrgUpdateCmd;

import java.util.List;

public interface OrgService {
    List<OrgDTO> listAll();
    PageInfo<OrgDTO> page(OrgPageQuery query);
    void save(OrgAddCmd cmd, Long currentUserId);
    void update(OrgUpdateCmd cmd, Long currentUserId);
    void delete(Long id, Long currentUserId);
    OrgDTO getById(Long id);
}
