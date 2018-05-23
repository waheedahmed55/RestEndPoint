package com.learnvest.qacodechallenge.integration.requestor;

public class BaseRequestor {

    public static final int RETRY_MAX_ATTEMPTS = 3;
    public static final int RETRY_BACKOFF_DELAY = 2000;

    protected String serviceHost;

}
