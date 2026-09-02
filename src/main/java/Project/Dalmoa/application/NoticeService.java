package Project.Dalmoa.application;

import Project.Dalmoa.domain.notice.Notice;
import Project.Dalmoa.domain.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    private final NoticeRepository noticeRepository;

    // 공지사항 목록을 최신순으로 조회
    public List<Notice> getNotices() {
        return noticeRepository.findAllByOrderByIdDesc();
    }

    // 공지사항 상세 조회
    public Notice getNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다."));
    }
}
