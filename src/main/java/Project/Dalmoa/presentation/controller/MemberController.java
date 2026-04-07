package Project.Dalmoa.presentation.controller;

import Project.Dalmoa.application.MemberService;
import Project.Dalmoa.presentation.dto.member.request.ProfileUpdateRequest;
import Project.Dalmoa.presentation.dto.member.request.SignUpRequest;
import Project.Dalmoa.presentation.dto.member.response.MemberResponse;
import Project.Dalmoa.presentation.dto.member.response.SignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> register(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(memberService.SignUp(request));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId) {
        MemberResponse response = memberService.getMember(memberId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long memberId,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(memberService.updateMember(memberId, request));
    }
}
