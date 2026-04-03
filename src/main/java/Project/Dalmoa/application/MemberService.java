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

    @Transactional
    public SignUpResponse SignUp(SignUpRequest request) {
        validateDuplicateEmail(request.email());

        Member member = Member.create(
                request.email(),
                request.name(),
                passwordEncoder.encode(request.password())
        );
        memberRepository.save(member);

        return SignUpResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .build();
    }

    @Transactional
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

    private void validateDuplicateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }
}
