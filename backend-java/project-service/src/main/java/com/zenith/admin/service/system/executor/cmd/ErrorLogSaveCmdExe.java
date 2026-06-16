package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dto.system.data.ErrorLogDTO;
import com.zenith.admin.service.system.executor.converter.ErrorLogConvertor;
import com.zenith.admin.dataobject.ErrorLogDO;
import com.zenith.admin.mapper.ErrorLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorLogSaveCmdExe {

    private final ErrorLogMapper errorLogMapper;
    private final ErrorLogConvertor errorLogConvertor;

    public void execute(ErrorLogDTO errorLogDTO) {
        ErrorLogDO errorLogDO = errorLogConvertor.toDataObject(errorLogDTO);
        errorLogMapper.insert(errorLogDO);
    }
}
