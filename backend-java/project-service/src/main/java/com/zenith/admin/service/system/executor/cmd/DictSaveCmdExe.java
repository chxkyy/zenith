package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dto.data.DictAddCmd;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictSaveCmdExe {

    private final DictMapper dictMapper;

    public void execute(DictAddCmd cmd) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, cmd.getType());

        Long count = dictMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BizException("DICT_TYPE_EXIST", "字典类型编码已存在");
        }

        DictDO dictDO = new DictDO();
        dictDO.setName(cmd.getName());
        dictDO.setType(cmd.getType());
        dictMapper.insert(dictDO);
    }
}
