package semicolon.MeetOn.domain.member.application;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dto.MemberDto;
import semicolon.MeetOn.global.OAuth.OAuthInfoResponse;
import semicolon.MeetOn.global.OAuth.OAuthLoginParams;
import semicolon.MeetOn.global.OAuth.RequestOAuthInfoService;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.domain.member.dto.JwtToken;
import semicolon.MeetOn.global.exception.BusinessLogicException;
import semicolon.MeetOn.global.exception.code.ExceptionCode;
import semicolon.MeetOn.global.jwt.JwtTokenGenerator;
import semicolon.MeetOn.global.jwt.JwtTokenProvider;
import semicolon.MeetOn.global.util.CookieUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static semicolon.MeetOn.domain.member.dto.MemberDto.*;
import static semicolon.MeetOn.global.exception.code.ExceptionCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;


    /**
     * 로그아웃
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getCookieValue("refreshToken", request);
        if(refreshToken != null){
            cookieUtil.deleteCookie("refreshToken", response);
        }
        cookieUtil.deleteCookie("memberId", response);
        cookieUtil.deleteCookie("JSESSIONID", response);
    }

    /**
     * 탈퇴 -> MSA이기 때문에 각 서버에 삭제 API를 끌어와서 해당 유저와 연관된 1:N 데이터 모두 삭제
     * @param request
     * @param response
     */
    @Transactional
    public void deactivate(HttpServletRequest request, HttpServletResponse response) {
        memberRepository.delete(findMember(request));
        cookieUtil.deleteCookie("JSESSIONID", response);
        cookieUtil.deleteCookie("refreshToken", response);
        cookieUtil.deleteCookie("memberId", response);
    }

    /**
     * 유저 정보 id 제외
     * @param request
     * @return
     */
    public MemberInfoNoIdDto userInfo(HttpServletRequest request) {
        return MemberInfoNoIdDto.memberInfoNoIdDto(findMember(request));
    }

    /**
     * 유저 정보 수정
     * @param updateMemberInfo
     * @param request
     */
    @Transactional
    public void updateUserInfo(MemberInfoNoIdDto updateMemberInfo, HttpServletRequest request) {
        findMember(request).updateInfo(updateMemberInfo);
    }

    /**
     * 채널 나가기 -> 연관 채널 null로 변경 or default 채널로 변경(아마 후자?)
     * MSA이기 때문에 각 서버에 삭제 API를 끌어와서 해당 유저와 연관된 1:N 데이터 모두 삭제(메모 제외)
     * @param request
     */
    @Transactional
    public void exitChannel(HttpServletRequest request) {
        findMember(request).exitChannel();
    }

    /**
     * 채널에 속해있는 유저 정보 리스트
     * @param request
     * @return
     */
    public List<MemberInfoIdDto> channelUserList(HttpServletRequest request) {
        Long channelId = Long.valueOf(cookieUtil.getCookieValue("channelId", request));
        log.info("channelId={}", channelId);
        return memberRepository.findByChannelId(channelId).stream()
                .map(MemberInfoIdDto::toMemberInfoIdDto)
                .collect(Collectors.toList());
    }

    /**
     * MemberId로 Member 찾기 -> 없으면 MEMBER_NOT_FOUND 예외 처리
     * @param request
     * @return
     */
    private Member findMember(HttpServletRequest request) {
        long memberId = Long.parseLong(cookieUtil.getCookieValue("memberId", request));
        return memberRepository.findById(memberId).orElseThrow(() -> new BusinessLogicException(MEMBER_NOT_FOUND));
    }
}
