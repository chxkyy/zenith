package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.NoticeService;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.NoticeAddCmd;
import com.zenith.admin.dto.data.NoticeDTO;
import com.zenith.admin.dto.data.NoticePageQuery;
import com.zenith.admin.dto.data.NoticeStatusUpdateCmd;
import com.zenith.admin.dto.data.NoticeUpdateCmd;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public MultiResponse<NoticeDTO> list() {
        List<NoticeDTO> list = noticeService.listAll();
        return MultiResponse.of(list);
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<NoticeDTO> page(@RequestBody @Valid NoticePageQuery query) {
        PageInfo<NoticeDTO> pageInfo = noticeService.page(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public Response save(@RequestBody @Valid NoticeAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        noticeService.save(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid NoticeUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        noticeService.update(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody IdQuery query) {
        noticeService.delete(query.getId());
        return Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<NoticeDTO> get(@RequestParam Long id) {
        return SingleResponse.of(noticeService.getById(id));
    }

    @PostMapping("/status")
    public Response updateStatus(@RequestBody @Validated NoticeStatusUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        noticeService.updateStatus(cmd.getId(), cmd.getStatus(), currentUserId);
        return Response.buildSuccess();
    }
}
