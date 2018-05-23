package com.learnvest.qacodechallenge.commons.sql;

import org.springframework.beans.BeansException;

public class SqlStatementsFileResourceException extends BeansException {

    private static final long serialVersionUID = 1551532974211387214L;

    public SqlStatementsFileResourceException(String msg) {
        super(msg);
    }

    public SqlStatementsFileResourceException(String msg, Throwable e) {
        super(msg,e);
    }

}
