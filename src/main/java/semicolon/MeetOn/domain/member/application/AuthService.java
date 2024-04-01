package semicolon.MeetOn.domain.member.application;

import jakarta.servlet.http.HttpServletResponse;
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
import semicolon.MeetOn.global.util.CookieUtil;

import java.util.Optional;

import static semicolon.MeetOn.global.exception.code.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RequestOAuthInfoService requestOAuthInfoService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final MemberRepository memberRepository;
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
                    Long memberId = findOrCreateMember(oAuthInfoResponse, response);
                    JwtToken token = jwtTokenGenerator.generate(memberId);
                    String refreshToken = jwtTokenGenerator.generateRefreshToken(memberId);
                    CookieUtil.createCookie("refreshToken", refreshToken, response);
                    CookieUtil.createCookie("memberId", String.valueOf(memberId), response);
                    return Mono.just(token);
                });
    }

    /**
     * 디폴트 채널을 null이 아닌 1번으로 변경 -> 채널 없는 유저를 모을 수 있는 채널을 생성(쓰레기 값?)
     * @param oAuthInfoResponse
     * @return
     */
    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse, HttpServletResponse response) {
        Optional<Member> findMember = memberRepository.findByEmail(oAuthInfoResponse.getEmail());
        if(findMember.isEmpty()){
            Member member = Member.toAdmin(oAuthInfoResponse);
            memberRepository.save(member);
            CookieUtil.createCookie("channelId", String.valueOf(1L), response);
            return member.getId();
        }
        CookieUtil.createCookie("channelId", String.valueOf(findMember.get().getChannelId()), response);
        return findMember.get().getId();
    }
}
