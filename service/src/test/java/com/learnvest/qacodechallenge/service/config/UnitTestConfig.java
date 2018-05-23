package com.learnvest.qacodechallenge.service.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import com.learnvest.qacodechallenge.commons.sql.SqlStatementsFileLoader;
import com.learnvest.qacodechallenge.commons.sql.SqlStatementsFileLoaderImpl;
import com.learnvest.qacodechallenge.service.db.CardDao;
import com.learnvest.qacodechallenge.service.db.CardDaoRowMapper;

@Configuration
@EnableAutoConfiguration
@PropertySources({ @PropertySource("classpath:application.properties"), @PropertySource("classpath:test.properties") })
public class UnitTestConfig {

    @Autowired
    Environment env;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getProperty("service.db-driver-class-name"));
        ds.setUrl(env.getProperty("service.db-driver-url"));
        ds.setUsername(env.getProperty("service.db-username"));
        ds.setPassword(env.getProperty("service.db-password"));

        Flyway flyway = new Flyway();
        flyway.setLocations(env.getProperty("service.db-migration-location").split("\\s*,\\s*"));
        flyway.setOutOfOrder(true);
        flyway.setDataSource(ds);
        flyway.clean();
        flyway.migrate();

        return ds;
    }

    @Bean
    public SqlStatementsFileLoader sqlStatementsFileLoader() {
        SqlStatementsFileLoader loader = new SqlStatementsFileLoaderImpl();
        loader.setStatementResourceLocation(env.getProperty("service.sql-statements-resource-location"));
        return loader;
    }

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
