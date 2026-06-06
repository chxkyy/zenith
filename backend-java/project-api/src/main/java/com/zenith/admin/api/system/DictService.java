package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.cmd.DictAddCmd;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.cmd.DictItemAddCmd;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.query.DictItemPageQuery;
import com.zenith.admin.dto.cmd.DictItemUpdateCmd;
import com.zenith.admin.dto.query.DictPageQuery;
import com.zenith.admin.dto.cmd.DictUpdateCmd;

import java.util.List;

public interface DictService {
    void delete(Long id);

    void deleteItem(Long id);

    DictDTO getById(Long id);

    DictItemDTO getItemById(Long id);

    List<DictDTO> listAll();

    List<DictDTO> listByType(String type);

    List<DictItemDTO> listItemsByType(String type);

    PageInfo<DictDTO> page(DictPageQuery query);

    PageInfo<DictItemDTO> pageItems(DictItemPageQuery query);

    void save(DictAddCmd cmd, Long currentUserId);

    void saveItem(DictItemAddCmd cmd, Long currentUserId);

    void update(DictUpdateCmd cmd, Long currentUserId);

    void updateItem(DictItemUpdateCmd cmd, Long currentUserId);
}
