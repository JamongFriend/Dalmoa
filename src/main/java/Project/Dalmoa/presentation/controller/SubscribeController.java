package Project.Dalmoa.presentation.controller;

import Project.Dalmoa.application.SubscribeService;
import Project.Dalmoa.presentation.dto.subscribe.request.SubscribeRequest;
import Project.Dalmoa.presentation.dto.subscribe.response.DashboardResponse;
import Project.Dalmoa.presentation.dto.subscribe.response.SubscribeDetailResponse;
import Project.Dalmoa.presentation.dto.subscribe.response.SubscribeListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscribeController {
    private final SubscribeService subscribeService;

    @PostMapping
    public ResponseEntity<SubscribeDetailResponse> createSubscribe(@Valid @RequestBody SubscribeRequest request,
                                                                  @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(SubscribeDetailResponse.from(subscribeService.createSubscribe(request, memberId)));
    }

    @PutMapping("/{subscribeId}")
    public ResponseEntity<SubscribeDetailResponse> editSubscribe(@Valid @RequestBody SubscribeRequest request,
                                                                @PathVariable Long subscribeId,
                                                                @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(SubscribeDetailResponse.from(subscribeService.editSubscribe(request, subscribeId, memberId)));
    }

    @GetMapping("/list")
    public ResponseEntity<List<SubscribeListResponse>> getList(@AuthenticationPrincipal Long memberId) {
        List<SubscribeListResponse> response = subscribeService.subscribeList(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{subscribeId}")
    public ResponseEntity<Void> deleteSubscribe(@PathVariable Long subscribeId, @AuthenticationPrincipal Long memberId) {
        subscribeService.deleteSubscribe(subscribeId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(subscribeService.getDashboard(memberId));
    }
}
