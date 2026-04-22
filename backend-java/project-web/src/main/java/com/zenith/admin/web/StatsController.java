package com.zenith.admin.web;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.StatsService;
import com.zenith.admin.dto.data.StatsOverviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/overview")
    public SingleResponse<StatsOverviewDTO> getOverview() {
        StatsOverviewDTO overview = statsService.getOverview();
        return SingleResponse.of(overview);
    }
}
