package Project.Dalmoa.domain.auth;

import Project.Dalmoa.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessExpMillis;
    private final long refreshExpMillis;
    private final String issuer;

    public JwtTokenProvider (JwtProperties properties){
        byte[] keyBytes = properties.secret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            log.warn("JWT secret key is too short. It should be at least 256 bits (32 bytes).");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpMillis = Duration.ofMinutes(properties.accessTokenExpMinutes()).toMillis();
        this.refreshExpMillis = Duration.ofDays(properties.refreshTokenExpDays()).toMillis();
        this.issuer = properties.issuer();
    }

    public String createAccessToken(Long memberId, String email) {
        return createToken(memberId, email, accessExpMillis);
    }

    public String createRefreshToken(Long memberId, String email) {
        return createToken(memberId, email, refreshExpMillis);
    }

    private String createToken(Long memberId, String email, long expMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expMillis);
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuer(issuer)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return false;
    }

    public Long getMemberId(String token) {
        return Long.parseLong(parseClaims(token).getBody().getSubject());
    }

    public String getEmail(String token) {
        Object v = parseClaims(token).getBody().get("email");
        return v == null ? null : v.toString();
    }
}
