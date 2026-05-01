package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.NoticeService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.NoticeDTO;
import com.zenith.admin.dto.data.NoticePageQuery;
import com.zenith.admin.dto.data.NoticeStatusUpdateCmd;
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
    public com.alibaba.cola.dto.Response save(@RequestBody NoticeDTO noticeDTO) {
        noticeService.save(noticeDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/update")
    public com.alibaba.cola.dto.Response update(@RequestBody NoticeDTO noticeDTO) {
        noticeService.save(noticeDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        noticeService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<NoticeDTO> get(@RequestParam Long id) {
        return SingleResponse.of(noticeService.getById(id));
    }

    @PostMapping("/status")
    public com.alibaba.cola.dto.Response updateStatus(@RequestBody @Validated NoticeStatusUpdateCmd cmd) {
        noticeService.updateStatus(cmd.getId(), cmd.getStatus());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
