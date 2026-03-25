package com.zenith.admin.app;

import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.LoginLogGateway;
import com.zenith.admin.domain.model.LoginLogEntity;
import com.zenith.admin.dto.LoginLogDTO;
import com.zenith.admin.infrastructure.convertor.LoginLogConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginLogService {

    @Autowired
    private LoginLogGateway loginLogGateway;

    @Autowired
    private LoginLogConvertor loginLogConvertor;

    public PageResponse<LoginLogDTO> listByPage(int pageIndex, int pageSize, String username, String status, String ip) {
        PageInfo<LoginLogEntity> pageInfo = loginLogGateway.listByPage(pageIndex, pageSize, username, status, ip);
        List<LoginLogDTO> dtos = loginLogConvertor.toDTOList(pageInfo.getList());
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), pageSize, pageIndex);
    }

    public void delete(Long id) {
        loginLogGateway.deleteById(id);
    }
}
