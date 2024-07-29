package org.gosvea;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void testGen(){
        Map<String,Object> claims=new HashMap<>();
        claims.put("id",1);
        claims.put("icpiename","James");
        JWT.create()
                .withClaim("icpie",claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*10))
                .sign(Algorithm.HMAC256("gosveaiccpra"));
    }

    @Test
    public void testParse(){
        String token="adaldad";
        JWTVerifier jwtVerifier =JWT.require(Algorithm.HMAC256("gosveaiccpra")).build();
        DecodedJWT decodedJWT =jwtVerifier.verify(token);
        Map<String, Claim> claim=decodedJWT.getClaims();
    }
}
