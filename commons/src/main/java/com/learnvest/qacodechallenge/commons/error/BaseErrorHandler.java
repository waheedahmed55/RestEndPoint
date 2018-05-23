package com.learnvest.qacodechallenge.commons.error;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

import com.learnvest.qacodechallenge.commons.model.error.ErrorSummary;

public abstract class BaseErrorHandler {

    public static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public ErrorSummary error(HttpServletRequest request, HttpServletResponse response) {
        // the appropriate HTTP response code (e.g. 404 or 500) is automatically set by Spring
        // so here we just define response body...
        return new ErrorSummary(response.getStatus(), getErrorAttributes(request));
    }

    protected abstract Map<String, Object> getErrorAttributes(HttpServletRequest request);

}
