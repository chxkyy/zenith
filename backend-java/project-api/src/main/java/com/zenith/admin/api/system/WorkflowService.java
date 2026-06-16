package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.system.cmd.ProcessInstanceCreateCmd;
import com.zenith.admin.dto.system.data.ProcessInstanceDTO;
import com.zenith.admin.dto.system.qry.ProcessInstancePageQuery;

public interface WorkflowService {

    Long saveDraft(ProcessInstanceCreateCmd cmd, Long currentUserId);

    void submit(Long processInstanceId, Long currentUserId);

    void revoke(Long processInstanceId, Long currentUserId);

    void cancel(Long processInstanceId, Long currentUserId);

    void resubmit(Long processInstanceId, String formData, Long currentUserId);

    ProcessInstanceDTO getDetail(Long id, Long currentUserId);

    PageInfo<ProcessInstanceDTO> pageMyProcess(ProcessInstancePageQuery query, Long currentUserId);

    PageInfo<ProcessInstanceDTO> pageAllProcess(ProcessInstancePageQuery query);
}
