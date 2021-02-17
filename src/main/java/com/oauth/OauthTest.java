package com.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;
import java.util.Base64;

public class OauthTest {

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
        OAuth2AccessToken accessToken = service.getAccessToken(code);
        System.out.println("Got the Access Token! type: " + accessToken.getTokenType());
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");


        System.out.println("Lets check if we can read the  JWT Access Token...");
        String claimsJsonString = jwtDecodeSupport(accessToken.getTokenType());
        System.out.println(claimsJsonString);
    }

    private static String jwtDecodeSupport(String jwtToken) {
        return Base64.getDecoder().decode(jwtToken).toString();
    }
}