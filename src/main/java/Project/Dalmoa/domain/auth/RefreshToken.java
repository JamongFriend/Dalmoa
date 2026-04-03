package Project.Dalmoa.domain.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String refreshTokenHash;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private LocalDateTime expirseAt;

    @Column(nullable = false)
    private boolean revoked;

    @Builder
    public RefreshToken(Long memberId, String refreshTokenHash, LocalDateTime expiresAt) {
        this.memberId = memberId;
        this.refreshTokenHash = refreshTokenHash;
        this.expirseAt = expiresAt;
        this.revoked = false;
    }

    public void updateToken(String newHash, LocalDateTime expiresAt) {
        this.refreshTokenHash = newHash;
        this.expirseAt = expiresAt;
        this.revoked = false;
    }

    public void revoke() {
        this.revoked = true;
    }
}
