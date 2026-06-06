package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dto.cmd.ProcessInstanceCreateCmd;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class WorkflowSaveDraftCmdExe {

    private final ProcessInstanceMapper processInstanceMapper;
    private final ProcessTemplateMapper processTemplateMapper;

    public Long execute(ProcessInstanceCreateCmd cmd, Long currentUserId) {
        ProcessTemplateDO template = processTemplateMapper.selectById(cmd.getProcessTemplateId());
        if (template == null) {
            throw new RuntimeException("流程模板不存在");
        }

        ProcessInstanceDO instance = new ProcessInstanceDO();
        instance.setProcessNo(generateProcessNo(template.getCode()));
        instance.setProcessTemplateId(cmd.getProcessTemplateId());
        instance.setProcessTemplateVersion(template.getVersion());
        instance.setTitle(cmd.getTitle());
        instance.setFormData(cmd.getFormData());
        instance.setStatus(ProcessStatusEnum.DRAFT.getCode());
        instance.setInitiatorId(currentUserId);
        processInstanceMapper.insert(instance);

        return instance.getId();
    }

    private String generateProcessNo(String code) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = getProcessTypePrefix(code);
        return prefix + dateStr + String.format("%04d", System.currentTimeMillis() % 10000);
    }

    private String getProcessTypePrefix(String code) {
        if (code == null) {
            return "LC";
        }
        if (code.contains("LEAVE")) {
            return "QJ";
        } else if (code.contains("EXPENSE")) {
            return "BX";
        } else if (code.contains("TRAVEL")) {
            return "CC";
        }
        return "LC";
    }
}
