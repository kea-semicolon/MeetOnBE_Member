package semicolon.MeetOn.domain.member.application;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.domain.member.dto.MemberDto;
import semicolon.MeetOn.global.exception.BusinessLogicException;
import semicolon.MeetOn.global.exception.code.ExceptionCode;
import semicolon.MeetOn.global.util.CookieUtil;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberChannelService {

    private final MemberRepository memberRepository;

    /**
     * Channel 서버에서 유저 정보 받아서 업데이트
     * @param memberInfoDto
     * @param request
     */
    @Transactional
    public void updateMember(MemberDto.MemberInfoDto memberInfoDto, Long memberId) {
//        long memberId = Long.parseLong(CookieUtil.getCookieValue("memberId", request));
        log.info("memberId={}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        member.updateChannelCreate(memberInfoDto);
    }
}
