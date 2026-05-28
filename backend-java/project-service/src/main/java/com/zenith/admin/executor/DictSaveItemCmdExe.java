package com.zenith.admin.executor;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.data.DictItemAddCmd;
import com.zenith.admin.mapper.DictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictSaveItemCmdExe {

    private final DictItemMapper dictItemMapper;

    public void execute(DictItemAddCmd cmd) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, cmd.getType());
        queryWrapper.eq(DictItemDO::getDictValue, cmd.getDictValue());

        Long count = dictItemMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BizException("DICT_ITEM_VALUE_EXIST", "同类型下字典项值已存在");
        }

        DictItemDO dictItemDO = new DictItemDO();
        dictItemDO.setType(cmd.getType());
        dictItemDO.setLabel(cmd.getLabel());
        dictItemDO.setDictValue(cmd.getDictValue());
        dictItemDO.setSort(cmd.getSort());
        dictItemMapper.insert(dictItemDO);
    }
}
