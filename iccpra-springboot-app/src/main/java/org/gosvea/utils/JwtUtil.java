package org.gosvea.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtUtil {

    //reveive data, create token and return
    private static final String KEY="gosveaiccpra";

    public static String genToken(Map<String, Object> claims){
        return JWT.create()
                .withClaim("claims",claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*12))
                .sign(Algorithm.HMAC256(KEY));
    }

    //receive token, verify token return data
    public static  Map<String,Object> parseToken(String token){
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token);
        // 输出 JWT 中的所有信息，帮助调试
        System.out.println("Header: " + decodedJWT.getHeader());
        System.out.println("Payload: " + decodedJWT.getPayload());
        System.out.println("Claims: " + decodedJWT.getClaims());


        // 获取 "claims" 字段
        Map<String, Object> claims = decodedJWT.getClaim("claims").asMap();
        System.out.println("Parsed claims: " + claims);

        // 获取 claims 中的具体字段
        String role = claims.get("role").toString();  // 确保你想要的是 role 字段
        System.out.println("Role from token: " + role);

        // 将 claims 中的信息保存到 Map 中返回
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("role", role);
        claimsMap.put("firstname", claims.get("firstname"));
        claimsMap.put("icpiename", claims.get("icpiename"));
        claimsMap.put("id", claims.get("id"));

        return claimsMap;
    }

    // 提取用户角色并进行权限判断
    public static String getUserRoleFromToken(String token) {
        Map<String, Object> claims = parseToken(token); // 解析 token 获取 claims
        String role = (String) claims.get("role"); // 提取 role
        return role;
    }

    // 示例：判断是否为指定角色
    public static boolean hasRole(String token, String requiredRole) {
        String role = getUserRoleFromToken(token); // 从 token 中获取用户角色
        return requiredRole.equals(role); // 比较是否拥有该角色
    }
}
