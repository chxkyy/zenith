package com.zenith.admin.web.inst.controller;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstAdmissionApproveCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionMaterialUploadCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionScoreCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionSubmitCmd;
import com.zenith.admin.dto.inst.data.InstAdmissionDTO;
import com.zenith.admin.dto.inst.data.InstAdmissionMaterialDTO;
import com.zenith.admin.dto.inst.qry.InstAdmissionPageQuery;
import com.zenith.admin.api.inst.InstAdmissionLogService;
import com.zenith.admin.api.inst.InstAdmissionMaterialService;
import com.zenith.admin.api.inst.InstAdmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 准入申请全流程控制器
 *
 * <p>提供准入申请从草稿创建到最终审批的全流程管理，包括：
 * <ul>
 *   <li>草稿操作：创建、更新</li>
 *   <li>流程推进：提交评审、评分</li>
 *   <li>审批操作：通过、驳回、退回补正、撤销</li>
 *   <li>查询功能：我的申请、待评分、待审批、详情</li>
 *   <li>材料管理：上传、列表、删除</li>
 * </ul></p>
 *
 * <h3>状态流转说明：</h3>
 * <ul>
 *   <li>DRAFT（草稿）→ PENDING_REVIEW（待评分）：提交评审</li>
 *   <li>PENDING_REVIEW → PENDING_APPROVAL（待审批）：完成评分</li>
 *   <li>PENDING_REVIEW/PENDING_APPROVAL → DRAFT：退回补正</li>
 *   <li>PENDING_APPROVAL → APPROVED（已通过）：审批通过</li>
 *   <li>PENDING_APPROVAL → REJECTED（已驳回）：审批驳回</li>
 *   <li>非终态 → WITHDRAWN（已撤销）：申请人撤销</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/inst/admissions")
@RequiredArgsConstructor
public class InstAdmissionController {

    private final InstAdmissionService admissionService;
    private final InstAdmissionMaterialService materialService;
    private final InstAdmissionLogService logService;

    // ==================== 草稿操作 ====================

    /**
     * 创建准入申请草稿
     *
     * @param cmd 创建命令对象
     * @return 申请单ID
     */
    @PostMapping("/draft")
    public SingleResponse<Long> createDraft(@RequestBody @Valid InstAdmissionCreateCmd cmd) {
        return admissionService.createDraft(cmd);
    }

    /**
     * 更新准入申请草稿
     *
     * <p>仅允许在 DRAFT 状态下编辑。</p>
     *
     * @param id  申请单ID
     * @param cmd 更新命令对象
     * @return 是否更新成功
     */
    @PutMapping("/draft/{id}")
    public SingleResponse<Boolean> updateDraft(@PathVariable Long id, @RequestBody @Valid InstAdmissionCreateCmd cmd) {
        // 将路径参数 id 设置到 cmd 中
        cmd.setId(id);
        return admissionService.updateDraft(cmd);
    }

    // ==================== 流程推进 ====================

    /**
     * 提交评审
     *
     * <p>将申请从 DRAFT 状态推进到 PENDING_REVIEW 状态，
     * 等待评分人员打分。</p>
     *
     * @param id  申请单ID
     * @param cmd 提交命令对象
     */
    @PostMapping("/{id}/submit")
    public SingleResponse<Void> submitForReview(@PathVariable Long id, @RequestBody @Valid InstAdmissionSubmitCmd cmd) {
        // 将路径参数 id 设置到 cmd 中
        cmd.setAdmissionId(id);
        return admissionService.submitForReview(cmd);
    }

    /**
     * 提交评分
     *
     * <p>对申请进行五维度评分，计算加权总分。
     * 评分完成后状态变更为 PENDING_APPROVAL，等待最终审批。</p>
     *
     * @param id  申请单ID
     * @param cmd 评分命令对象
     */
    @PostMapping("/{id}/score")
    public SingleResponse<Void> score(@PathVariable Long id, @RequestBody @Valid InstAdmissionScoreCmd cmd) {
        // 将路径参数 id 设置到 cmd 中
        cmd.setAdmissionId(id);
        return admissionService.score(cmd);
    }

    // ==================== 审批操作 ====================

