package Project.Dalmoa.application;

import Project.Dalmoa.config.JwtProperties;
import Project.Dalmoa.domain.auth.JwtTokenProvider;
import Project.Dalmoa.domain.auth.RefreshToken;
import Project.Dalmoa.domain.auth.RefreshTokenRepository;
import Project.Dalmoa.domain.member.Member;
import Project.Dalmoa.domain.member.MemberRepository;
import Project.Dalmoa.presentation.dto.auth.request.LoginRequest;
import Project.Dalmoa.presentation.dto.auth.request.ReissueRequest;
import Project.Dalmoa.presentation.dto.auth.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if(!passwordEncoder.matches(request.password(), member.getPassword())){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String access = jwtTokenProvider.createAccessToken(member.getId(), member.getEmail());
        String refresh = jwtTokenProvider.createRefreshToken(member.getId(), member.getEmail());

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(jwtProperties.refreshTokenExpDays());

        refreshTokenRepository.deleteByMemberId(member.getId());
        refreshTokenRepository.save(new RefreshToken(member.getId(), refresh, expiresAt));

        return TokenResponse.builder()
                .memberId(member.getId())
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    public TokenResponse reissue(ReissueRequest request) {
        if (!jwtTokenProvider.isValid(request.refreshToken())) {
            throw new IllegalArgumentException("RefreshToken이 유효하지 않습니다.");
        }

        Long memberId = jwtTokenProvider.getMemberId(request.refreshToken());
        String email = jwtTokenProvider.getEmail(request.refreshToken());

        RefreshToken saved = refreshTokenRepository.findValidToken(memberId, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("저장된 RefreshToken이 없거나 만료되었습니다."));

        if (!request.refreshToken().equals(saved.getRefreshTokenHash())) {
            throw new IllegalArgumentException("RefreshToken이 일치하지 않습니다.");
        }

        String newAccess = jwtTokenProvider.createAccessToken(memberId, email);
        String newRefresh = jwtTokenProvider.createRefreshToken(memberId, email);

        LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(jwtProperties.refreshTokenExpDays());
        saved.updateToken(newRefresh, newExpiresAt);

        return TokenResponse.builder()
                .memberId(memberId)
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .build();
    }

    @Transactional
    public void logout(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }
}
