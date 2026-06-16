package com.zenith.admin.dto.system.data;

import com.zenith.admin.annotation.UserName;
import lombok.Data;

@Data
public class TaskDTO {

    private Long id;

    private Long processInstanceId;

    private String processNo;

    private String processTemplateName;

    private String title;

    private Integer nodeOrder;

    private String nodeName;

    private Integer nodeType;

    private String nodeTypeName;

    @UserName(userId = "initiatorId")
    private Long initiatorId;

    private String initiatorName;

    private Long createdTime;
}
