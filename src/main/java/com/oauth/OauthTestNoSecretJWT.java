package com.oauth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

public class OauthTestNoSecretJWT {
    public static CloseableHttpClient httpClient;

    //NEED TO UPDATE THIS BASED ON AUTH SYSTEM
    private static final String clientId = "*** Please enter Client ID Here ***";
    private static final String accessTokenUrl = "*** Please enter Token URL Here ***";
    private static final String authorizationBaseUrl = "*** Please enter Authorize URL Here ***";
    private static final String callBackUrl = "http://localhost";

    //NO ACTION NEEDE HERE
    private static final String NETWORK_NAME = "IDA";
    private static final String secretState = "secret" + new Random().nextInt(999_999);

    public static void main(String args[]) throws Exception {

        final OAuth20Service service = new ServiceBuilder(clientId)
                .scope("profile email") // replace with desired scope
                .state(secretState)
                .callback(callBackUrl)
                .build(new OAuthProvider(accessTokenUrl, authorizationBaseUrl));

        final Scanner in = new Scanner(System.in, "UTF-8");
        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        final String authorizationUrl = service.getAuthorizationUrl();

        System.out.println("1) Go and authorize here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final String code = in.nextLine();
        System.out.println();

        System.out.println("And paste the state from server here. We have set 'state'='" + secretState + "'.");
        System.out.print(">>");
        final String returnedState = in.nextLine();
        System.out.println("returnedState: " + returnedState + " Should be equal to input state: " + secretState);

        System.out.println("Trading the Request Token for an Access Token...");
        //OAuth2AccessToken accessToken = service.getAccessToken(code);
        OAuth2AccessToken accessToken = customAccessTokenRequest(code);
        System.out.println("Got the Access Token! type: " + accessToken.getTokenType());
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");


        System.out.println("Lets check if we can read the  JWT Access Token...");
        String claimsJsonString = jwtDecodeSupport(accessToken.getTokenType());
        System.out.println(claimsJsonString);
    }

    private static String jwtDecodeSupport(String jwtToken) {
        return Base64.getDecoder().decode(jwtToken).toString();
    }

    private static  OAuth2AccessToken customAccessTokenRequest(String code) throws IOException, URISyntaxException {
        httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        String responseStr = null;

        URIBuilder builder = new URIBuilder(accessTokenUrl);
        builder.setParameter("client_id", clientId);
        builder.setParameter("code", code);

        HttpGet request = new HttpGet(builder.build());
        String authHeader = String.format("Basic %s", clientId);
        request.addHeader("Authorization", authHeader);

        HttpContext  httpContext = new BasicHttpContext();
        HttpResponse response = httpClient.execute(request, httpContext);
        httpClient.close();

        if (response.getEntity() != null) {
            responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        }

        String retrievedToken = "Must be parsed from Response";
        return new OAuth2AccessToken(retrievedToken, responseStr);
    }
}
