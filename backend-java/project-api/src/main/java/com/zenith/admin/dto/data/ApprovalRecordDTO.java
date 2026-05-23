package com.zenith.admin.dto.data;

import lombok.Data;

@Data
public class ApprovalRecordDTO {

    private Long id;

    private Integer nodeOrder;

    private String nodeName;

    private Long operatorId;

    private String operatorName;

    private Integer actionType;

    private String actionName;

    private String opinion;

    private Long operateTime;
}
