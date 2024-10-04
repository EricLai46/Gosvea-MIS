package org.gosvea.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gosvea.pojo.Result;
import org.gosvea.utils.JwtUtil;

import org.gosvea.utils.ThreadLocalUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
@Component
public class LoginInterceptors implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        // 处理CORS预检请求
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS");
            response.setHeader("Access-Control-Max-Age", "1800");
            response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type");
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }
        //令牌验证
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // 去掉 "Bearer " 前缀
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;  // 没有正确的 Authorization 头，返回 401
        }

        //验证token
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            System.out.println("Parsed claims: " + claims);  // 输出解析的claims

            // 确保 claims 中包含角色信息
            String role = (String) claims.get("role");
            System.out.println("User role from token: " + role);  // 打印角色信息

            if (!"ROLE_ICPIE".equals(role)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());  // 没有匹配角色，返回403
                return false;
            }

            // 将用户认证信息存储到 Spring Security 的 SecurityContext 中
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims.get("icpiename"), null, Collections.singletonList(new SimpleGrantedAuthority(role))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 打印 SecurityContext 中的认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authenticated user: " + auth.getName());
            System.out.println("Roles: " + auth.getAuthorities());
            // 把业务数据存储到 threadlocal 中
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            //http response code:401
            e.printStackTrace();
            response.setStatus(401);
            return false;
        }




//
//        // 处理JWT验证
//        String token = request.getHeader("Authorization");
//
//        try {
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7); // 移除 "Bearer " 前缀
//            } else {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return false;
//            }
//
//            Map<String, Object> claims = JwtUtil.parseToken(token);
//
//            // 把业务数据存储到threadlocal中
//            ThreadLocalUtil.set(claims);
//            return true;
//        } catch (Exception e) {
//            // HTTP response code: 401
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}
