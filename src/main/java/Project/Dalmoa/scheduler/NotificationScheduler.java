package Project.Dalmoa.scheduler;

import Project.Dalmoa.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;

    // 매일 오전 9시에 알림 체크 실행
    @Scheduled(cron = "0 0 9 * * *")
    public void runNotificationTask() {
        notificationService.checkAndSendNotifications();
    }
}
