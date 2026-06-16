package com.zenith.admin.dto.system.data;

import lombok.Data;

@Data
public class NodeTemplateDTO {

    private Long id;

    private Long processTemplateId;

    private Integer nodeOrder;

    private String nodeName;

    private Integer nodeType;

    private String nodeTypeName;

    private Integer approverType;

    private String approverTypeName;

    private String approverValue;

    private Integer opinionRequired;
}
