package Project.Dalmoa.domain.auth;

import Project.Dalmoa.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessExpMillis;
    private final long refreshExpMillis;
    private final String issuer;

    public JwtTokenProvider (JwtProperties properties){
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.accessExpMillis = Duration.ofMinutes(properties.accessTokenExpMinutes()).toMillis();
        this.refreshExpMillis = Duration.ofDays(properties.refreshTokenExpDays()).toMillis();
        this.issuer = properties.issuer();
    }

    public String createAccessToken(Long memberId, String email) {
        return  createToken(memberId, email, accessExpMillis);
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
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getMemberId(String token) {
        return Long.parseLong(parseClaims(token).getBody().getSubject());
    }

    public String getEmail(String token) {
        Object v = parseClaims(token).getBody().get("email");
        return v == null ? null : v.toString();
    }
}
