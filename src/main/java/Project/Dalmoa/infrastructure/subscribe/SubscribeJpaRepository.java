package Project.Dalmoa.infrastructure.subscribe;

import Project.Dalmoa.domain.subscribe.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeJpaRepository extends JpaRepository<Subscribe, Long> {
}
