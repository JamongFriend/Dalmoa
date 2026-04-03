package Project.Dalmoa.domain.member;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findByEmail(String email);

    Optional<Member> findById(Long memberId);
}
