package com.zenith.admin.dto.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessInstanceCreateCmd {

    private Long processTemplateId;

    private String title;

    private BigDecimal amount;

    private Long startDate;

    private Long endDate;

    private String formData;
}
