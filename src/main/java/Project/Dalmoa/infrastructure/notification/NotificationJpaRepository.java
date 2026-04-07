package Project.Dalmoa.infrastructure.notification;

import Project.Dalmoa.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdOrderByIdDesc(Long memberId);

    boolean existsByMemberIdAndIsReadFalse(Long memberId);
}
