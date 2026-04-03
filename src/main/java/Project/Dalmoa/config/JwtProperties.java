package Project.Dalmoa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        String issuer,
        long accessTokenExpMinutes,
        long refreshTokenExpDays
        ) {}
