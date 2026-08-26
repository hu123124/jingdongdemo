package com.example.jingdongdemo.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 工具类 —— 生成和校验 token
 *
 * 核心概念：
 * - JWT 由 3 部分组成：Header.Payload.Signature（用 . 分隔）
 * - Payload 存业务数据（比如 userId）
 * - Signature 用密钥签名，防止被篡改
 */
@Component
public class JwtUtils {

    private final SecretKey secretKey;

    /** token 有效期：30 天 */
    private static final long EXPIRATION = 30L * 24 * 60 * 60 * 1000;

    /** 构造器注入，密钥从 application.properties 读取 */
    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 token
     * @param userId 用户ID
     * @return JWT token 字符串
     */
    public String generateToken(long userId) {
        return generateToken(userId, "USER");
    }
    public String generateToken(long userId, String  role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRATION);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role",role)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从 token 中解析出 userId
     * @param token JWT token
     * @return userId，解析失败返回 null
     */
    public Long getUserId(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Long.valueOf(subject);
        } catch (Exception e) {
            return null;
        }
    }
    /** 解析 token 里的角色；旧 token 没有 role 时返回 null，过滤器会兜底成 USER */
    public String getRole(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
