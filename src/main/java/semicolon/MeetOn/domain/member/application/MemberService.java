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

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static semicolon.MeetOn.domain.member.dto.MemberDto.*;
import static semicolon.MeetOn.global.exception.code.ExceptionCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;

    /**
     * 로그인
     * @param params
     * @return
     */
    @Transactional
    public JwtToken login(OAuthLoginParams params, HttpServletResponse response) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long memberId = findOrCreateMember(oAuthInfoResponse);
        JwtToken token = jwtTokenGenerator.generate(memberId);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(memberId);
//        CookieUtil.createCookie("accessToken", token.getRefreshToken(), response);
        CookieUtil.createCookie("refreshToken", refreshToken, response);
        CookieUtil.createCookie("memberId", String.valueOf(memberId), response);
        return token;
    }

    /**
     * AccessToken 재발급
     * @return
     * 여기 지금 쿠키에 access랑 refresh를 담고 있으니 프론트에서 쿠키 값 꺼내서 쓰고 access만 갱신하면 됨(refresh x)
     */
    @Transactional
    public JwtToken refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue("refreshToken", request);
        String memberId = getCookieValue("memberId", request);
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
        String refreshToken = getCookieValue("refreshToken", request);
        if(refreshToken != null){
            CookieUtil.deleteCookie("refreshToken", response);
        }
        CookieUtil.deleteCookie("memberId", response);
        CookieUtil.deleteCookie("JSESSIONID", response);


//        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
//        if(!adminRepository.existsById(Long.valueOf(authentication.getName()))){
//            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
//        }
//        if (authentication.isAuthenticated()) {
//            CookieUtil.deleteCookie(accessToken, response);
//            CookieUtil.deleteCookie("memberId", response);
//            CookieUtil.deleteCookie("JSESSIONID", response);
//        }
//        String refreshToken = getCookieValue("refreshToken", request);
//        if(!jwtTokenProvider.validateToken(refreshToken)){
//            throw new BusinessLogicException(ExceptionCode.LOGOUT_MEMBER);
//        }
//        authentication = jwtTokenProvider.getAuthentication(accessToken);
//        if (authentication.isAuthenticated()) {
//            CookieUtil.deleteCookie(accessToken, response);
//        }
    }

    @Transactional
    public void deactivate(HttpServletRequest request, HttpServletResponse response) {
        long memberId = Long.parseLong(getCookieValue("memberId", request));
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new BusinessLogicException(MEMBER_NOT_FOUND));
        memberRepository.delete(member);
        CookieUtil.deleteCookie("JSESSIONID", response);
        CookieUtil.deleteCookie("refreshToken", response);
        CookieUtil.deleteCookie("memberId", response);
    }


    public MemberInfoDto userInfo(HttpServletRequest request) {
        long memberId = Long.parseLong(getCookieValue("memberId", request));
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new BusinessLogicException(MEMBER_NOT_FOUND));
        return MemberInfoDto.toMemberInfoDto(member);
    }

    private String getCookieValue(String cookieName, HttpServletRequest request){
        Cookie cookie = CookieUtil.getCookie(request, cookieName);
        if(cookie == null){
            throw new BusinessLogicException(ExceptionCode.INVALID_REQUEST);
        }
        return cookie.getValue();
    }

    /**
     * 디폴트 채널을 null이 아닌 1번으로 변경 -> 채널 없는 유저를 모을 수 있는 채널을 생성(쓰레기 값?)
     * @param oAuthInfoResponse
     * @return
     */
    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        Optional<Member> findAdmin = memberRepository.findByEmail(oAuthInfoResponse.getEmail());
        if(findAdmin.isEmpty()){
            Member member = Member.toAdmin(oAuthInfoResponse);
            memberRepository.save(member);
            return member.getId();
        }
        return findAdmin.get().getId();
    }
}
