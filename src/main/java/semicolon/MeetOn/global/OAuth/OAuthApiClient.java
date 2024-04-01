package semicolon.MeetOn.global.OAuth;

import reactor.core.publisher.Mono;
import semicolon.MeetOn.global.OAuth.kakao.KakaoInfoResponse;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    Mono<String> requestAccessToken(OAuthLoginParams params);
    Mono<OAuthInfoResponse> requestOauthInfo(String accessToken);
}
