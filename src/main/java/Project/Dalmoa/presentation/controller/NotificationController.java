package Project.Dalmoa.presentation.controller;

import Project.Dalmoa.application.NotificationService;
import Project.Dalmoa.presentation.dto.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 모든 알림 목록을 최신순으로 조회
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@AuthenticationPrincipal Long memberId) {
        List<NotificationResponse> responses = notificationService.getMemberNotifications(memberId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> readNotification(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    // 읽지 않은 알림 확인
    @GetMapping("/unread-exists")
    public ResponseEntity<Boolean> hasUnread(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(notificationService.hasUnreadNotifications(memberId));
    }
}
