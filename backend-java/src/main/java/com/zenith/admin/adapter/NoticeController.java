package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.NoticeService;
import com.zenith.admin.dto.NoticeDTO;
import com.zenith.admin.dto.NoticePageQuery;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public MultiResponse<NoticeDTO> list() {
        return noticeService.listAll();
    }

    @PostMapping("/page")
    public PageResponse<NoticeDTO> page(@RequestBody @Valid NoticePageQuery query) {
        return noticeService.page(query);
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody NoticeDTO noticeDTO) {
        noticeService.save(noticeDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PutMapping
    public com.alibaba.cola.dto.Response update(@RequestBody NoticeDTO noticeDTO) {
        noticeService.save(noticeDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @DeleteMapping
    public com.alibaba.cola.dto.Response delete(@RequestParam Long id) {
        noticeService.delete(id);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/get")
    public com.alibaba.cola.dto.SingleResponse<NoticeDTO> get(@RequestParam Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(noticeService.getById(id));
    }

    @PutMapping("/status")
    public com.alibaba.cola.dto.Response updateStatus(@RequestParam Long id, @RequestParam String status) {
        noticeService.updateStatus(id, status);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
