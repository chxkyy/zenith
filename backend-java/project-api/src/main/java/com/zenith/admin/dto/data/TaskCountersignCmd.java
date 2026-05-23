package com.zenith.admin.dto.data;

import lombok.Data;

import java.util.List;

@Data
public class TaskCountersignCmd {

    private Long taskId;

    private Integer approverType;

    private List<Long> approverIds;

    private String opinion;
}
