package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 关联材料上传命令对象
 */
@Data
public class InstAdmissionMaterialUploadCmd {

    @NotNull(message = "申请单ID不能为空")
    private Long admissionId;

    @NotBlank(message = "材料分类不能为空")
    private String materialCategory;

    /**
     * 材料名称
     */
    private String materialName;

    @NotNull(message = "文件ID不能为空")
    private Long fileId;
}
