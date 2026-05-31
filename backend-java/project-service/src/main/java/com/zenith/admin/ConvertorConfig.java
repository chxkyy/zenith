package com.zenith.admin;

import com.zenith.admin.service.system.executor.converter.DictConvertor;
import com.zenith.admin.service.system.executor.converter.ErrorLogConvertor;
import com.zenith.admin.service.system.executor.converter.LoginLogConvertor;
import com.zenith.admin.service.system.executor.converter.MenuConvertor;
import com.zenith.admin.service.system.executor.converter.NoticeConvertor;
import com.zenith.admin.service.system.executor.converter.OperLogConvertor;
import com.zenith.admin.service.system.executor.converter.OrgConvertor;
import com.zenith.admin.service.system.executor.converter.RoleConvertor;
import com.zenith.admin.service.system.executor.converter.UserConvertor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConvertorConfig {

    @Bean
    public DictConvertor dictConvertor() {
        return DictConvertor.INSTANCE;
    }

    @Bean
    public ErrorLogConvertor errorLogConvertor() {
        return ErrorLogConvertor.INSTANCE;
    }

    @Bean
    public LoginLogConvertor loginLogConvertor() {
        return LoginLogConvertor.INSTANCE;
    }

    @Bean
    public MenuConvertor menuConvertor() {
        return MenuConvertor.INSTANCE;
    }

    @Bean
    public NoticeConvertor noticeConvertor() {
        return NoticeConvertor.INSTANCE;
    }

    @Bean
    public OperLogConvertor operLogConvertor() {
        return OperLogConvertor.INSTANCE;
    }

    @Bean
    public OrgConvertor orgConvertor() {
        return OrgConvertor.INSTANCE;
    }

    @Bean
    public RoleConvertor roleConvertor() {
        return RoleConvertor.INSTANCE;
    }

    @Bean
    public UserConvertor userConvertor() {
        return UserConvertor.INSTANCE;
    }
}
