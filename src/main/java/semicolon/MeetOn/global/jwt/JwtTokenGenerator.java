package semicolon.MeetOn.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import semicolon.MeetOn.domain.member.domain.Authority;
import semicolon.MeetOn.domain.member.dto.JwtToken;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {

    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final JwtTokenProvider jwtTokenProvider;

    public JwtToken generate(Long memberId, Long channelId) {
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String subject = memberId.toString();
        String accessToken = jwtTokenProvider.generate(subject, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.generate(subject, refreshTokenExpiredAt);
        return JwtToken.of(BEARER_TYPE, accessToken, refreshToken,
                ACCESS_TOKEN_EXPIRE_TIME / 1000L, REFRESH_TOKEN_EXPIRE_TIME / 1000L,
                        memberId, channelId);
    }

    public String generateRefreshToken(Long memberId) {
        long now = (new Date()).getTime();
        String subject = memberId.toString();
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        return jwtTokenProvider.generate(subject, refreshTokenExpiredAt);
    }

    public Long extractMemberId(String accessToken) {
        return Long.valueOf(jwtTokenProvider.extractSubject(accessToken));
    }
}
