package semicolon.MeetOn.domain.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semicolon.MeetOn.domain.member.domain.Member;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
