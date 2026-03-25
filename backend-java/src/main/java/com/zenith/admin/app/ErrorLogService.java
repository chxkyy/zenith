package com.zenith.admin.app;

import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.ErrorLogGateway;
import com.zenith.admin.domain.model.ErrorLogEntity;
import com.zenith.admin.dto.ErrorLogDTO;
import com.zenith.admin.infrastructure.convertor.ErrorLogConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErrorLogService {

    @Autowired
    private ErrorLogGateway errorLogGateway;

    @Autowired
    private ErrorLogConvertor errorLogConvertor;

    public PageResponse<ErrorLogDTO> listByPage(int pageIndex, int pageSize, String module, String ip) {
        PageInfo<ErrorLogEntity> pageInfo = errorLogGateway.listByPage(pageIndex, pageSize, module, ip);
        List<ErrorLogDTO> dtos = errorLogConvertor.toDTOList(pageInfo.getList());
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), pageSize, pageIndex);
    }

    public void delete(Long id) {
        errorLogGateway.deleteById(id);
    }

    public void clear(int months) {
        errorLogGateway.clearLogs(months);
    }
}
