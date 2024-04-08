package semicolon.MeetOn.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommonService {

    private final MemberRepository memberRepository;

    public Boolean findMember(Long memberId) {
        Optional<Member> find = memberRepository.findById(memberId);
        return find.isPresent();
    }
}
