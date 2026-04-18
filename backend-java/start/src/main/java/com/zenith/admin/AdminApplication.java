package com.zenith.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.zenith.admin.mapper")
@Slf4j
public class AdminApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AdminApplication.class, args);
        
        // 检查 RoleNameAspect Bean 是否存在
        if (context.containsBean("roleNameAspect")) {
            log.info("========== RoleNameAspect Bean 存在 ==========");
        } else {
            log.error("========== RoleNameAspect Bean 不存在 ==========");
        }
        
        // 打印所有 Bean 名称
        String[] beanNames = context.getBeanDefinitionNames();
        log.info("========== 容器中所有 Bean 数量: {} ==========", beanNames.length);
    }
}
