package Project.Dalmoa.presentation.controller;

import Project.Dalmoa.application.SubscribeService;
import Project.Dalmoa.presentation.dto.subscribe.request.SubscribeRequest;
import Project.Dalmoa.presentation.dto.subscribe.response.DashboardResponse;
import Project.Dalmoa.presentation.dto.subscribe.response.SubscribeDetailResponse;
import Project.Dalmoa.presentation.dto.subscribe.response.SubscribeListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscribeController {
    private final SubscribeService subscribeService;

    @PostMapping("/{memberId}")
    public ResponseEntity<SubscribeDetailResponse> createSubscribe(@Valid @RequestBody SubscribeRequest request,
                                                                  @PathVariable Long memberId) {
        return ResponseEntity.ok(SubscribeDetailResponse.from(subscribeService.createSubscribe(request, memberId)));
    }

    @PutMapping("/{subscribeId}")
    public ResponseEntity<SubscribeDetailResponse> editSubscribe(@Valid @RequestBody SubscribeRequest request,
                                                                @PathVariable Long subscribeId) {
        return ResponseEntity.ok(SubscribeDetailResponse.from(subscribeService.editSubscribe(request, subscribeId)));
    }

    @GetMapping("/list/{memberId}")
    public ResponseEntity<List<SubscribeListResponse>> getList(@PathVariable Long memberId) {
        List<SubscribeListResponse> response = subscribeService.subscribeList(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{subscribeId}/{memberId}")
    public ResponseEntity<Void> deleteSubscribe(@PathVariable Long subscribeId, @PathVariable Long memberId) {
        subscribeService.deleteSubscribe(subscribeId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard/{memberId}")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable Long memberId) {
        return ResponseEntity.ok(subscribeService.getDashboard(memberId));
    }
}
