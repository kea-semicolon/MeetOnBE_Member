package semicolon.MeetOn.domain.member.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.member.application.MemberService;
import semicolon.MeetOn.domain.member.dto.JwtToken;
import semicolon.MeetOn.domain.member.dto.MemberDto;

import java.util.List;

import static semicolon.MeetOn.domain.member.dto.MemberDto.*;

@Slf4j
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

    /**
     * Spring Cloud에서 Listening을 이용해서 Member와 연관된 모든 값을 제거해야 함 -> Cascading 안됨
     * @param request
     * @param response
     * @return
     */
    @DeleteMapping("/deactivate")
    public ResponseEntity<String> deactivate(HttpServletRequest request, HttpServletResponse response) {
        memberService.deactivate(request, response);
        return ResponseEntity.ok("Ok");
    }

    /**
     * 유저 정보 가져오기(이름, 프로필 이미지)
     * @param request
     * @return
     */
    @GetMapping("/info")
    public ResponseEntity<MemberInfoDto> userInfo(HttpServletRequest request) {
        return ResponseEntity.ok(memberService.userInfo(request));
    }

    /**
     * 유저 정보 업데이트
     */
    @PatchMapping("/info-change")
    public ResponseEntity<String> userInfoUpdate(@RequestBody MemberInfoDto updateMemberInfo, HttpServletRequest request) {
        memberService.updateUserInfo(updateMemberInfo, request);
        return ResponseEntity.ok("Ok");
    }

    /**
     * 채널 나가기
     */
    @PatchMapping("/exit-channel")
    public ResponseEntity<String> userExitChannel(HttpServletRequest request) {
        memberService.exitChannel(request);
        return ResponseEntity.ok("Ok");
    }

    /**
     * 채널 유저 리스트
     */
    @GetMapping("")
    public ResponseEntity<List<MemberDto.MemberInfoDto>> channelUserList(HttpServletRequest request) {
        return ResponseEntity.ok(memberService.channelUserList(request));
    }
}