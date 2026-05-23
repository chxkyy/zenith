package com.zenith.admin.dto.data;

import lombok.Data;

import java.util.List;

@Data
public class ProcessTemplateDTO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String formSchema;

    private Integer status;

    private String statusName;

    private Integer version;

    private Integer nodeCount;

    private Long createdTime;

    private List<NodeTemplateDTO> nodes;
}
