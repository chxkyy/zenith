package com.zenith.admin.dto.system.data;

import lombok.Data;

@Data
public class NodeProgressDTO {

    private Integer nodeOrder;

    private String nodeName;

    private String status;

    private String assigneeNames;
}
