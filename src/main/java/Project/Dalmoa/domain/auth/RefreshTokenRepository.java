package Project.Dalmoa.domain.auth;


import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findValidByMemberId(Long memberId);

    RefreshToken save(RefreshToken token);

    void deleteByMemberId(Long memberId);
}
