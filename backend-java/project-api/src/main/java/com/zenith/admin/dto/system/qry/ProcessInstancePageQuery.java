package com.zenith.admin.dto.system.qry;

import lombok.Data;

@Data
public class ProcessInstancePageQuery {

    private Integer pageIndex;

    private Integer pageSize;

    private Integer status;

    private String processTemplateName;

    private String initiatorName;

    private Long startTime;

    private Long endTime;
}
