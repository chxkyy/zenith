package com.zenith.admin.service.inst.executor.cmd;

import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.InstProductDO;
import com.zenith.admin.dto.inst.cmd.InstProductCreateCmd;
import com.zenith.admin.mapper.InstProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 创建/编辑产品执行器
 */
@Component
@RequiredArgsConstructor
public class InstProductCreateCmdExe {

    private final InstProductMapper instProductMapper;

    /**
     * 执行创建或编辑产品（有ID则更新，无ID则创建）
     *
     * @param cmd 创建/编辑命令
     * @return 产品ID
     */
    public Long execute(InstProductCreateCmd cmd) {
        if (cmd.getId() != null) {
            // 编辑模式：校验存在性后更新
            return update(cmd);
        } else {
            // 创建模式：直接插入
            return create(cmd);
        }
    }

    /**
     * 创建产品
     */
    private Long create(InstProductCreateCmd cmd) {
        InstProductDO productDO = new InstProductDO();
        copyCmdToDO(cmd, productDO);
        productDO.setCreateUserId(UserContext.getUserId());

        instProductMapper.insert(productDO);
        return productDO.getId();
    }

    /**
     * 编辑产品
     */
    private Long update(InstProductCreateCmd cmd) {
        // 校验存在性
        InstProductDO existingDO = instProductMapper.selectById(cmd.getId());
        if (existingDO == null) {
            throw new RuntimeException("产品不存在");
        }

        // 更新字段
        copyCmdToDO(cmd, existingDO);
        existingDO.setUpdateUserId(UserContext.getUserId());

        instProductMapper.updateById(existingDO);
        return existingDO.getId();
    }

    /**
     * 将Cmd属性复制到DO
     */
    private void copyCmdToDO(InstProductCreateCmd cmd, InstProductDO target) {
        target.setInstitutionId(cmd.getInstitutionId());
        target.setProductName(cmd.getProductName());
        target.setProductCode(cmd.getProductCode());
        target.setProductType(cmd.getProductType());
        target.setCooperationStatus(cmd.getCooperationStatus());
        target.setCooperationStartDate(cmd.getCooperationStartDate());
        target.setEndDate(cmd.getEndDate());
        target.setContactPerson(cmd.getContactPerson());
    }
}
