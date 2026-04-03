package Project.Dalmoa.infrastructure.auth;

import Project.Dalmoa.domain.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findValidByMemberId(Long memberId);

    RefreshToken save(RefreshToken token);

    void deleteByMemberId(Long memberId);
}
