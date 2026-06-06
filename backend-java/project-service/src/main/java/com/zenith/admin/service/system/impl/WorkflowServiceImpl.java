package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.system.WorkflowDomainService;
import com.zenith.admin.api.system.WorkflowService;
import com.zenith.admin.dto.cmd.ProcessInstanceCreateCmd;
import com.zenith.admin.dto.data.ProcessInstanceDTO;
import com.zenith.admin.dto.query.ProcessInstancePageQuery;
import com.zenith.admin.service.system.executor.cmd.WorkflowCancelCmdExe;
import com.zenith.admin.service.system.executor.qry.WorkflowGetDetailQryExe;
import com.zenith.admin.service.system.executor.qry.WorkflowPageAllProcessQryExe;
import com.zenith.admin.service.system.executor.qry.WorkflowPageMyProcessQryExe;
import com.zenith.admin.service.system.executor.cmd.WorkflowResubmitCmdExe;
import com.zenith.admin.service.system.executor.cmd.WorkflowRevokeCmdExe;
import com.zenith.admin.service.system.executor.cmd.WorkflowSaveDraftCmdExe;
import com.zenith.admin.service.system.executor.cmd.WorkflowSubmitCmdExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowCancelCmdExe workflowCancelCmdExe;
    private final WorkflowGetDetailQryExe workflowGetDetailQryExe;
    private final WorkflowPageAllProcessQryExe workflowPageAllProcessQryExe;
    private final WorkflowPageMyProcessQryExe workflowPageMyProcessQryExe;
    private final WorkflowResubmitCmdExe workflowResubmitCmdExe;
    private final WorkflowRevokeCmdExe workflowRevokeCmdExe;
    private final WorkflowSaveDraftCmdExe workflowSaveDraftCmdExe;
    private final WorkflowSubmitCmdExe workflowSubmitCmdExe;
    private final WorkflowDomainService workflowDomainService;

    @Override
    @Transactional
    public void cancel(Long processInstanceId, Long currentUserId) {
        workflowCancelCmdExe.execute(processInstanceId, currentUserId);
    }

    @Override
    public ProcessInstanceDTO getDetail(Long id, Long currentUserId) {
        return workflowGetDetailQryExe.execute(id, currentUserId);
    }

    @Override
    public PageInfo<ProcessInstanceDTO> pageAllProcess(ProcessInstancePageQuery query) {
        return workflowPageAllProcessQryExe.execute(query);
    }

    @Override
    public PageInfo<ProcessInstanceDTO> pageMyProcess(ProcessInstancePageQuery query, Long currentUserId) {
        return workflowPageMyProcessQryExe.execute(query, currentUserId);
    }

    @Override
    @Transactional
    public void resubmit(Long processInstanceId, String formData, Long currentUserId) {
        workflowResubmitCmdExe.execute(processInstanceId, formData, currentUserId);
    }

    @Override
    @Transactional
    public void revoke(Long processInstanceId, Long currentUserId) {
        workflowRevokeCmdExe.execute(processInstanceId, currentUserId);
    }

    @Override
    public Long saveDraft(ProcessInstanceCreateCmd cmd, Long currentUserId) {
        return workflowSaveDraftCmdExe.execute(cmd, currentUserId);
    }

    @Override
    @Transactional
    public void submit(Long processInstanceId, Long currentUserId) {
        workflowSubmitCmdExe.execute(processInstanceId, currentUserId);
    }
}
