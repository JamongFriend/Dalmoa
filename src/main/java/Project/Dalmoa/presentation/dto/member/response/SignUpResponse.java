package Project.Dalmoa.presentation.dto.member.response;

import lombok.Builder;

@Builder
public record SignUpResponse(
        Long memberId,
        String email,
        String name
) {
}
