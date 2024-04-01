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

    /**
     * AccessToken 재발급
     * @return
     * 여기 지금 쿠키에 access랑 refresh를 담고 있으니 프론트에서 쿠키 값 꺼내서 쓰고 access만 갱신하면 됨(refresh x)
     */
    @Transactional
    public JwtToken refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue("refreshToken", request);
        String memberId = CookieUtil.getCookieValue("memberId", request);
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new BusinessLogicException(ExceptionCode.LOGOUT_MEMBER);
        }
        JwtToken jwtToken = jwtTokenGenerator.generate(Long.valueOf(memberId));
        refreshToken = jwtTokenGenerator.generateRefreshToken(Long.valueOf(memberId));
        CookieUtil.createCookie("refreshToken", refreshToken, response);
        return jwtToken;
    }

    /**
     * 로그아웃
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue("refreshToken", request);
        if(refreshToken != null){
            CookieUtil.deleteCookie("refreshToken", response);
        }
        CookieUtil.deleteCookie("memberId", response);
        CookieUtil.deleteCookie("JSESSIONID", response);
    }

    /**
     * 탈퇴 -> MSA이기 때문에 각 서버에 삭제 API를 끌어와서 해당 유저와 연관된 1:N 데이터 모두 삭제
     * @param request
     * @param response
     */
    @Transactional
    public void deactivate(HttpServletRequest request, HttpServletResponse response) {
        memberRepository.delete(findMember(request));
        CookieUtil.deleteCookie("JSESSIONID", response);
        CookieUtil.deleteCookie("refreshToken", response);
        CookieUtil.deleteCookie("memberId", response);
    }

    /**
     * 유저 정보
     * @param request
     * @return
     */
    public MemberInfoDto userInfo(HttpServletRequest request) {
        return MemberInfoDto.toMemberInfoDto(findMember(request));
    }

    /**
     * 유저 정보 수정
     * @param updateMemberInfo
     * @param request
     */
    @Transactional
    public void updateUserInfo(MemberInfoDto updateMemberInfo, HttpServletRequest request) {
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
    public List<MemberDto.MemberInfoDto> channelUserList(HttpServletRequest request) {
        Long channelId = Long.valueOf(CookieUtil.getCookieValue("channelId", request));
        return memberRepository.findByChannelId(channelId).stream()
                .map(MemberInfoDto::toMemberInfoIdDto)
                .collect(Collectors.toList());
    }

    /**
     * MemberId로 Member 찾기 -> 없으면 MEMBER_NOT_FOUND 예외 처리
     * @param request
     * @return
     */
    private Member findMember(HttpServletRequest request) {
        long memberId = Long.parseLong(CookieUtil.getCookieValue("memberId", request));
        return memberRepository.findById(memberId).orElseThrow(() -> new BusinessLogicException(MEMBER_NOT_FOUND));
    }
}
