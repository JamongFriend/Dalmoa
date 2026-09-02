package Project.Dalmoa.presentation.controller;

import Project.Dalmoa.application.NoticeService;
import Project.Dalmoa.presentation.dto.notice.NoticeDetailResponse;
import Project.Dalmoa.presentation.dto.notice.NoticeListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 목록을 최신순으로 조회
    @GetMapping
    public ResponseEntity<List<NoticeListResponse>> getNotices() {
        List<NoticeListResponse> responses = noticeService.getNotices()
                .stream()
                .map(NoticeListResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 공지사항 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<NoticeDetailResponse> getNotice(@PathVariable Long id) {
        return ResponseEntity.ok(NoticeDetailResponse.from(noticeService.getNotice(id)));
    }
}
