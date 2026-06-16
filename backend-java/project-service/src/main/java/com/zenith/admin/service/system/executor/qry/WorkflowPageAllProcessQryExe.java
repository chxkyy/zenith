package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.util.PageResponseUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dto.system.data.ProcessInstanceDTO;
import com.zenith.admin.dto.system.qry.ProcessInstancePageQuery;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkflowPageAllProcessQryExe {

    private final ProcessInstanceMapper processInstanceMapper;
    private final WorkflowGetDetailQryExe workflowGetDetailQryExe;

    public PageInfo<ProcessInstanceDTO> execute(ProcessInstancePageQuery query) {
        LambdaQueryWrapper<ProcessInstanceDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getStatus() != null) {
            queryWrapper.eq(ProcessInstanceDO::getStatus, query.getStatus());
        }
        queryWrapper.orderByDesc(ProcessInstanceDO::getCreatedTime);

        PageInfo<ProcessInstanceDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> processInstanceMapper.selectList(queryWrapper));

        return PageResponseUtils.convert(pageInfo, list -> list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    private ProcessInstanceDTO convertToDTO(ProcessInstanceDO dO) {
        return workflowGetDetailQryExe.execute(dO.getId(), null);
    }
}
