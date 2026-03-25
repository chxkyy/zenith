package com.zenith.admin.adapter;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.app.NoticeService;
import com.zenith.admin.dto.NoticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public MultiResponse<NoticeDTO> list() {
        return noticeService.listAll();
    }

    @PostMapping
    public com.alibaba.cola.dto.Response save(@RequestBody NoticeDTO noticeDTO) {
        noticeService.save(noticeDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @PutMapping
    public com.alibaba.cola.dto.Response update(@RequestBody NoticeDTO noticeDTO) {
        noticeService.update(noticeDTO);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @DeleteMapping("/{id}")
    public com.alibaba.cola.dto.Response delete(@PathVariable Long id) {
        noticeService.delete(id);
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/{id}")
    public com.alibaba.cola.dto.SingleResponse<NoticeDTO> get(@PathVariable Long id) {
        return com.alibaba.cola.dto.SingleResponse.of(noticeService.getById(id));
    }
}
