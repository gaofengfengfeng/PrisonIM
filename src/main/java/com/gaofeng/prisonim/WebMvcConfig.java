package com.gaofeng.prisonim;

import com.didi.meta.javalib.JInterceptor;
import com.didi.meta.javalib.JMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @Author:gaofeng
 * @Date:2018-05-23
 * @Description:
 **/
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void configureMessageConverters(List converters) {
        converters.add(new JMessageConverter());
        super.configureMessageConverters(converters);

    }
}
