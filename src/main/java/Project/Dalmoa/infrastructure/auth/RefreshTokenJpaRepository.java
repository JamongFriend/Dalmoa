package Project.Dalmoa.infrastructure.auth;

import Project.Dalmoa.domain.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    @Query("SELECT r FROM RefreshToken r WHERE r.memberId = :memberId AND r.expiresAt > :now AND r.revoked = false")
    Optional<RefreshToken> findValidToken(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    void deleteByMemberId(Long memberId);
}
