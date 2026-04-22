package com.zenith.admin.api;

import java.util.Map;

public interface StatsService {
    Map<String, Object> getOverview();
    Map<String, Object> getUserStats();
    Map<String, Object> getRoleStats();
    Map<String, Object> getOrgStats();
    Map<String, Object> getMenuStats();
    Map<String, Object> getLogStats();
}
