package com.zenith.admin.dto.system.data;

import com.zenith.admin.annotation.UserName;
import lombok.Data;

@Data
public class ApprovalRecordDTO {

    private Long id;

    private Integer nodeOrder;

    private String nodeName;

    @UserName(userId = "operatorId")
    private Long operatorId;

    private String operatorName;

    private Integer actionType;

    private String actionName;

    private String opinion;

    private Long operateTime;
}
