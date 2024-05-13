package semicolon.MeetOn.domain.member.application;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.domain.member.dto.JwtToken;
import semicolon.MeetOn.global.OAuth.OAuthInfoResponse;
import semicolon.MeetOn.global.OAuth.OAuthLoginParams;
import semicolon.MeetOn.global.OAuth.RequestOAuthInfoService;
import semicolon.MeetOn.global.exception.BusinessLogicException;
import semicolon.MeetOn.global.exception.code.ExceptionCode;
import semicolon.MeetOn.global.jwt.JwtTokenGenerator;
import semicolon.MeetOn.global.jwt.JwtTokenProvider;
import semicolon.MeetOn.global.util.CookieUtil;

import java.util.Optional;

import static semicolon.MeetOn.global.exception.code.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RequestOAuthInfoService requestOAuthInfoService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final CookieUtil cookieUtil;
    private static final long channelId = 1L;

    /**
     * 로그인
     * @param params
     * @return
     */
    @Transactional
    public Mono<JwtToken> login(OAuthLoginParams params, HttpServletResponse response) {
        return requestOAuthInfoService.request(params)
                .flatMap(oAuthInfoResponse -> {
                    Ids ids = findOrCreateMember(oAuthInfoResponse, response);
                    JwtToken token = jwtTokenGenerator.generate(ids.getMemberId(), ids.getChannelId());
                    String refreshToken = jwtTokenGenerator.generateRefreshToken(ids.memberId);
                    //cookieUtil.createCookie("refreshToken", refreshToken, response);
                    //cookieUtil.createCookie("memberId", String.valueOf(memberId), response);
                    return Mono.just(token);
                });
    }

    /**
     * AccessToken 재발급
     * @return
     * 여기 지금 쿠키에 access랑 refresh를 담고 있으니 프론트에서 쿠키 값 꺼내서 쓰고 access만 갱신하면 됨(refresh x)
     */
    @Transactional
    public JwtToken refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getCookieValue("refreshToken", request);
        String memberId = cookieUtil.getCookieValue("memberId", request);
        String channelId = cookieUtil.getCookieValue("channelId", request);
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new BusinessLogicException(ExceptionCode.LOGOUT_MEMBER);
        }
        JwtToken jwtToken = jwtTokenGenerator.generate(Long.valueOf(memberId), Long.valueOf(channelId));
        refreshToken = jwtTokenGenerator.generateRefreshToken(Long.valueOf(memberId));
        cookieUtil.createCookie("refreshToken", refreshToken, response);
        return jwtToken;
    }

    /**
     * 디폴트 채널을 null이 아닌 1번으로 변경 -> 채널 없는 유저를 모을 수 있는 채널을 생성(쓰레기 값?)
     * @param oAuthInfoResponse
     * @return
     */
    private Ids findOrCreateMember(OAuthInfoResponse oAuthInfoResponse, HttpServletResponse response) {
        Optional<Member> findMember = memberRepository.findByEmail(oAuthInfoResponse.getEmail());
        if(findMember.isEmpty()){
            Member member = Member.toAdmin(oAuthInfoResponse);
            memberRepository.save(member);
            //cookieUtil.createCookie("channelId", String.valueOf(1L), response);
            return new Ids(member.getId(), channelId);
        }
        //cookieUtil.createCookie("channelId", String.valueOf(findMember.get().getChannelId()), response);
        return new Ids(findMember.get().getId(), findMember.get().getChannelId());
    }

    @Getter
    @Builder
    private static class Ids {
        Long memberId;
        Long channelId;
    }
}
