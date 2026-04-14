package com.zenith.admin.adapter;

import com.zenith.admin.app.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        return statsService.getOverview();
    }

    @GetMapping("/users")
    public Map<String, Object> getUserStats() {
        return statsService.getUserStats();
    }

    @GetMapping("/roles")
    public Map<String, Object> getRoleStats() {
        return statsService.getRoleStats();
    }

    @GetMapping("/orgs")
    public Map<String, Object> getOrgStats() {
        return statsService.getOrgStats();
    }

    @GetMapping("/menus")
    public Map<String, Object> getMenuStats() {
        return statsService.getMenuStats();
    }

    @GetMapping("/logs")
    public Map<String, Object> getLogStats() {
        return statsService.getLogStats();
    }
}
