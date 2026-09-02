package Project.Dalmoa.infrastructure.notice;

import Project.Dalmoa.domain.notice.Notice;
import Project.Dalmoa.domain.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {
    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public List<Notice> findAllByOrderByIdDesc() {
        return noticeJpaRepository.findAllByOrderByIdDesc();
    }

    @Override
    public Optional<Notice> findById(Long id) {
        return noticeJpaRepository.findById(id);
    }
}
