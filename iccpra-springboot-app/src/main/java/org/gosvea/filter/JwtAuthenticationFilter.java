package org.gosvea.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gosvea.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil; //

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 跳过登录路径，不执行 JWT 验证
        String requestPath = request.getServletPath();
        if ("/icpie/login".equals(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 获取 Authorization 头
        String token = request.getHeader("Authorization");
        System.out.println("Authorization header: " + token);
     // 确认 token 是否不为空且符合 Bearer 前缀
        if (token != null) {
            System.out.println("Token exists: " + token);
            if (token.startsWith("Bearer ")) {
                System.out.println("Token starts with 'Bearer '");
                token = token.substring(7); // 去掉 "Bearer " 前缀
                try {
                    // 解析 Token，获取 claims
                    Map<String, Object> claims = jwtUtil.parseToken(token);

                    // 获取用户名和角色
                    String role = (String) claims.get("role");
                    String username = (String) claims.get("icpiename");

                    System.out.println("Parsed claims: " + claims);
                    System.out.println("Role: " + role);

                    // 创建权限列表
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

                    // 将用户信息和角色权限存储到 SecurityContext 中
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 打印调试信息
                    System.out.println("Authenticated user: " + username);
                    System.out.println("Authorities: " + authorities);
                } catch (Exception e) {
                    System.out.println("Error parsing token: " + e.getMessage());
                    e.printStackTrace(); // 打印异常
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 解析失败，返回 401
                    return;
                }
            } else {
                System.out.println("Token does not start with 'Bearer '");
            }
        } else {
            System.out.println("Authorization header is null or empty");
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
