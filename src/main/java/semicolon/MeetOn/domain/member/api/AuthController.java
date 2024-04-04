package semicolon.MeetOn.domain.member.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import semicolon.MeetOn.domain.member.application.AuthService;
import semicolon.MeetOn.global.OAuth.kakao.KakaoLoginParams;
import semicolon.MeetOn.domain.member.dto.JwtToken;

@Slf4j
//@CrossOrigin(value = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 -> accessToken, refreshToken 생성 후 리턴
     * @param code
     * @param response
     * @return
     */
    @PostMapping("/callback/kakao")
    public Mono<ResponseEntity<JwtToken>> login(@RequestParam String code,
                                                HttpServletResponse response) {
        KakaoLoginParams kakaoLoginParams = new KakaoLoginParams(code);
        return authService.login(kakaoLoginParams, response)
                .map(token -> {
                    log.info("accessToken={}", token.getAccessToken());
                    return ResponseEntity.ok(token);
                });
    }

    /**
     * refreshToken으로 accessToken 갱신
     * 아마 블로그 특성 상 자동 갱신이 맞을듯?
     * 자동 갱신은 refresh 서비스 로직 그대로 filter로 옮기면 될듯
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtToken> refresh(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }

}
