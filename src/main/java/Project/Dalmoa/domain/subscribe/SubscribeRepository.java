package Project.Dalmoa.domain.subscribe;


import org.springframework.stereotype.Repository;

@Repository
public interface SubscribeRepository {
    Subscribe save(Subscribe subscribe);

}
