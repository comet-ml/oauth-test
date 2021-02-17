package com.oauth;

import com.github.scribejava.core.builder.ServiceBuilderOAuth20;
import com.github.scribejava.core.builder.api.DefaultApi20;

public class NoSecretJWT extends DefaultApi20 {
    private static String accessTokenEndpoint = "";
    private static String authorizationBaseUrl = "";

    private static class InstanceHolder {
        private static final NoSecretJWT INSTANCE = new NoSecretJWT();
    }

    public static NoSecretJWT instance() {
        return InstanceHolder.INSTANCE;
    }

    public static void setAuthorizationBaseUrl(String url){
        authorizationBaseUrl = url;
    }

    public static void setAccessTokenEndPoint(String url){
        accessTokenEndpoint = url;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return accessTokenEndpoint;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return authorizationBaseUrl;
    }
}
