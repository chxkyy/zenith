package com.zenith.admin.config;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class FastJson2Config implements WebMvcConfigurer {

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();

        // ==================== 核心：时间戳 + 注解优先 ====================
        config.setDateFormat("timestamp");

        config.setWriterFeatures(
                JSONWriter.Feature.WriteNullListAsEmpty,
                JSONWriter.Feature.WriteNullStringAsEmpty,
                JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.IgnoreErrorGetter
        );

        config.setReaderFeatures(
                JSONReader.Feature.SupportSmartMatch
        );

        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));

        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 只替换 Jackson，不破坏 Spring 任何功能
        converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
        converters.add(0, fastJsonHttpMessageConverter());
    }
}