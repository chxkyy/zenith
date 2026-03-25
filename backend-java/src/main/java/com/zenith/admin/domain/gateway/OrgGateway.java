package com.zenith.admin.domain.gateway;

import com.zenith.admin.domain.model.OrgEntity;
import java.util.List;

public interface OrgGateway {
    List<OrgEntity> listAll();
    void save(OrgEntity org);
    OrgEntity getById(Long id);
    void deleteById(Long id);
}
