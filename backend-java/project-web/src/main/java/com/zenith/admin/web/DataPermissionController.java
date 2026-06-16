package com.zenith.admin.web;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.system.cmd.DataPermissionAssignCmd;
import com.zenith.admin.dto.system.cmd.DataPermissionBatchAssignCmd;
import com.zenith.admin.dto.system.cmd.DataPermissionRevokeCmd;
import com.zenith.admin.dto.system.data.DataPermissionDTO;
import com.zenith.admin.dto.system.qry.DataPermissionQuery;
import com.zenith.admin.service.system.executor.cmd.DataPermissionManageCmdExe;
import com.zenith.admin.service.system.executor.qry.DataPermissionListQryExe;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据权限管理 Controller
 * <p>
 * 提供管理员操作数据权限绑定关系的 REST API（PRODUCT.md #16-19）。
 * 仅具有「数据权限管理」功能权限的管理员可访问。
 * </p>
 *
 * @see com.zenith.admin.annotation.DataPermission
 */
@RestController
@RequestMapping("/api/data-permissions")
@RequiredArgsConstructor
public class DataPermissionController {

    private final DataPermissionManageCmdExe manageCmdExe;
    private final DataPermissionListQryExe listQryExe;

    /**
     * 分配数据权限
     * <p>POST /api/data-permissions/assign</p>
     */
    @PostMapping("/assign")
    public SingleResponse<Void> assign(@Valid @RequestBody DataPermissionAssignCmd cmd) {
        String operatorName = getOperatorName();
        manageCmdExe.assign(cmd, operatorName);
        return SingleResponse.buildSuccess();
    }

    /**
     * 回收数据权限
     * <p>POST /api/data-permissions/revoke</p>
     */
    @PostMapping("/revoke")
    public SingleResponse<Void> revoke(@Valid @RequestBody DataPermissionRevokeCmd cmd) {
        String operatorName = getOperatorName();
        manageCmdExe.revoke(cmd, operatorName);
        return SingleResponse.buildSuccess();
    }

    /**
     * 批量分配数据权限
     * <p>POST /api/data-permissions/batch-assign</p>
     */
    @PostMapping("/batch-assign")
    public SingleResponse<Void> batchAssign(@Valid @RequestBody DataPermissionBatchAssignCmd cmd) {
        String operatorName = getOperatorName();
        manageCmdExe.batchAssign(cmd, operatorName);
        return SingleResponse.buildSuccess();
    }

    /**
     * 查询某用户负责的数据权限列表
     * <p>GET /api/data-permissions?userId=xxx</p>
     */
    @GetMapping
    public SingleResponse<List<DataPermissionDTO>> listByUser(DataPermissionQuery query) {
        List<DataPermissionDTO> list = listQryExe.listByUser(query);
        return SingleResponse.of(list);
    }

    /**
     * 查询某数据的负责人用户ID列表
     * <p>GET /api/data-permissions/owners?dataType=xxx&dataId=yyy</p>
     */
    @GetMapping("/owners")
    public SingleResponse<List<Long>> listOwnersByData(DataPermissionQuery query) {
        List<Long> ownerIds = listQryExe.listOwnersByData(query);
        return SingleResponse.of(ownerIds);
    }

    /**
     * 检查指定数据的负责人数量（用于唯一负责人警告提示）
     * <p>GET /api/data-permissions/owners/count?dataType=xxx&dataId=yyy</p>
     */
    @GetMapping("/owners/count")
    public SingleResponse<Long> countOwnersByData(@RequestParam String dataType, @RequestParam Long dataId) {
        long count = listQryExe.countOwnersByData(dataType, dataId);
        return SingleResponse.of(count);
    }

    private String getOperatorName() {
        Long userId = UserContext.getUserId();
        return userId != null ? String.valueOf(userId) : "system";
    }
}
