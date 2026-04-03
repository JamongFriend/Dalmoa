package Project.Dalmoa.presentation.dto.auth.response;

import lombok.Builder;

@Builder
public record TokenResponse(
        Long memberId,
        String accessToken,
        String refreshToken
) {
}
