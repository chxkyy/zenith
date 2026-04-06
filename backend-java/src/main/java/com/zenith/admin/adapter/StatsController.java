package com.zenith.admin.adapter;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.app.UserService;
import com.zenith.admin.app.RoleService;
import com.zenith.admin.app.NoticeService;
import com.zenith.admin.app.OperLogService;
import com.zenith.admin.app.LoginLogService;
import com.zenith.admin.app.ErrorLogService;
import com.zenith.admin.dto.UserPageQuery;
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
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private OperLogService operLogService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private ErrorLogService errorLogService;

    @GetMapping("/health")
    public SingleResponse<String> health() {
        return SingleResponse.of("ok");
    }

    @GetMapping
    public SingleResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        // In a real app, we'd have count methods in services
        UserPageQuery query = new UserPageQuery();
        query.setPageIndex(1);
        query.setPageSize(1);
        stats.put("totalUsers", userService.listByPage(query).getTotalCount());
        
        UserPageQuery activeQuery = new UserPageQuery();
        activeQuery.setPageIndex(1);
        activeQuery.setPageSize(1);
        activeQuery.setStatus(1); // 1 for active
        stats.put("activeUsers", userService.listByPage(activeQuery).getTotalCount());
        
        stats.put("totalRoles", roleService.listAll().getData().size());
        stats.put("pendingNotices", noticeService.listAll().getData().size());
        stats.put("operLogs", operLogService.listByPage(1, 1, null, null, null).getTotalCount());
        stats.put("loginLogs", loginLogService.listByPage(1, 1, null, null, null).getTotalCount());
        stats.put("errorLogs", errorLogService.listByPage(1, 1, null, null).getTotalCount());
        
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
