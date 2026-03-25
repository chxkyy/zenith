package com.zenith.admin.app;

import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.OperLogGateway;
import com.zenith.admin.domain.model.OperLogEntity;
import com.zenith.admin.dto.OperLogDTO;
import com.zenith.admin.infrastructure.convertor.OperLogConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperLogService {

    @Autowired
    private OperLogGateway operLogGateway;

    @Autowired
    private OperLogConvertor operLogConvertor;

    public PageResponse<OperLogDTO> listByPage(int pageIndex, int pageSize, String operator, String module, String result) {
        PageInfo<OperLogEntity> pageInfo = operLogGateway.listByPage(pageIndex, pageSize, operator, module, result);
        List<OperLogDTO> dtos = operLogConvertor.toDTOList(pageInfo.getList());
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), pageSize, pageIndex);
    }

    public void delete(Long id) {
        operLogGateway.deleteById(id);
    }
}
