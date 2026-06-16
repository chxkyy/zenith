package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dto.system.data.OperLogDTO;
import com.zenith.admin.service.system.executor.converter.OperLogConvertor;
import com.zenith.admin.dataobject.OperLogDO;
import com.zenith.admin.mapper.OperLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperLogSaveCmdExe {

    private final OperLogMapper operLogMapper;
    private final OperLogConvertor operLogConvertor;

    public void execute(OperLogDTO operLogDTO) {
        OperLogDO operLogDO = operLogConvertor.toDataObject(operLogDTO);
        operLogMapper.insert(operLogDO);
    }
}
