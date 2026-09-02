package Project.Dalmoa.application;

import Project.Dalmoa.domain.member.Member;
import Project.Dalmoa.domain.member.MemberRepository;
import Project.Dalmoa.presentation.dto.member.request.ProfileUpdateRequest;
import Project.Dalmoa.presentation.dto.member.request.SignUpRequest;
import Project.Dalmoa.presentation.dto.member.response.MemberResponse;
import Project.Dalmoa.presentation.dto.member.response.SignUpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository, passwordEncoder);
    }

    @Test
    void SignUp_성공() {
        // given
        SignUpRequest request = new SignUpRequest(
                "test@test.com", "테스터", "password123", "password123", LocalDate.of(1990, 1, 1)
        );
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // when
        SignUpResponse result = memberService.SignUp(request);

        // then
        assertThat(result.email()).isEqualTo("test@test.com");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void SignUp_비밀번호불일치_예외() {
        // given
        SignUpRequest request = new SignUpRequest(
                "test@test.com", "테스터", "password123", "differentPassword", LocalDate.of(1990, 1, 1)
        );

        // when & then
        assertThatThrownBy(() -> memberService.SignUp(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    void SignUp_이메일중복_예외() {
        // given
        SignUpRequest request = new SignUpRequest(
                "test@test.com", "테스터", "password123", "password123", LocalDate.of(1990, 1, 1)
        );
        Member existingMember = Member.create("test@test.com", "기존회원", "encodedPassword", LocalDate.of(1990, 1, 1));
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(existingMember));

        // when & then
        assertThatThrownBy(() -> memberService.SignUp(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    void updateMember_성공() {
        // given
        Long memberId = 1L;
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));
        ProfileUpdateRequest request = new ProfileUpdateRequest("새이름");
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        MemberResponse result = memberService.updateMember(memberId, request);

        // then
        assertThat(result.name()).isEqualTo("새이름");
    }

    @Test
    void updateMember_존재하지않는회원_예외() {
        // given
        Long memberId = 999L;
        ProfileUpdateRequest request = new ProfileUpdateRequest("새이름");
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.updateMember(memberId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원이 존재하지 않습니다.");
    }

    @Test
    void getMember_성공() {
        // given
        Long memberId = 1L;
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        MemberResponse result = memberService.getMember(memberId);

        // then
        assertThat(result.email()).isEqualTo("test@test.com");
        assertThat(result.name()).isEqualTo("테스터");
    }

    @Test
    void getMember_존재하지않는회원_예외() {
        // given
        Long memberId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMember(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원이 존재하지 않습니다.");
    }
}
