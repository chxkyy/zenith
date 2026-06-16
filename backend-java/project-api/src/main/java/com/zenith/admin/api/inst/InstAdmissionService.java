package com.zenith.admin.api.inst;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstAdmissionApproveCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionScoreCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionSubmitCmd;
import com.zenith.admin.dto.inst.data.InstAdmissionDTO;
import com.zenith.admin.dto.inst.qry.InstAdmissionPageQuery;

/**
 * 准入申请核心服务接口
 *
 * <p>提供准入申请的全流程管理，包括草稿创建、提交评审、评分、审批等操作。
 * 遵循状态机模式，确保状态流转的合法性。</p>
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
public interface InstAdmissionService {

    /**
     * 创建准入申请草稿
     *
     * @param cmd 创建命令对象
     * @return 申请单ID
     */
    SingleResponse<Long> createDraft(InstAdmissionCreateCmd cmd);

    /**
     * 更新准入申请草稿
     *
     * <p>仅允许在 DRAFT 状态下编辑。</p>
     *
     * @param cmd 更新命令对象（需包含 admissionId）
     * @return 是否更新成功
     */
    SingleResponse<Boolean> updateDraft(InstAdmissionCreateCmd cmd);

    /**
     * 提交评审
     *
     * <p>将申请从 DRAFT 状态推进到 PENDING_REVIEW 状态，
     * 等待评分人员打分。</p>
     *
     * @param cmd 提交命令对象
     */
    SingleResponse<Void> submitForReview(InstAdmissionSubmitCmd cmd);

    /**
     * 评分
     *
     * <p>对申请进行五维度评分，计算加权总分。
     * 评分完成后状态变更为 PENDING_APPROVAL，等待最终审批。</p>
     *
     * @param cmd 评分命令对象
     */
    SingleResponse<Void> score(InstAdmissionScoreCmd cmd);

    /**
     * 退回补正
     *
     * <p>将申请退回到 DRAFT 状态，要求申请人补充材料或修改信息。</p>
     *
     * @param admissionId 申请单ID
     * @param reason      退回原因
     */
    SingleResponse<Void> returnForRevision(Long admissionId, String reason);

    /**
     * 审批通过
     *
     * <p>审批通过后自动执行：
     * <ol>
     *   <li>创建或更新机构记录</li>
     *   <li>将机构加入目标池</li>
     *   <li>设置合作状态为"合作中"</li>
     * </ol></p>
     *
     * @param cmd 审批命令对象
     */
    SingleResponse<Void> approve(InstAdmissionApproveCmd cmd);

    /**
     * 审批驳回
     *
     * @param cmd 审批命令对象（包含驳回原因）
     */
    SingleResponse<Void> reject(InstAdmissionApproveCmd cmd);

    /**
     * 撤销申请
     *
     * <p>仅非终态（APPROVED/REJECTED）的申请可以撤销。</p>
     *
     * @param admissionId 申请单ID
     */
    SingleResponse<Void> withdraw(Long admissionId);

    /**
     * 查询申请详情
     *
     * @param id 申请单ID
     * @return 申请详情（含材料和日志）
     */
    SingleResponse<InstAdmissionDTO> detail(Long id);

    /**
     * 查询我的申请列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResponse<InstAdmissionDTO> myApplications(InstAdmissionPageQuery query);

    /**
     * 查询待评分列表
     *
     * <p>返回所有处于 PENDING_REVIEW 状态的申请，按创建时间升序排列。</p>
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResponse<InstAdmissionDTO> pendingReview(InstAdmissionPageQuery query);

    /**
     * 查询待审批列表
     *
     * <p>返回所有处于 PENDING_APPROVAL 状态的申请，按创建时间升序排列。</p>
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResponse<InstAdmissionDTO> pendingApproval(InstAdmissionPageQuery query);
}
