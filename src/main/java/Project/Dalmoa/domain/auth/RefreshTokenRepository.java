package Project.Dalmoa.domain.auth;


import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findValidToken(Long memberId, LocalDateTime now);

    RefreshToken save(RefreshToken token);

    void deleteByMemberId(Long memberId);
}
