package Project.Dalmoa.domain.notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    List<Notification> findByMemberIdOrderByIdDesc(Long memberId);

    boolean existsByMemberIdAndIsReadFalse(Long memberId);
}
