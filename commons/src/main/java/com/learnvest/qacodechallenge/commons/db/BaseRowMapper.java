package com.learnvest.qacodechallenge.commons.db;

import org.springframework.jdbc.core.RowMapper;

public abstract class BaseRowMapper<T> implements RowMapper<T>, ReverseRowMapper<T> {
}
