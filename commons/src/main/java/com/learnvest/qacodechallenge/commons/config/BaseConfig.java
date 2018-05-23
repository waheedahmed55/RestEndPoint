package com.learnvest.qacodechallenge.commons.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import com.learnvest.qacodechallenge.commons.sql.SqlStatementsFileLoader;
import com.learnvest.qacodechallenge.commons.sql.SqlStatementsFileLoaderImpl;

public class BaseConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BaseConfig.class);

    protected String dbDriverClassName;

    protected String dbDriverUrl;

    protected String dbUsername;

    protected String dbPassword;

    protected String dbMigrationLocation;

    protected int dbPoolMaxwait;

    protected boolean dbPoolRemoveabandoned;

    protected int dbPoolRemoveabandonedtimeout;

    protected boolean dbPoolLogabandoned;

    protected long dbPoolMaxage;

    protected String sqlStatementsResourceLocation;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        LOG.info("Loading DataSource");
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(dbDriverClassName);
        poolProperties.setUrl(dbDriverUrl);
        poolProperties.setUsername(dbUsername);
        poolProperties.setPassword(dbPassword);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");

        // Set additional pool properties
        poolProperties.setMaxWait(dbPoolMaxwait);
        poolProperties.setRemoveAbandoned(dbPoolRemoveabandoned);
        poolProperties.setRemoveAbandonedTimeout(dbPoolRemoveabandonedtimeout);
        poolProperties.setLogAbandoned(dbPoolLogabandoned);
        poolProperties.setMaxAge(dbPoolMaxage);

        DataSource ds = new DataSource();
        ds.setPoolProperties(poolProperties);

        LOG.info("Running database migration on {}", dbDriverUrl);
        Flyway flyway = new Flyway();
        flyway.setLocations(dbMigrationLocation.split("\\s*,\\s*"));
        flyway.setOutOfOrder(true);
        flyway.setDataSource(ds);
        flyway.repair();
        flyway.migrate();

        return ds;
    }

    @Bean
    public SqlStatementsFileLoader sqlStatementsFileLoader() {
        SqlStatementsFileLoaderImpl loader = new SqlStatementsFileLoaderImpl();
        loader.setStatementResourceLocation(sqlStatementsResourceLocation);
        return loader;
    }

    public String getDbDriverClassName() {
        return dbDriverClassName;
    }

    public void setDbDriverClassName(String dbDriverClassName) {
        this.dbDriverClassName = dbDriverClassName;
    }

    public String getDbDriverUrl() {
        return dbDriverUrl;
    }

    public void setDbDriverUrl(String dbDriverUrl) {
        this.dbDriverUrl = dbDriverUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbMigrationLocation() {
        return dbMigrationLocation;
    }

    public void setDbMigrationLocation(String dbMigrationLocation) {
        this.dbMigrationLocation = dbMigrationLocation;
    }

    public String getSqlStatementsResourceLocation() {
        return sqlStatementsResourceLocation;
    }

    public void setSqlStatementsResourceLocation(String sqlStatementsResourceLocation) {
        this.sqlStatementsResourceLocation = sqlStatementsResourceLocation;
    }

    public int getDbPoolMaxwait() {
        return dbPoolMaxwait;
    }

    public void setDbPoolMaxwait(int dbPoolMaxwait) {
        this.dbPoolMaxwait = dbPoolMaxwait;
    }

    public boolean isDbPoolRemoveabandoned() {
        return dbPoolRemoveabandoned;
    }

    public void setDbPoolRemoveabandoned(boolean dbPoolRemoveabandoned) {
        this.dbPoolRemoveabandoned = dbPoolRemoveabandoned;
    }

    public int getDbPoolRemoveabandonedtimeout() {
        return dbPoolRemoveabandonedtimeout;
    }

    public void setDbPoolRemoveabandonedtimeout(int dbPoolRemoveabandonedtimeout) {
        this.dbPoolRemoveabandonedtimeout = dbPoolRemoveabandonedtimeout;
    }

    public boolean isDbPoolLogabandoned() {
        return dbPoolLogabandoned;
    }

    public void setDbPoolLogabandoned(boolean dbPoolLogabandoned) {
        this.dbPoolLogabandoned = dbPoolLogabandoned;
    }

    public long getDbPoolMaxage() {
        return dbPoolMaxage;
    }

    public void setDbPoolMaxage(long dbPoolMaxage) {
        this.dbPoolMaxage = dbPoolMaxage;
    }

}
