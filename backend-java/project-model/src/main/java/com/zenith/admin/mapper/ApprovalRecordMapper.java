package com.zenith.admin.mapper;

import com.zenith.admin.dataobject.ApprovalRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalRecordMapper {

    void insert(ApprovalRecordDO record);

    List<ApprovalRecordDO> selectByProcessInstanceId(Long processInstanceId);
}
