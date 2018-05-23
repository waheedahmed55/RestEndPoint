package com.learnvest.qacodechallenge.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.learnvest.qacodechallenge.commons.config.BaseConfig;
import com.learnvest.qacodechallenge.service.db.CardDao;
import com.learnvest.qacodechallenge.service.db.CardDaoRowMapper;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConfigurationProperties(prefix = "service")
@EnableSwagger2
public class ServiceConfig extends BaseConfig {

    @Bean
    CardDaoRowMapper cardRowMapper() {
        return new CardDaoRowMapper();
    }

    @Bean
    CardDao cardDao() {
        CardDao cardDao = new CardDao();
        cardDao.setDataSource(dataSource());
        cardDao.setSqlStatementsFileLoader(sqlStatementsFileLoader());
        cardDao.setRowMapper(cardRowMapper());
        return cardDao;
    }

}
