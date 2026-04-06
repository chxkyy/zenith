package com.zenith.admin.common;

import com.zenith.admin.infrastructure.convertor.DictConvertor;
import com.zenith.admin.infrastructure.convertor.ErrorLogConvertor;
import com.zenith.admin.infrastructure.convertor.LoginLogConvertor;
import com.zenith.admin.infrastructure.convertor.MenuConvertor;
import com.zenith.admin.infrastructure.convertor.NoticeConvertor;
import com.zenith.admin.infrastructure.convertor.OperLogConvertor;
import com.zenith.admin.infrastructure.convertor.OrgConvertor;
import com.zenith.admin.infrastructure.convertor.RoleConvertor;
import com.zenith.admin.infrastructure.convertor.UserConvertor;
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
        return com.zenith.admin.infrastructure.convertor.ErrorLogConvertor.INSTANCE;
    }

    @Bean
    public LoginLogConvertor loginLogConvertor() {
        return com.zenith.admin.infrastructure.convertor.LoginLogConvertor.INSTANCE;
    }

    @Bean
    public MenuConvertor menuConvertor() {
        return com.zenith.admin.infrastructure.convertor.MenuConvertor.INSTANCE;
    }

    @Bean
    public NoticeConvertor noticeConvertor() {
        return com.zenith.admin.infrastructure.convertor.NoticeConvertor.INSTANCE;
    }

    @Bean
    public OperLogConvertor operLogConvertor() {
        return com.zenith.admin.infrastructure.convertor.OperLogConvertor.INSTANCE;
    }

    @Bean
    public OrgConvertor orgConvertor() {
        return com.zenith.admin.infrastructure.convertor.OrgConvertor.INSTANCE;
    }

    @Bean
    public RoleConvertor roleConvertor() {
        return com.zenith.admin.infrastructure.convertor.RoleConvertor.INSTANCE;
    }

    @Bean
    public UserConvertor userConvertor() {
        return com.zenith.admin.infrastructure.convertor.UserConvertor.INSTANCE;
    }
}
