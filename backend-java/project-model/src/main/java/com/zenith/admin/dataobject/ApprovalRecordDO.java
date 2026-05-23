package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_wf_approval_record")
public class ApprovalRecordDO {

    private Long id;

    private Long processInstanceId;

    private Integer nodeOrder;

    private String nodeName;

    private Long operatorId;

    private String operatorName;

    private Integer actionType;

    private String opinion;

    private LocalDateTime operateTime;
}
