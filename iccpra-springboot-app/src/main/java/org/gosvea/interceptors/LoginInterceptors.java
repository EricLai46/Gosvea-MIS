package org.gosvea.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gosvea.pojo.Result;
import org.gosvea.utils.JwtUtil;

import org.gosvea.utils.ThreadLocalUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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
            return false;
        }
        //令牌验证
        String token=request.getHeader("Authorization");

        //验证token
        try {
            Map<String,Object> claims= JwtUtil.parseToken(token);

            //把业务数据存储到threadlocal中
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            //http response code:401
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
