package Project.Dalmoa.infrastructure.auth;

import Project.Dalmoa.domain.auth.RefreshToken;
import Project.Dalmoa.domain.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Optional<RefreshToken> findValidByMemberId(Long memberId) {
        return refreshTokenJpaRepository.findValidByMemberId(memberId);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return refreshTokenJpaRepository.save(token);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        refreshTokenJpaRepository.deleteByMemberId(memberId);
    }
}
