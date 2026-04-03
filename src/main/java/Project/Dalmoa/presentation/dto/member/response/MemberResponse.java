package Project.Dalmoa.presentation.dto.member.response;

import Project.Dalmoa.domain.member.Member;

public record MemberResponse(
        Long id,
        String email,
        String name
) {
    public static MemberResponse fromEntity(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
}