    /**
     * 审批操作（通过/驳回/退回）
     *
     * <p>根据 action 字段执行不同操作：
     * <ul>
     *   <li>approve - 审批通过</li>
     *   <li>reject - 审批驳回</li>
     *   <li>return - 退回补正</li>
     * </ul></p>
     *
     * @param id  申请单ID
     * @param cmd 审批命令对象
     */
    @PostMapping("/{id}/approve")
    public SingleResponse<Void> approve(@PathVariable Long id, @RequestBody @Valid InstAdmissionApproveCmd cmd) {
        // 将路径参数 id 设置到 cmd 中
        cmd.setAdmissionId(id);

        // 根据 action 类型调用不同的服务方法
        String action = cmd.getAction();
        if ("approve".equals(action)) {
            return admissionService.approve(cmd);
        } else if ("reject".equals(action)) {
            return admissionService.reject(cmd);
        } else if ("return".equals(action)) {
            return admissionService.returnForRevision(id, cmd.getOpinion());
        } else {
            throw new IllegalArgumentException("不支持的审批操作类型: " + action);
        }
    }

    /**
     * 撤销申请
     *
     * <p>仅非终态（APPROVED/REJECTED）的申请可以撤销。</p>
     *
     * @param id 申请单ID
     */
    @PostMapping("/{id}/withdraw")
    public SingleResponse<Void> withdraw(@PathVariable Long id) {
        return admissionService.withdraw(id);
    }

    /**
     * 退回补正
     *
     * <p>将申请退回到 DRAFT 状态，要求申请人补充材料或修改信息。</p>
     *
     * @param id     申请单ID
     * @param reason 退回原因
     */
    @PostMapping("/{id}/return")
    public SingleResponse<Void> returnForRevision(@PathVariable Long id, @RequestParam String reason) {
        return admissionService.returnForRevision(id, reason);
    }

    // ==================== 查询 ====================

    /**
     * 查询我的申请列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/my")
    public com.alibaba.cola.dto.PageResponse<InstAdmissionDTO> myApplications(InstAdmissionPageQuery query) {
        return admissionService.myApplications(query);
    }

    /**
     * 查询待评分列表
     *
     * <p>返回所有处于 PENDING_REVIEW 状态的申请，按创建时间升序排列。</p>
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/pending-review")
    public com.alibaba.cola.dto.PageResponse<InstAdmissionDTO> pendingReview(InstAdmissionPageQuery query) {
        return admissionService.pendingReview(query);
    }

    /**
     * 查询待审批列表
     *
     * <p>返回所有处于 PENDING_APPROVAL 状态的申请，按创建时间升序排列。</p>
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/pending-approval")
    public com.alibaba.cola.dto.PageResponse<InstAdmissionDTO> pendingApproval(InstAdmissionPageQuery query) {
        return admissionService.pendingApproval(query);
    }

    /**
     * 查询申请单详情
     *
     * @param id 申请单ID
     * @return 申请详情（含材料和日志）
     */
    @GetMapping("/{id}")
    public SingleResponse<InstAdmissionDTO> detail(@PathVariable Long id) {
        return admissionService.detail(id);
    }

    // ==================== 材料管理 ====================

    /**
     * 上传申请材料
     *
     * <p>将文件关联到指定的准入申请单。校验：
     * <ul>
     *   <li>申请单必须存在且非终态</li>
     *   <li>文件必须在文件系统中存在</li>
     * </ul></p>
     *
     * @param id  申请单ID
     * @param cmd 材料上传命令对象
     * @return 材料记录ID
     */
    @PostMapping("/{id}/materials")
    public SingleResponse<Long> uploadMaterial(@PathVariable Long id, @RequestBody @Valid InstAdmissionMaterialUploadCmd cmd) {
        // 将路径参数 id 设置到 cmd 中
        cmd.setAdmissionId(id);
        return materialService.uploadMaterial(cmd);
    }

    /**
     * 查询申请的材料列表
     *
     * @param id 申请单ID
     * @return 材料列表（按排序顺序）
     */
    @GetMapping("/{id}/materials")
    public List<InstAdmissionMaterialDTO> listMaterials(@PathVariable Long id) {
        return materialService.listByAdmissionId(id);
    }

    /**
     * 删除申请材料
     *
     * @param matId 材料记录ID
     * @return 是否删除成功
     */
    @DeleteMapping("/materials/{matId}")
    public SingleResponse<Boolean> deleteMaterial(@PathVariable Long matId) {
        return materialService.deleteMaterial(matId);
    }
}
