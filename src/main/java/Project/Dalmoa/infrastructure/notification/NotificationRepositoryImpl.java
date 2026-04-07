package Project.Dalmoa.infrastructure.notification;

import Project.Dalmoa.domain.notification.Notification;
import Project.Dalmoa.domain.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {
    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id);
    }

    @Override
    public List<Notification> findByMemberIdOrderByIdDesc(Long memberId) {
        return notificationJpaRepository.findByMemberIdOrderByIdDesc(memberId);
    }

    @Override
    public boolean existsByMemberIdAndIsReadFalse(Long memberId) {
        return notificationJpaRepository.existsByMemberIdAndIsReadFalse(memberId);
    }
}
