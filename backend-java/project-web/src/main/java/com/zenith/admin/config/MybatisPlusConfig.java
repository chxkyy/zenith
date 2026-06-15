package com.zenith.admin.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.zenith.admin.aspect.DataPermissionInnerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置
 * <p>
 * 注册数据权限拦截器（DataPermissionInnerInterceptor）。
 * 注意：项目使用 PageHelper 处理分页，无需注册 MP 的分页拦截器。
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class MybatisPlusConfig {

    private final DataPermissionInnerInterceptor dataPermissionInnerInterceptor;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 数据权限 SQL 改写拦截器
        interceptor.addInnerInterceptor(dataPermissionInnerInterceptor);

        return interceptor;
    }
}
