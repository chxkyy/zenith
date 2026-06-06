package com.zenith.admin.web;

import com.zenith.admin.api.system.OperLogService;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.dto.data.IdQuery;
import com.zenith.admin.dto.data.OperLogDTO;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/oper-logs")
@RequiredArgsConstructor
public class OperLogController {

    private final OperLogService operLogService;

    @GetMapping
    public com.alibaba.cola.dto.PageResponse<OperLogDTO> list(
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String result) {
        PageInfo<OperLogDTO> pageInfo = operLogService.listByPage(pageIndex, pageSize, operator, module, result);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/delete")
    public com.alibaba.cola.dto.Response delete(@RequestBody IdQuery query) {
        operLogService.delete(query.getId());
        return com.alibaba.cola.dto.Response.buildSuccess();
    }

    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String result,
            HttpServletResponse response) throws Exception {
        List<OperLogDTO> list = operLogService.listAll(operator, module, result);

        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String fileName = URLEncoder.encode("操作记录", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".csv");

        PrintWriter writer = response.getWriter();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        writer.println("\uFEFF时间,模块,操作内容,操作人,IP地址,结果,备注");

        for (OperLogDTO log : list) {
            StringBuilder sb = new StringBuilder();
            sb.append(log.getCreatedTime() != null ? log.getCreatedTime().format(formatter) : "").append(",");
            sb.append(escapeCsv(log.getModule())).append(",");
            sb.append(escapeCsv(log.getContent())).append(",");
            sb.append(escapeCsv(log.getOperator())).append(",");
            sb.append(log.getIp() != null ? log.getIp() : "").append(",");
            sb.append(log.getResult() != null ? log.getResult() : "").append(",");
            sb.append(escapeCsv(log.getRemark()));
            writer.println(sb.toString());
        }

        writer.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
