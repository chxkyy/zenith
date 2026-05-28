package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dto.data.ProcessInstanceDTO;
import com.zenith.admin.dto.data.ProcessInstancePageQuery;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkflowPageMyProcessQryExe {

    private final ProcessInstanceMapper processInstanceMapper;
    private final WorkflowGetDetailQryExe workflowGetDetailQryExe;

    public PageInfo<ProcessInstanceDTO> execute(ProcessInstancePageQuery query, Long currentUserId) {
        LambdaQueryWrapper<ProcessInstanceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessInstanceDO::getInitiatorId, currentUserId);
        if (query.getStatus() != null) {
            queryWrapper.eq(ProcessInstanceDO::getStatus, query.getStatus());
        }
        queryWrapper.orderByDesc(ProcessInstanceDO::getCreatedTime);

        PageInfo<ProcessInstanceDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> processInstanceMapper.selectList(queryWrapper));

        List<ProcessInstanceDTO> dtos = pageInfo.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageInfo<ProcessInstanceDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    private ProcessInstanceDTO convertToDTO(ProcessInstanceDO dO) {
        return workflowGetDetailQryExe.execute(dO.getId(), null);
    }
}
