package com.zenith.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "session")
public class SessionProperties {

    private int maxConcurrent = 5;

    private int timeoutMinutes = 120;

    private int kickedRecordExpireMinutes = 10;
}
