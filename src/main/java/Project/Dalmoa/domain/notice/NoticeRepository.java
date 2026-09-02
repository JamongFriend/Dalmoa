package Project.Dalmoa.domain.notice;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository {
    List<Notice> findAllByOrderByIdDesc();

    Optional<Notice> findById(Long id);
}
