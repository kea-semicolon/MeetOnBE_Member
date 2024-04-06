package semicolon.MeetOn.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.domain.member.dto.MemberBoardDto;
import semicolon.MeetOn.domain.member.dto.MemberReplyDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReplyService {

    private final MemberRepository memberRepository;

    public List<MemberReplyDto> memberInfoList(List<Long> memberIds) {
        List<Member> memberList = memberRepository.findMembersByIdIn(memberIds);

        return memberList.stream()
                .map(member -> new MemberReplyDto(member.getId(), member.getUsername()))
                .toList();
    }
}
