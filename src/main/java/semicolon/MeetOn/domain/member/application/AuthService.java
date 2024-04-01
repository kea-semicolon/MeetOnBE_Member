package semicolon.MeetOn.domain.member.application;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.domain.member.dto.JwtToken;
import semicolon.MeetOn.global.OAuth.OAuthInfoResponse;
import semicolon.MeetOn.global.OAuth.OAuthLoginParams;
import semicolon.MeetOn.global.OAuth.RequestOAuthInfoService;
import semicolon.MeetOn.global.jwt.JwtTokenGenerator;
import semicolon.MeetOn.global.util.CookieUtil;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RequestOAuthInfoService requestOAuthInfoService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final MemberRepository memberRepository;

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
