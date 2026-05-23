package com.zenith.admin.dto.data;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProcessInstanceDTO {

    private Long id;

    private String processNo;

    private Long processTemplateId;

    private String processTemplateName;

    private String title;

    private BigDecimal amount;

    private Long startDate;

    private Long endDate;

    private String formData;

    private Integer status;

    private String statusName;

    private Long initiatorId;

    private String initiatorName;

    private Integer currentNodeOrder;

    private String currentNodeName;

    private Long createdTime;

    private List<NodeProgressDTO> nodes;

    private List<ApprovalRecordDTO> approvalRecords;
}
