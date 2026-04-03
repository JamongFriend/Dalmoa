package Project.Dalmoa.presentation.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank String refreshToken
) {
}
