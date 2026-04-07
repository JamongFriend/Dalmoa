package Project.Dalmoa.application;

import Project.Dalmoa.domain.member.Member;
import Project.Dalmoa.domain.notification.Notification;
import Project.Dalmoa.domain.notification.NotificationRepository;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final SubscribeRepository subscribeRepository;
    private final NotificationRepository notificationRepository;

    /**
     * 매일 오전 9시에 실행되어 지출일 알림을 보냅니다.
     * (7일 전 알림 및 하루 전 알림)
     */
    @Transactional
    @Scheduled(cron = "0 0 9 * * *")
    public void checkAndSendNotifications() {
        LocalDate today = LocalDate.now();

        // 7일 전 알림
        LocalDate sevenDaysLater = today.plusDays(7);
        List<Subscribe> weekBeforeList = subscribeRepository.findByPaymentDay(sevenDaysLater.getDayOfMonth());

        for (Subscribe sub : weekBeforeList) {
            sendNotification(sub, "일주일 뒤에 [" + sub.getName() + "] 결제가 예정되어 있습니다.");
        }

        // 하루 전 알림
        LocalDate tomorrow = today.plusDays(1);
        List<Subscribe> dayBeforeList = subscribeRepository.findByPaymentDay(tomorrow.getDayOfMonth());

        for (Subscribe sub : dayBeforeList) {
            sendNotification(sub, "내일 [" + sub.getName() + "] 결제 예정입니다. 잔액을 확인해주세요!");
        }
    }

    private void sendNotification(Subscribe subscribe, String message) {
        Notification notification = Notification.builder()
                .member(subscribe.getMember())
                .message(message)
                .build();

        notificationRepository.save(notification);
        log.info("알림 저장 완료: 대상={}, 내용={}", subscribe.getMember().getEmail(), message);
    }

    public List<Notification> getMemberNotifications(Long memberId) {
        return notificationRepository.findByMemberIdOrderByIdDesc(memberId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 없습니다."));
        notification.markAsRead();
    }

    public boolean hasUnreadNotifications(Long memberId) {
        return notificationRepository.existsByMemberIdAndIsReadFalse(memberId);
    }
}
