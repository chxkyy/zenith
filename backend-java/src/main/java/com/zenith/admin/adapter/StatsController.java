package com.zenith.admin.adapter;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.domain.gateway.UserGateway;
import com.zenith.admin.domain.gateway.RoleGateway;
import com.zenith.admin.domain.gateway.NoticeGateway;
import com.zenith.admin.domain.gateway.OperLogGateway;
import com.zenith.admin.domain.gateway.LoginLogGateway;
import com.zenith.admin.domain.gateway.ErrorLogGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private UserGateway userGateway;

    @Autowired
    private RoleGateway roleGateway;

    @Autowired
    private NoticeGateway noticeGateway;

    @Autowired
    private OperLogGateway operLogGateway;

    @Autowired
    private LoginLogGateway loginLogGateway;

    @Autowired
    private ErrorLogGateway errorLogGateway;

    @GetMapping("/health")
    public SingleResponse<String> health() {
        return SingleResponse.of("ok");
    }

    @GetMapping
    public SingleResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        // In a real app, we'd have count methods in gateways
        stats.put("totalUsers", userGateway.listByPage(1, 1).getTotal());
        stats.put("activeUsers", userGateway.listByPage(1, 1).getTotal()); // Mock active
        stats.put("totalRoles", roleGateway.listAll().size());
        stats.put("pendingNotices", noticeGateway.listAll().size());
        stats.put("operLogs", operLogGateway.listByPage(1, 1, null, null, null).getTotal());
        stats.put("loginLogs", loginLogGateway.listByPage(1, 1, null, null, null).getTotal());
        stats.put("errorLogs", errorLogGateway.listByPage(1, 1, null, null).getTotal());
        
        // Mock chart data
        java.util.List<Map<String, Object>> chartData = new java.util.ArrayList<>();
        chartData.add(createDataPoint("1月", 400));
        chartData.add(createDataPoint("2月", 300));
        chartData.add(createDataPoint("3月", 600));
        chartData.add(createDataPoint("4月", 800));
        chartData.add(createDataPoint("5月", 500));
        chartData.add(createDataPoint("6月", 900));
        stats.put("chartData", chartData);
        
        return SingleResponse.of(stats);
    }

    private Map<String, Object> createDataPoint(String name, int value) {
        Map<String, Object> point = new HashMap<>();
        point.put("name", name);
        point.put("value", value);
        return point;
    }
}
