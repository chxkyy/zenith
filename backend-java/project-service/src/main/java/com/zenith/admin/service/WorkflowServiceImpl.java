package com.zenith.admin.service;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.WorkflowDomainService;
import com.zenith.admin.api.WorkflowService;
import com.zenith.admin.dto.data.ProcessInstanceCreateCmd;
import com.zenith.admin.dto.data.ProcessInstanceDTO;
import com.zenith.admin.dto.data.ProcessInstancePageQuery;
import com.zenith.admin.executor.*;
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
