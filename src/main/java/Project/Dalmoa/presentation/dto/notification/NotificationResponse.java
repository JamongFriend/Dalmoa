package Project.Dalmoa.presentation.dto.notification;

import Project.Dalmoa.domain.notification.Notification;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
