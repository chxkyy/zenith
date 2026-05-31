package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.mapper.DictItemMapper;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictDeleteCmdExe {

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;

    public void execute(Long id) {
        DictDO dictDO = dictMapper.selectById(id);
        if (dictDO != null) {
            LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DictItemDO::getType, dictDO.getType());
            Long count = dictItemMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BizException("DICT_HAS_ITEMS", "该字典类型下存在字典项，请先删除所有字典项后再删除类型");
            }
        }
        dictMapper.deleteById(id);
    }
}
