package semicolon.MeetOn.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.domain.member.dto.MemberBoardDto;
import semicolon.MeetOn.global.exception.BusinessLogicException;
import semicolon.MeetOn.global.exception.code.ExceptionCode;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberBoardService {

    private final MemberRepository memberRepository;

    public List<MemberBoardDto> findMemberInfoList(String username, Long channelId) {
        username = URLDecoder.decode(username, StandardCharsets.UTF_8);
        List<Member> memberList = memberRepository.findAllByUsernameAndChannelId(username, channelId);
        log.info("memberSize={}, username={}, channelId={}", memberList.size(), username, channelId);
        return memberList.stream()
                .map(member -> MemberBoardDto.builder().id(member.getId()).username(member.getUsername()).build())
                .collect(Collectors.toList());
    }

    public MemberBoardDto findMemberInfo(Long memberId) {
         Member member = find(memberId);
         return MemberBoardDto.builder().id(member.getId()).username(member.getUsername()).build();
    }

    private Member find(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
