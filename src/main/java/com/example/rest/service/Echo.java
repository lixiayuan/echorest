package com.example.rest.service;

public class Echo {
    private final String queryStringParams ;
    private final Greeting requestBody;
    private final String requestIP;
    private final String userAgent;


    public Echo(String queryStringParams, Greeting requestBody, String requestIP, String userAgent) {
        this.queryStringParams = queryStringParams;
        this.requestBody = requestBody;
        this.requestIP = requestIP;
        this.userAgent = userAgent;
    }

    public String getQueryStringParams() {
        return queryStringParams;
    }

    public Greeting getRequestBody() {
        return requestBody;
    }

    public String getRequestIP() {
        return requestIP;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
