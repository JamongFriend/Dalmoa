package Project.Dalmoa.presentation.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SignUpRequest(
        @Email @NotBlank String email,
        @NotBlank String name,
        @NotBlank String password,
        @NotBlank String confirmPassword,
        @NotNull LocalDate birthDate
) {
}
