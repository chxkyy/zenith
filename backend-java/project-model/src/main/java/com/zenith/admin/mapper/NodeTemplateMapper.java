package com.zenith.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.dataobject.NodeTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NodeTemplateMapper extends BaseMapper<NodeTemplateDO> {

    List<NodeTemplateDO> selectByProcessTemplateId(Long processTemplateId);

    void deleteByProcessTemplateId(Long processTemplateId);
}
