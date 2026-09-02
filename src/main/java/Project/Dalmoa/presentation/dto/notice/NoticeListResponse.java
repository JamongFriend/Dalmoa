package Project.Dalmoa.presentation.dto.notice;

import Project.Dalmoa.domain.notice.Notice;

import java.time.LocalDateTime;

public record NoticeListResponse(
        Long id,
        String version,
        String title,
        LocalDateTime createdAt
) {
    public static NoticeListResponse from(Notice notice) {
        return new NoticeListResponse(
                notice.getId(),
                notice.getVersion(),
                notice.getTitle(),
                notice.getCreatedAt()
        );
    }
}
