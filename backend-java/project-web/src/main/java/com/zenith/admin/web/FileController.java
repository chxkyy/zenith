package com.zenith.admin.web;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.FileService;
import com.zenith.admin.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.data.FileDTO;
import com.zenith.admin.dto.data.FilePageQuery;
import com.zenith.admin.dto.data.IdQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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
    public com.alibaba.cola.dto.PageResponse<FileDTO> page(@RequestBody @Valid FilePageQuery query) {
        PageInfo<FileDTO> pageInfo = fileService.page(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/upload")
    public SingleResponse<FileDTO> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Long currentUserId = UserContext.getUserId();
        FileDTO fileDTO = fileService.upload(
                file.getBytes(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                currentUserId
        );
        return SingleResponse.of(fileDTO);
    }

    @GetMapping
    public ResponseEntity<Resource> download(@RequestParam Long id) throws MalformedURLException {
        FileDTO fileDTO = fileService.getById(id);
        if (fileDTO == null) {
            return ResponseEntity.notFound().build();
        }

        String pathStr = fileDTO.getPath();
        if (pathStr != null && pathStr.startsWith("/")) {
            pathStr = pathStr.substring(1);
        }
        Path filePath = Paths.get(pathStr);
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
    public Response delete(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        fileService.delete(query.getId(), currentUserId);
        return Response.buildSuccess();
    }
}
