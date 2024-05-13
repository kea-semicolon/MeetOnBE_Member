package semicolon.MeetOn.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long tokenExpireIn;
    private Long refreshTokenExpireIn;
    private Long memberId;
    private Long channelId;

    public static JwtToken of(String grantType, String accessToken, String refreshToken,
                              Long expiresIn, Long refreshTokenExpireIn, Long memberId, Long channelId) {
        return new JwtToken(grantType, accessToken, refreshToken,
                expiresIn, refreshTokenExpireIn, memberId, channelId);
    }
}
