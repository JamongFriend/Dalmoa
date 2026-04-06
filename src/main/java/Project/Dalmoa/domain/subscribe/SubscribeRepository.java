package Project.Dalmoa.domain.subscribe;


import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscribeRepository {
    Subscribe save(Subscribe subscribe);

    Optional<Subscribe> findById(Long subscribeId);

    void delete(Subscribe subscribe);

    List<Subscribe> findAllByMemberId(Long memberId);
}
