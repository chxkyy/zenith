package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.service.system.executor.converter.FunctionConvertor;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.mapper.FunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FunctionListByMenuIdQryExe {

    private final FunctionMapper functionMapper;
    private final FunctionConvertor functionConvertor;

    public List<FunctionDTO> execute(Long menuId) {
        LambdaQueryWrapper<FunctionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FunctionDO::getMenuId, menuId);
        queryWrapper.orderByAsc(FunctionDO::getSort, FunctionDO::getId);
        List<FunctionDO> functionDOS = functionMapper.selectList(queryWrapper);
        return functionConvertor.toDTOList(functionDOS);
    }
}
