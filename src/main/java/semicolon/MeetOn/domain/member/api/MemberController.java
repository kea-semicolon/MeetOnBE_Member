package semicolon.MeetOn.domain.member.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.member.application.MemberService;
import semicolon.MeetOn.domain.member.dto.JwtToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

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
        return ResponseEntity.ok(memberService.refresh(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        memberService.logout(request, response);
        return ResponseEntity.ok("Ok");
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<String> deactivate(HttpServletRequest request, HttpServletResponse response) {
        memberService.deactivate(request, response);
        return ResponseEntity.ok("Ok");
    }
}
