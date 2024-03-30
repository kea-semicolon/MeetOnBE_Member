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

    public static JwtToken of(String accessToken, String refreshToken, String grantType, Long expiresIn) {
        return new JwtToken(grantType, accessToken, refreshToken, expiresIn);
    }
}
