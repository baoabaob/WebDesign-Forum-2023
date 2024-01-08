package cc.ccake.forumApp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenUtil {
    private final String base64Secret = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE+QS1HicgKSAjVaBP97V55nH1jOli" +
            "lZ0bPWREs3mBdGkjx6Axc/Tf/V16Z/h06jbxlIh3j/jRJxU/3RWw4I4FIQ=="; // Base64 编码密钥
    private final SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
    private final long validityInMilliseconds = 3600000 * 24; // 1天有效期
    private ConcurrentHashMap<String, Date> tokenBlacklist = new ConcurrentHashMap<>();

    // 生成JWT令牌
    public String createToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey);

        return builder.compact();
    }

    //从http请求中解析token
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 从JWT令牌中获取用户名
    public String getUsername(String token) {
        JwtParserBuilder jwtParserBuilder = Jwts.parser()
                .verifyWith(secretKey);
        Claims claims = jwtParserBuilder.build().parseSignedClaims(token).getPayload();

        return claims.getSubject();
    }

    // 验证JWT令牌
    public boolean validateToken(String token) {
        try {
            JwtParserBuilder jwtParserBuilder = Jwts.parser()
                    .verifyWith(secretKey);
            jwtParserBuilder.build().parseSignedClaims(token);
            return !isTokenBlacklisted(token);
        } catch (Exception e) {
            throw new RuntimeException("Expired or invalid JWT token");
        }
    }

    //把令牌加入黑名单，用于注销
    public void blacklistToken(String token) {
        tokenBlacklist.put(token, new Date(new Date().getTime() + validityInMilliseconds));
    }

    // 检查 token 是否被注销
    public boolean isTokenBlacklisted(String token) {
        Date expiryDate = tokenBlacklist.get(token);
        if (expiryDate == null) {
            return false;
        }
        return true;
    }

    public void purgeExpiredTokens() {
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue().before(new Date()));
    }
}
