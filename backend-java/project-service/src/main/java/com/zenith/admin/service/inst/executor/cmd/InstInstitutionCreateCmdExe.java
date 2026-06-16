package com.zenith.admin.service.inst.executor.cmd;

import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.InstInstitutionDO;
import com.zenith.admin.dto.inst.cmd.InstInstitutionCreateCmd;
import com.zenith.admin.mapper.InstInstitutionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 创建/编辑机构执行器
 */
@Component
@RequiredArgsConstructor
public class InstInstitutionCreateCmdExe {

    private final InstInstitutionMapper instInstitutionMapper;

    /**
     * 执行创建或编辑机构（有ID则更新，无ID则创建）
     *
     * @param cmd 创建/编辑命令
     * @return 机构ID
     */
    public Long execute(InstInstitutionCreateCmd cmd) {
        if (cmd.getId() != null) {
            // 编辑模式：校验存在性后更新
            return update(cmd);
        } else {
            // 创建模式：直接插入
            return create(cmd);
        }
    }

    /**
     * 创建机构
     */
    private Long create(InstInstitutionCreateCmd cmd) {
        InstInstitutionDO institutionDO = new InstInstitutionDO();
        copyCmdToDO(cmd, institutionDO);
        institutionDO.setCooperationStatus("pending"); // 默认待合作状态
        institutionDO.setCreateUserId(UserContext.getUserId());

        instInstitutionMapper.insert(institutionDO);
        return institutionDO.getId();
    }

    /**
     * 编辑机构
     */
    private Long update(InstInstitutionCreateCmd cmd) {
        // 校验存在性
        InstInstitutionDO existingDO = instInstitutionMapper.selectById(cmd.getId());
        if (existingDO == null) {
            throw new RuntimeException("机构不存在");
        }

        // 更新字段（creditCode格式已在Cmd层校验）
        copyCmdToDO(cmd, existingDO);
        existingDO.setUpdateUserId(UserContext.getUserId());

        instInstitutionMapper.updateById(existingDO);
        return existingDO.getId();
    }

    /**
     * 将Cmd属性复制到DO
     */
    private void copyCmdToDO(InstInstitutionCreateCmd cmd, InstInstitutionDO target) {
        target.setFullName(cmd.getFullName());
        target.setShortName(cmd.getShortName());
        target.setCreditCode(cmd.getCreditCode());
        target.setInstType(cmd.getInstType());
        target.setEstablishDate(cmd.getEstablishDate());
        target.setRegisteredCapital(cmd.getRegisteredCapital());
        target.setLegalRepresentative(cmd.getLegalRepresentative());
        target.setRegisteredAddress(cmd.getRegisteredAddress());
        target.setContactPhone(cmd.getContactPhone());
        target.setContactEmail(cmd.getContactEmail());
        target.setLogoUrl(cmd.getLogoUrl());
    }
}
