package com.learnvest.qacodechallenge.commons.sql;

public interface SqlStatementsFileLoader {

    String sql(String name);

    void setStatementResourceLocation(String statementResourceLocation);

    String getStatementResourceLocation();

}
