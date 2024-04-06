package semicolon.MeetOn.domain.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semicolon.MeetOn.domain.member.domain.Member;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberBoardRepository {

    Optional<Member> findByEmail(String email);

    List<Member> findByChannelId(Long channelId);

    List<Member> findMembersByIdIn(List<Long> memberIds);
}
