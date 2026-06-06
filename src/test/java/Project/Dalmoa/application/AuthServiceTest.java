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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties(
                "testSecretKeyMinimum32CharactersRequired1234",
                "dalmoa-backend",
                30L,
                30L
        );
        authService = new AuthService(
                memberRepository,
                refreshTokenRepository,
                passwordEncoder,
                jwtTokenProvider,
                jwtProperties
        );
    }

    @Test
    void login_성공() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password123", true);
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(any(), any())).thenReturn("accessToken");
        when(jwtTokenProvider.createRefreshToken(any(), any())).thenReturn("refreshToken");

        // when
        TokenResponse result = authService.login(request);

        // then
        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
        verify(refreshTokenRepository).deleteByMemberId(any());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void login_존재하지않는이메일_예외() {
        // given
        LoginRequest request = new LoginRequest("notexist@test.com", "password123", true);
        when(memberRepository.findByEmail("notexist@test.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    void login_비밀번호불일치_예외() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "wrongPassword", true);
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));

        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    void reissue_성공() {
        // given
        String refreshTokenStr = "validRefreshToken";
        ReissueRequest request = new ReissueRequest(refreshTokenStr);
        RefreshToken savedToken = new RefreshToken(1L, refreshTokenStr, LocalDateTime.now().plusDays(30));

        when(jwtTokenProvider.isValid(refreshTokenStr)).thenReturn(true);
        when(jwtTokenProvider.getMemberId(refreshTokenStr)).thenReturn(1L);
        when(jwtTokenProvider.getEmail(refreshTokenStr)).thenReturn("test@test.com");
        when(refreshTokenRepository.findValidToken(eq(1L), any())).thenReturn(Optional.of(savedToken));
        when(jwtTokenProvider.createAccessToken(1L, "test@test.com")).thenReturn("newAccessToken");
        when(jwtTokenProvider.createRefreshToken(1L, "test@test.com")).thenReturn("newRefreshToken");

        // when
        TokenResponse result = authService.reissue(request);

        // then
        assertThat(result.accessToken()).isEqualTo("newAccessToken");
        assertThat(result.refreshToken()).isEqualTo("newRefreshToken");
        assertThat(result.memberId()).isEqualTo(1L);
    }

    @Test
    void reissue_유효하지않은RefreshToken_예외() {
        // given
        when(jwtTokenProvider.isValid(any())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue(new ReissueRequest("invalidToken")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RefreshToken이 유효하지 않습니다.");
    }

    @Test
    void reissue_DB에저장된토큰없음_예외() {
        // given
        String refreshTokenStr = "validRefreshToken";
        ReissueRequest request = new ReissueRequest(refreshTokenStr);

        when(jwtTokenProvider.isValid(refreshTokenStr)).thenReturn(true);
        when(jwtTokenProvider.getMemberId(refreshTokenStr)).thenReturn(1L);
        when(jwtTokenProvider.getEmail(refreshTokenStr)).thenReturn("test@test.com");
        when(refreshTokenRepository.findValidToken(eq(1L), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.reissue(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("저장된 RefreshToken이 없거나 만료되었습니다.");
    }

    @Test
    void reissue_토큰불일치_예외() {
        // given
        String requestToken = "requestToken";
        ReissueRequest request = new ReissueRequest(requestToken);
        RefreshToken savedToken = new RefreshToken(1L, "differentToken", LocalDateTime.now().plusDays(30));

        when(jwtTokenProvider.isValid(requestToken)).thenReturn(true);
        when(jwtTokenProvider.getMemberId(requestToken)).thenReturn(1L);
        when(jwtTokenProvider.getEmail(requestToken)).thenReturn("test@test.com");
        when(refreshTokenRepository.findValidToken(eq(1L), any())).thenReturn(Optional.of(savedToken));

        // when & then
        assertThatThrownBy(() -> authService.reissue(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RefreshToken이 일치하지 않습니다.");
    }

    @Test
    void logout_성공() {
        // given
        Long memberId = 1L;

        // when
        authService.logout(memberId);

        // then
        verify(refreshTokenRepository).deleteByMemberId(memberId);
    }
}
