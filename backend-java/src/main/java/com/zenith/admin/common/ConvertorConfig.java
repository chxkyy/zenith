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
        return new com.zenith.admin.infrastructure.convertor.DictConvertorImpl();
    }

    @Bean
    public ErrorLogConvertor errorLogConvertor() {
        return new com.zenith.admin.infrastructure.convertor.ErrorLogConvertorImpl();
    }

    @Bean
    public LoginLogConvertor loginLogConvertor() {
        return new com.zenith.admin.infrastructure.convertor.LoginLogConvertorImpl();
    }

    @Bean
    public MenuConvertor menuConvertor() {
        return new com.zenith.admin.infrastructure.convertor.MenuConvertorImpl();
    }

    @Bean
    public NoticeConvertor noticeConvertor() {
        return new com.zenith.admin.infrastructure.convertor.NoticeConvertorImpl();
    }

    @Bean
    public OperLogConvertor operLogConvertor() {
        return new com.zenith.admin.infrastructure.convertor.OperLogConvertorImpl();
    }

    @Bean
    public OrgConvertor orgConvertor() {
        return new com.zenith.admin.infrastructure.convertor.OrgConvertorImpl();
    }

    @Bean
    public RoleConvertor roleConvertor() {
        return new com.zenith.admin.infrastructure.convertor.RoleConvertorImpl();
    }

    @Bean
    public UserConvertor userConvertor() {
        return new com.zenith.admin.infrastructure.convertor.UserConvertorImpl();
    }
}
