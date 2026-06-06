package Project.Dalmoa.application;

import Project.Dalmoa.domain.member.Member;
import Project.Dalmoa.domain.member.MemberRepository;
import Project.Dalmoa.presentation.dto.member.request.ProfileUpdateRequest;
import Project.Dalmoa.presentation.dto.member.request.SignUpRequest;
import Project.Dalmoa.presentation.dto.member.response.MemberResponse;
import Project.Dalmoa.presentation.dto.member.response.SignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional // 회원가입
    public SignUpResponse SignUp(SignUpRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        validateDuplicateEmail(request.email());

        Member member = Member.create(
                request.email(),
                request.name(),
                passwordEncoder.encode(request.password()),
                request.birthDate()
        );
        memberRepository.save(member);

        return SignUpResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .build();
    }

    @Transactional // 회원 정보 수정
    public MemberResponse updateMember(Long memberId, @Valid ProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member.update(request.name());
        return MemberResponse.fromEntity(member);
    }

    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return MemberResponse.fromEntity(member);
    }

    // 이메일 검증
    private void validateDuplicateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }
}
