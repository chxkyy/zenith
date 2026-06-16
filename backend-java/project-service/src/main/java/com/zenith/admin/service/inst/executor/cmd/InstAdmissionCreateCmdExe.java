package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.fastjson2.JSON;
import com.zenith.admin.dto.inst.cmd.InstAdmissionCreateCmd;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 创建准入申请草稿执行器
 *
 * <p>生成申请单号、初始化状态为 DRAFT，将基本信息存储为 JSON。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionCreateCmdExe {

    private final InstAdmissionMapper admissionMapper;

    /**
     * 执行创建草稿操作
     *
     * @param cmd 创建命令对象
     * @return 新创建的申请单ID
     */
    public Long execute(InstAdmissionCreateCmd cmd) {
        Long currentUserId = UserContext.getUserId();

        // 生成申请单号：ADM-YYYYMMDD-NNNN
        String admissionNo = generateAdmissionNo();

        // 构建基本信息的 JSON
        String basicInfoJson = buildBasicInfoJson(cmd);

        // 构建 DO 对象
        InstAdmissionDO admission = new InstAdmissionDO();
        admission.setAdmissionNo(admissionNo);
        admission.setManagerName(cmd.getManagerName());
        admission.setManagerType(cmd.getManagerType());
        admission.setCreditCode(cmd.getCreditCode());
        admission.setRegisteredCapital(cmd.getRegisteredCapital());
        admission.setEstablishDate(cmd.getEstablishDate());
        admission.setLegalRepresentative(cmd.getLegalRepresentative());
        admission.setRegisteredAddress(cmd.getRegisteredAddress());
        admission.setContactPerson(cmd.getContactPerson());
        admission.setContactPhone(cmd.getContactPhone());
        admission.setContactEmail(cmd.getContactEmail());
        admission.setTargetPoolIds(JSON.toJSONString(cmd.getTargetPoolIds()));
        admission.setBasicInfo(basicInfoJson);
        admission.setStatus("DRAFT");
        admission.setCreateUserId(currentUserId);
        admission.setCreatedTime(LocalDateTime.now());

        admissionMapper.insert(admission);

        return admission.getId();
    }

    /**
     * 生成申请单号
     *
     * <p>格式：ADM-YYYYMMDD-NNNN，其中 NNNN 为当天序号（4位，不足补零）</p>
     *
     * @return 申请单号
     */
    private String generateAdmissionNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String prefix = "ADM-" + dateStr + "-";

        // 查询当天已生成的申请单数量
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        Long todayCount = admissionMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InstAdmissionDO>()
                        .ge(InstAdmissionDO::getCreatedTime, startOfDay)
        );

        // 序号 = 当天数量 + 1，格式化为4位数字
        int sequence = (todayCount != null ? todayCount.intValue() : 0) + 1;
        return prefix + String.format("%04d", sequence);
    }

    /**
     * 构建基本信息 JSON 字符串
     *
     * @param cmd 创建命令对象
     * @return JSON 字符串
     */
    private String buildBasicInfoJson(InstAdmissionCreateCmd cmd) {
        return JSON.toJSONString(cmd);
    }
}
