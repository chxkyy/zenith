package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.DictConvertor;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.mapper.DictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DictListItemsByTypeQryExe {

    private final DictItemMapper dictItemMapper;
    private final DictConvertor dictConvertor;

    public List<DictItemDTO> execute(String type) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, type).orderByAsc(DictItemDO::getSort);
        List<DictItemDO> dictItemDOS = dictItemMapper.selectList(queryWrapper);
        return dictConvertor.toItemDTOList(dictItemDOS);
    }
}
