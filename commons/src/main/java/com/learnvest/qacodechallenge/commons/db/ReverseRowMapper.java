package com.learnvest.qacodechallenge.commons.db;

import java.util.Map;

public interface ReverseRowMapper<T> {

    Map<String,Object> mapObject(T object);

}
