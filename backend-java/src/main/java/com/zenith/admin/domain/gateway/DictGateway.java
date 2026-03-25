package com.zenith.admin.domain.gateway;

import com.zenith.admin.domain.model.DictEntity;
import java.util.List;

public interface DictGateway {
    List<DictEntity> listAll();
    List<DictEntity> listByType(String type);
    void save(DictEntity dict);
    DictEntity getById(Long id);
    void deleteById(Long id);
}
