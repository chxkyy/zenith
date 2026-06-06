package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.service.system.executor.converter.FunctionConvertor;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.mapper.FunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FunctionGetByIdQryExe {

    private final FunctionMapper functionMapper;
    private final FunctionConvertor functionConvertor;

    public FunctionDTO execute(Long id) {
        FunctionDO functionDO = functionMapper.selectById(id);
        return functionConvertor.toDTO(functionDO);
    }
}
