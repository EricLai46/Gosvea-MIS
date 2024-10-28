package org.gosvea.config;


import org.gosvea.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig  {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 这里使用 lambda 替代被弃用的 csrf() 方法
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/icpie/login").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/api/icpie/**").hasRole("ICPIE")
                        .requestMatchers("/api/icpim/**").hasAnyRole("ICPIM", "ICPIE")
                        .requestMatchers("/api/icpis/**").hasAnyRole("ICPIS", "ICPIM", "ICPIE")
                        .requestMatchers("/venue/**").authenticated()  // 确保 /venue 路径需要认证
                        .requestMatchers("/instructor/**").authenticated()  // 确保 /instructor 路径需要认证
                        .anyRequest().authenticated()

                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // 在用户名密码验证之前添加 JWT 验证
                .httpBasic(httpBasic -> httpBasic.disable());  // 禁用 HTTP Basic 认证

        return http.build();
    }




}
