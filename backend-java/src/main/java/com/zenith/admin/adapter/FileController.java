package com.zenith.admin.adapter;

import com.alibaba.cola.dto.PageResponse;
import com.zenith.admin.app.FileService;
import com.zenith.admin.dto.FileDTO;
import com.zenith.admin.dto.FilePageQuery;
import com.zenith.admin.dto.IdQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/page")
    public PageResponse<FileDTO> page(@RequestBody @Valid FilePageQuery query) {
        return fileService.page(query);
    }

    @PostMapping("/upload")
    public com.alibaba.cola.dto.SingleResponse<FileDTO> upload(@RequestParam("file") MultipartFile file) throws IOException {
        FileDTO fileDTO = fileService.upload(file);
        return com.alibaba.cola.dto.SingleResponse.of(fileDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws MalformedURLException {
        FileDTO fileDTO = fileService.getById(id);
        if (fileDTO == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(fileDTO.getPath().substring(1));
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDTO.getOriginalName() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        fileService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }
}
