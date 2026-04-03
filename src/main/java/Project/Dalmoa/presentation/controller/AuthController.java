package Project.Dalmoa.presentation.controller;

import Project.Dalmoa.application.AuthService;
import Project.Dalmoa.presentation.dto.auth.request.LoginRequest;
import Project.Dalmoa.presentation.dto.auth.request.ReissueRequest;
import Project.Dalmoa.presentation.dto.auth.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        return ResponseEntity.ok(authService.reissue(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Long memberId) {
        authService.logout(memberId);
        return ResponseEntity.noContent().build();
    }
}
