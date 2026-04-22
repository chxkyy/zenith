package com.zenith.admin.api;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictPageQuery;
import com.zenith.admin.dto.data.DictItemPageQuery;

public interface DictService {
    MultiResponse<DictDTO> listAll();
    MultiResponse<DictDTO> listByType(String type);
    void update(DictDTO dictDTO);
    DictDTO getById(Long id);
    PageInfo<DictDTO> page(DictPageQuery query);
    void delete(Long id);
    void save(DictDTO dictDTO);
    MultiResponse<DictItemDTO> listItemsByType(String type);
    PageInfo<DictItemDTO> pageItems(DictItemPageQuery query);
    void saveItem(DictItemDTO dictItemDTO);
    void updateItem(DictItemDTO dictItemDTO);
    void deleteItem(Long id);
    DictItemDTO getItemById(Long id);
}
