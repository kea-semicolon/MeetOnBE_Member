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

import java.util.List;

import static semicolon.MeetOn.domain.member.dto.MemberDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberChannelService {

    private final MemberRepository memberRepository;

    /**
     * Channel 서버에서 유저 정보 받아서 업데이트
     * @param memberInfoDto
     * @param memberId
     */
    @Transactional
    public void updateMember(MemberInfoNoIdDto memberInfoDto, Long memberId, Long channelId) {
//        long memberId = Long.parseLong(CookieUtil.getCookieValue("memberId", request));
        Member member = findMember(memberId);
        member.updateChannelCreate(memberInfoDto, channelId);
    }

//    /**
//     * 유저 채널 default 채널로 변경
//     * @param channelId
//     */
//    @Transactional
//    public void deleteChannel(Long channelId) {
//        memberRepository.findByChannelId(channelId).forEach(Member::exitChannel);
//    }

//    /**
//     * 유저 채널 default 채널로 변경
//     * @param memberId
//     */
//    @Transactional
//    public void deleteMemberInChannel(Long memberId) {
//        Member member = findMember(memberId);
//        member.exitChannel();
//    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
