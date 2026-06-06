package com.zenith.admin.dto.query;

import lombok.Data;

@Data
public class ProcessTemplatePageQuery {

    private Integer pageIndex;

    private Integer pageSize;

    private String name;

    private Integer status;
}
