package semicolon.MeetOn.domain.member.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import semicolon.MeetOn.domain.member.application.AuthService;
import semicolon.MeetOn.domain.member.application.MemberService;
import semicolon.MeetOn.global.OAuth.kakao.KakaoLoginParams;
import semicolon.MeetOn.domain.member.dto.JwtToken;

@Slf4j
//@CrossOrigin(value = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
@RestController
@RequestMapping
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 -> accessToken, refreshToken 생성 후 리턴
     * @param authorizationCode
     * @param response
     * @return
     */
    @PostMapping("/oauth/callback/kakao")
    public Mono<ResponseEntity<JwtToken>> login(@RequestBody KakaoLoginParams authorizationCode,
                                                HttpServletResponse response) {
        log.info("code={}", authorizationCode.getAuthorizationCode());
        return authService.login(authorizationCode, response)
                .map(token -> {
                    log.info("accessToken={}", token.getAccessToken());
                    return ResponseEntity.ok(token);
                });
    }
}
