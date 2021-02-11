package com.oauth.test;

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

public class OauthTest {

    //NEED TO UPDATE THIS BASED ON AUTH SYSTEM
    private static final String clientId = "1018158525168-bgevh35rqj4fvut43il2pq7cckosvc77.apps.something.com";
    private static final String clientSecret = "NUjYgkt15D_l7Uh6rQi6mkD6asdasdasd";
    private static final String accessTokenUrl = "https://dev-example.okta.com/oauth2/default/v1/token/";
    private static final String authorizationBaseUrl = "https://dev-example.okta.com/oauth2/default/v1/authorize";
    private static final String protectedResourceUrl = "https://yourNetwork/oauth2/v3/userinfo"; // similar to https://connect2id.com/products/server/docs/api/userinfo

    //JWT SUPPORT NEED TO UPDATE THIS:
    private static final String hmacShaKeyForJWT = "someSecret";

    //NO ACTION NEEDE HERE
    private static final String NETWORK_NAME = "IDA";
    private static final String secretState = "secret" + new Random().nextInt(999_999);
    public final static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String args[]) throws Exception {

        final OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .scope("profile email") // replace with desired scope
                .state(secretState)
                .callback("https://www.comet.ml/google/google_oauth_callback")
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
        Jws<Claims> claims = jwtSupport(accessToken.getTokenType());
        String claimsJsonString = objectMapper.writeValueAsString(claims.getBody());
        System.out.println(claimsJsonString);

        //OPTIONAL STEP
        System.out.println("Refreshing the Access Token...");
        accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
        System.out.println("Refreshed the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();


        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, protectedResourceUrl);
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        String userProfileResponse = response.getBody();

        System.out.println("User profile information: ");
        System.out.println(userProfileResponse);
    }


    private static Jws<Claims> jwtSupport(String jwtToken) {
        SecretKey key = Keys.hmacShaKeyFor(hmacShaKeyForJWT.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
    }


}