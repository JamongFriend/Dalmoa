package Project.Dalmoa.infrastructure.subscribe;

import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SubscribeRepositoryImpl implements SubscribeRepository {
    private final SubscribeJpaRepository subscribeJpaRepository;
    @Override
    public Subscribe save(Subscribe subscribe) {
        return subscribeJpaRepository.save(subscribe);
    }

    @Override
    public Optional<Subscribe> findById(Long subscribeId) {
        return subscribeJpaRepository.findById(subscribeId);
    }

    @Override
    public void delete(Subscribe subscribe) {
        subscribeJpaRepository.delete(subscribe);
    }

    @Override
    public List<Subscribe> findAllByMemberId(Long memberId) {
        return subscribeJpaRepository.findAllByMemberId(memberId);
    }

    @Override
    public List<Subscribe> findByPaymentDay(int paymentDay) {
        return subscribeJpaRepository.findByPaymentDay(paymentDay);
    }
}
