package Project.Dalmoa.integration;

import Project.Dalmoa.config.JwtProperties;
import Project.Dalmoa.presentation.dto.auth.request.LoginRequest;
import Project.Dalmoa.presentation.dto.auth.request.ReissueRequest;
import Project.Dalmoa.presentation.dto.auth.response.TokenResponse;
import Project.Dalmoa.presentation.dto.member.request.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 앱이 자동로그인 상태에서 액세스 토큰이 만료됐을 때
 * (1) 401로 응답하는지, (2) reissue로 재발급받은 토큰으로 재요청하면
 * 재로그인 없이 정상적으로 정보 조회가 이어지는지를 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TokenReissueFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProperties jwtProperties;

    private static final String EMAIL = "reissue-test@dalmoa.com";
    private static final String PASSWORD = "password1234!";

    @BeforeEach
    void signUp() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                EMAIL, "재발급테스트", PASSWORD, PASSWORD, LocalDate.of(2000, 1, 1)
        );

        mockMvc.perform(post("/api/member/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void 만료된_액세스_토큰은_401이고_재발급받은_토큰으로는_자동으로_정보조회에_성공한다() throws Exception {
        // 로그인 -> access/refresh 토큰 발급
        TokenResponse tokens = login();

        // 1) 정상 access token으로는 내 정보 조회 성공
        mockMvc.perform(get("/api/member")
                        .header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));

        // 2) access token이 만료된 상태로 요청하면 403이 아니라 401이어야
        //    안드로이드 TokenAuthenticator(OkHttp Authenticator)가 재발급을 트리거할 수 있다
        String expiredAccessToken = createExpiredAccessToken(tokens.memberId(), EMAIL);

        mockMvc.perform(get("/api/member")
                        .header("Authorization", "Bearer " + expiredAccessToken))
                .andExpect(status().isUnauthorized());

        // 3) refresh token으로 재발급
        String reissueResponse = mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReissueRequest(tokens.refreshToken()))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TokenResponse reissued = objectMapper.readValue(reissueResponse, TokenResponse.class);
        assertThat(reissued.accessToken()).isNotBlank();
        assertThat(reissued.memberId()).isEqualTo(tokens.memberId());

        // 4) 재발급받은 access token으로는 재로그인 없이 정보 조회가 다시 성공한다 (= 자동로그인 유지)
        mockMvc.perform(get("/api/member")
                        .header("Authorization", "Bearer " + reissued.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    void 유효하지_않은_refresh_token으로는_재발급에_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReissueRequest("invalid.refresh.token"))))
                .andExpect(status().isBadRequest());
    }

    private TokenResponse login() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, PASSWORD, true))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, TokenResponse.class);
    }

    // JwtTokenProvider와 동일한 서명 방식으로, 이미 만료된 access token을 직접 생성한다
    private String createExpiredAccessToken(Long memberId, String email) {
        Key key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
        Date issuedAt = Date.from(OffsetDateTime.now().minusHours(1).toInstant());
        Date expiredAt = Date.from(OffsetDateTime.now().minusMinutes(1).toInstant());

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuer(jwtProperties.issuer())
                .claim("email", email)
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
