package com.zenith.admin.dto.data;

import lombok.Data;

import java.util.List;

@Data
public class ProcessTemplateCreateCmd {

    private String code;

    private String name;

    private String description;

    private String formSchema;

    private List<NodeTemplateCreateCmd> nodes;

    @Data
    public static class NodeTemplateCreateCmd {
        private Integer nodeOrder;
        private String nodeName;
        private Integer nodeType;
        private Integer approverType;
        private String approverValue;
        private Integer opinionRequired;
    }
}
