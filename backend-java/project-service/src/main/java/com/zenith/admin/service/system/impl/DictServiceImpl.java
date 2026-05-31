package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.system.DictService;
import com.zenith.admin.dto.data.DictAddCmd;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictItemAddCmd;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictItemPageQuery;
import com.zenith.admin.dto.data.DictItemUpdateCmd;
import com.zenith.admin.dto.data.DictPageQuery;
import com.zenith.admin.dto.data.DictUpdateCmd;
import com.zenith.admin.service.system.executor.cmd.DictDeleteCmdExe;
import com.zenith.admin.service.system.executor.cmd.DictDeleteItemCmdExe;
import com.zenith.admin.service.system.executor.qry.DictGetByIdQryExe;
import com.zenith.admin.service.system.executor.qry.DictGetItemByIdQryExe;
import com.zenith.admin.service.system.executor.qry.DictListAllQryExe;
import com.zenith.admin.service.system.executor.qry.DictListByTypeQryExe;
import com.zenith.admin.service.system.executor.qry.DictListItemsByTypeQryExe;
import com.zenith.admin.service.system.executor.qry.DictPageItemsQryExe;
import com.zenith.admin.service.system.executor.qry.DictPageQryExe;
import com.zenith.admin.service.system.executor.cmd.DictSaveCmdExe;
import com.zenith.admin.service.system.executor.cmd.DictSaveItemCmdExe;
import com.zenith.admin.service.system.executor.cmd.DictUpdateCmdExe;
import com.zenith.admin.service.system.executor.cmd.DictUpdateItemCmdExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {
    private final DictDeleteCmdExe dictDeleteCmdExe;
    private final DictDeleteItemCmdExe dictDeleteItemCmdExe;
    private final DictGetByIdQryExe dictGetByIdQryExe;
    private final DictGetItemByIdQryExe dictGetItemByIdQryExe;
    private final DictListAllQryExe dictListAllQryExe;
    private final DictListByTypeQryExe dictListByTypeQryExe;
    private final DictListItemsByTypeQryExe dictListItemsByTypeQryExe;
    private final DictPageItemsQryExe dictPageItemsQryExe;
    private final DictPageQryExe dictPageQryExe;
    private final DictSaveCmdExe dictSaveCmdExe;
    private final DictSaveItemCmdExe dictSaveItemCmdExe;
    private final DictUpdateCmdExe dictUpdateCmdExe;
    private final DictUpdateItemCmdExe dictUpdateItemCmdExe;

    @Override
    public void delete(Long id) {
        dictDeleteCmdExe.execute(id);
    }

    @Override
    public void deleteItem(Long id) {
        dictDeleteItemCmdExe.execute(id);
    }

    @Override
    public DictDTO getById(Long id) {
        return dictGetByIdQryExe.execute(id);
    }

    @Override
    public DictItemDTO getItemById(Long id) {
        return dictGetItemByIdQryExe.execute(id);
    }

    @Override
    public List<DictDTO> listAll() {
        return dictListAllQryExe.execute();
    }

    @Override
    public List<DictDTO> listByType(String type) {
        return dictListByTypeQryExe.execute(type);
    }

    @Override
    public List<DictItemDTO> listItemsByType(String type) {
        return dictListItemsByTypeQryExe.execute(type);
    }

    @Override
    public PageInfo<DictDTO> page(DictPageQuery query) {
        return dictPageQryExe.execute(query);
    }

    @Override
    public PageInfo<DictItemDTO> pageItems(DictItemPageQuery query) {
        return dictPageItemsQryExe.execute(query);
    }

    @Override
    public void save(DictAddCmd cmd, Long currentUserId) {
        dictSaveCmdExe.execute(cmd);
    }

    @Override
    public void saveItem(DictItemAddCmd cmd, Long currentUserId) {
        dictSaveItemCmdExe.execute(cmd);
    }

    @Override
    public void update(DictUpdateCmd cmd, Long currentUserId) {
        dictUpdateCmdExe.execute(cmd);
    }

    @Override
    public void updateItem(DictItemUpdateCmd cmd, Long currentUserId) {
        dictUpdateItemCmdExe.execute(cmd);
    }
}
