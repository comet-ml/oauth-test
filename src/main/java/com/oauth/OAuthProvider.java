package com.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class OAuthProvider extends DefaultApi20 {

    private String accessTokenEndpoint;
    private String authorizationBaseUrl;

    public OAuthProvider(String accessTokenEndpoint, String authorizationBaseUrl) {
        this.accessTokenEndpoint = accessTokenEndpoint;
        this.authorizationBaseUrl = authorizationBaseUrl;
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
