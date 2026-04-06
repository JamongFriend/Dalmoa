package Project.Dalmoa.infrastructure.subscribe;

import Project.Dalmoa.domain.subscribe.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeJpaRepository extends JpaRepository<Subscribe, Long> {
    Subscribe save(Subscribe subscribe);

    Optional<Subscribe> findById(Long subscribeId);

    List<Subscribe> findAllByMemberId(Long memberId);
}
