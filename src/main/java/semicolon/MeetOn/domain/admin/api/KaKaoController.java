package semicolon.MeetOn.domain.admin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.admin.OAuth.OAuthLoginParams;
import semicolon.MeetOn.domain.admin.OAuth.kakao.KakaoLoginParams;
import semicolon.MeetOn.domain.admin.application.AdminService;
import semicolon.MeetOn.domain.admin.dto.AdminDto;
import semicolon.MeetOn.domain.admin.dto.AuthToken;

@RequiredArgsConstructor
@RestController
@RequestMapping("")
public class KaKaoController {

    private final AdminService adminService;

    @PostMapping("/oauth/callback/kakao")
    public ResponseEntity<AuthToken> callBack(@RequestParam String code) {
        OAuthLoginParams oAuthLoginParams = new KakaoLoginParams(code);
        return ResponseEntity.ok(adminService.login(oAuthLoginParams));
    }
}
