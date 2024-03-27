package semicolon.MeetOn.domain.admin.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.admin.application.AdminService;
import semicolon.MeetOn.global.OAuth.kakao.KakaoLoginParams;
import semicolon.MeetOn.domain.admin.dto.AuthToken;
import semicolon.MeetOn.global.util.CookieUtil;

@Slf4j
@CrossOrigin(value = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
@RestController
@RequestMapping("")
public class AdminController {

    private final AdminService adminService;

    /**
     * 로그인 -> accessToken, refreshToken 생성 후 리턴
     * @param authorizationCode
     * @param response
     * @return
     */
    @PostMapping("/oauth/callback/kakao")
    public ResponseEntity<AuthToken> callBack(@RequestBody KakaoLoginParams authorizationCode,
                                              HttpServletResponse response) {
        log.info("code={}", authorizationCode.getAuthorizationCode());
        AuthToken token = adminService.login(authorizationCode, response);
        log.info("accessToken={}", token.getAccessToken());
        log.info("refreshToken={}", token.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    /**
     * refreshToken으로 accessToken 갱신
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/oauth/refresh")
    public ResponseEntity<AuthToken> refresh(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(adminService.refresh(request, response));
    }
}
