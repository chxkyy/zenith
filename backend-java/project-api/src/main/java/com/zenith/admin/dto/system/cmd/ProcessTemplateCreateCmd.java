package com.zenith.admin.dto.system.cmd;

import lombok.Data;

import java.util.List;

@Data
public class ProcessTemplateCreateCmd {

    private String code;

    private String name;

    private String description;

    private String formSchema;

    private List<NodeTemplateCreateCmd> nodes;
}
