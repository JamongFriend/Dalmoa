package Project.Dalmoa.presentation.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @Email @NotBlank String email,
        @NotBlank String name,
        @NotBlank String password
) {
}
