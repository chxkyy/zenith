package com.zenith.admin.dto.data;

import lombok.Data;

@Data
public class NodeProgressDTO {

    private Integer nodeOrder;

    private String nodeName;

    private String status;

    private String assigneeNames;
}
