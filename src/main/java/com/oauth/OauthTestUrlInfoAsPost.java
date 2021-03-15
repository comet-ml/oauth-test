package com.oauth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.Random;
import java.util.Scanner;

public class OauthTestUrlInfoAsPost {

    //NEED TO UPDATE THIS BASED ON AUTH SYSTEM
    private static final String scope = "profile email";
    private static final String clientId = "1018158525168-bgevh35rqj4fvut43il2pq7cckosvc77.apps.something.com";
    private static final String clientSecret = "NUjYgkt15D_l7Uh6rQi6mkD6asdasdasd";
    private static final String accessTokenUrl = "https://dev-example.okta.com/oauth2/default/v1/token/";
    private static final String authorizationBaseUrl = "https://dev-example.okta.com/oauth2/default/v1/authorize";
    private static final String protectedResourceUrl = "https://yourNetwork/oauth2/v3/userinfo"; // similar to https://connect2id.com/products/server/docs/api/userinfo

    //NO ACTION NEEDED HERE
    private static final String NETWORK_NAME = "Test";
    private static final String secretState = "secret" + new Random().nextInt(999_999);

    public static void main(String args[]) throws Exception {

        final OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
//                .defaultScope(scope)
                .callback("https://www.comet.ml/oauth_callback")
                .build(new OAuthProvider(accessTokenUrl, authorizationBaseUrl));

        final Scanner in = new Scanner(System.in, "UTF-8");
        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        final String authorizationUrl = service.getAuthorizationUrl(secretState);

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
        OAuth2AccessToken accessToken = service.getAccessToken(code);
        System.out.println("Got the Access Token! type: " + accessToken.getTokenType());
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");

        OAuthRequest request = new OAuthRequest(Verb.POST, protectedResourceUrl);
        //service.signRequest(accessToken, request);
        request.addBodyParameter("token", accessToken.getAccessToken());
        //request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        //request.addHeader("Authorization", "Basic client");
        //request.addHeader("api-version", "3");

        final Response response = service.execute(request);
        String userProfileResponse = response.getBody();

        System.out.println("User profile information: ");
        System.out.println(userProfileResponse);
    }

}
