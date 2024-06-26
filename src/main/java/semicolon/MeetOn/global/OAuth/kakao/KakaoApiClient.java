package semicolon.MeetOn.global.OAuth.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import semicolon.MeetOn.global.OAuth.OAuthApiClient;
import semicolon.MeetOn.global.OAuth.OAuthInfoResponse;
import semicolon.MeetOn.global.OAuth.OAuthLoginParams;
import semicolon.MeetOn.global.OAuth.OAuthProvider;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements OAuthApiClient {

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    private static final String GRANT_TYPE = "authorization_code";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public Mono<String> requestAccessToken(OAuthLoginParams params) {
        //HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
        //HTTP Body 생성
        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
//
//        //HTTP(카카오) 요청보내기
//        HttpEntity<?> kakaoTokenRequest = new HttpEntity<>(body, headers);
//        KaKaoToken response = restTemplate.postForObject(TOKEN_URI, kakaoTokenRequest, KaKaoToken.class);
//
//        if (response == null) {
//            throw new RuntimeException("카카오 로그인에 실패했습니다.");
//        }
//        return response.getAccess_token();

        return webClient.post()
                .uri(TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(KaKaoToken.class)
                .map(KaKaoToken::getAccess_token)
                .onErrorMap(e -> new RuntimeException("카카오 로그인에 실패했습니다.", e));
    }

    @Override
    public Mono<OAuthInfoResponse> requestOauthInfo(String accessToken) {
        return webClient.post()
                .uri(USER_INFO_URI) // 혹은 .uri("/specific-path") 직접 경로 지정 가능
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("property_keys=[\"kakao_account.email\", \"kakao_account.profile\"]")
                .retrieve() // 응답 처리를 시작
                .bodyToMono(KakaoInfoResponse.class)
                .cast(OAuthInfoResponse.class)
                .onErrorMap(e -> new RuntimeException("카카오 정보 요청에 실패했습니다.", e)); // 응답 본문을 KakaoInfoResponse 클래스의 인스턴스로 변환
    }

//    @Override
//    public OAuthInfoResponse requestOauthInfo(String accessToken) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("Authorization", "Bearer " + accessToken);
//
//        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");
//
//        HttpEntity<?> request = new HttpEntity<>(body, headers);
//
//        return restTemplate.postForObject(USER_INFO_URI, request, KakaoInfoResponse.class);
//    }
}
