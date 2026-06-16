package com.zenith.admin.dto.system.cmd;

import lombok.Data;

@Data
public class NodeTemplateCreateCmd {

    private Integer nodeOrder;

    private String nodeName;

    private Integer nodeType;

    private Integer approverType;

    private String approverValue;

    private Integer opinionRequired;
}
