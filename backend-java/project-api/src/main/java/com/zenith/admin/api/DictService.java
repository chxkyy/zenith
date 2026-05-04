package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.DictAddCmd;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictItemAddCmd;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictItemPageQuery;
import com.zenith.admin.dto.data.DictItemUpdateCmd;
import com.zenith.admin.dto.data.DictPageQuery;
import com.zenith.admin.dto.data.DictUpdateCmd;

import java.util.List;

public interface DictService {
    List<DictDTO> listAll();
    List<DictDTO> listByType(String type);
    void update(DictUpdateCmd cmd, Long currentUserId);
    DictDTO getById(Long id);
    PageInfo<DictDTO> page(DictPageQuery query);
    void delete(Long id);
    void save(DictAddCmd cmd, Long currentUserId);
    List<DictItemDTO> listItemsByType(String type);
    PageInfo<DictItemDTO> pageItems(DictItemPageQuery query);
    void saveItem(DictItemAddCmd cmd, Long currentUserId);
    void updateItem(DictItemUpdateCmd cmd, Long currentUserId);
    void deleteItem(Long id);
    DictItemDTO getItemById(Long id);
}
