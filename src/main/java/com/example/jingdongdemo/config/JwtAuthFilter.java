package com.example.jingdongdemo.config;

import com.example.jingdongdemo.common.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 * 每个请求只执行一次（OncePerRequestFilter 保证）
 *
 * 执行流程：
 * 1. 从请求头 Authorization 里取出 token
 * 2. 用 JwtUtils 解析出 userId
 * 3. 把 userId 存到 SecurityContextHolder（Spring Security 的上下文）
 * 4. 放行请求
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    // 加这两行
    private final JwtUtils jwtUtils;
    public JwtAuthFilter(JwtUtils jwtUtils) { this.jwtUtils = jwtUtils; }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        System.out.println("JwtAuthFilter 被调用了，请求路径: " + request.getRequestURI());
        // 1. 从请求头获取 token
        String authHeader = request.getHeader("Authorization");

        System.out.println("拿到的 Authorization 头: [" + authHeader + "]");

        // 2. 如果有 token 且格式正确（Bearer xxx）
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer " 前缀

            // 3. 解析 token 拿到 userId
            Long userId = jwtUtils.getUserId(token);

            // 4. 如果解析成功，把用户信息存入 Spring Security 上下文
            if (userId != null) {
                //从token解析角色，赋给Security上下文；旧token没有role默认当普通用户
                String role = jwtUtils.getRole(token);
                String authority = "ROLE_" + (role != null ? role : "USER");
                // UsernamePasswordAuthenticationToken 是 Spring Security 的标准认证对象
                // 第二个参数 null 是密码（不需要），第三个参数是权限列表（先给空集合）
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(new SimpleGrantedAuthority(authority)));

                // 存入上下文，后续 Controller 里可以随时取
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 5. 放行，让请求继续走到 Controller
        filterChain.doFilter(request, response);
    }
}
