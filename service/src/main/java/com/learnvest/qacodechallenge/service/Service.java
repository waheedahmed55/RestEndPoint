package com.learnvest.qacodechallenge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableAutoConfiguration
@ComponentScan
public class Service extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(Service.class);

    private ApplicationContext applicationContext;

    public static void main(String[] args) throws Exception {
        Thread.currentThread().setName("Service-Service");
        SpringApplication.run(Service.class, args);
    }

    /**
     * http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/env/SystemEnvironmentPropertySource.html
     * @param applicationContext {@link org.springframework.context.ApplicationContext}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        LOG.trace("Setting application context in Service");
        this.applicationContext = applicationContext;
        LOG.info("SPRING_PROFILES_ACTIVE={}", System.getenv("SPRING_PROFILES_ACTIVE"));
    }

    //@Override
    //public void addInterceptors(InterceptorRegistry registry) {
    //    registry.addInterceptor(applicationContext.getBean(ServiceHandlerInterceptor.class));
    //}

}
