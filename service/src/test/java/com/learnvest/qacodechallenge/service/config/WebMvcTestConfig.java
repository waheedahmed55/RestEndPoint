package com.learnvest.qacodechallenge.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableAutoConfiguration
@PropertySources({ @PropertySource("classpath:application.properties"), @PropertySource("classpath:test.properties") })
public class WebMvcTestConfig extends WebMvcConfigurerAdapter {

    @Autowired
    Environment env;

    //@Override
    //public void addInterceptors(InterceptorRegistry registry) {
    //    registry.addInterceptor( );
    //}

}
