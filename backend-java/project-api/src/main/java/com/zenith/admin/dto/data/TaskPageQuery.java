package com.zenith.admin.dto.data;

import lombok.Data;

@Data
public class TaskPageQuery {

    private Integer pageIndex;

    private Integer pageSize;

    private String processTemplateName;
}
