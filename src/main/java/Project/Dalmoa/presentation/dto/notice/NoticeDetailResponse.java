package Project.Dalmoa.presentation.dto.notice;

import Project.Dalmoa.domain.notice.Notice;

import java.time.LocalDateTime;

public record NoticeDetailResponse(
        Long id,
        String version,
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static NoticeDetailResponse from(Notice notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getVersion(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt()
        );
    }
}
