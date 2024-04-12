package semicolon.MeetOn.domain.member.api;

import io.swagger.v3.oas.annotations.Operation;
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
     * 로그아웃
     * @param request
     * @param response
     * @return
     */
    @Operation(description = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        memberService.logout(request, response);
        return ResponseEntity.ok("Ok");
    }

    /**
     * 회원 탈퇴
     * Spring Cloud에서 Listening을 이용해서 Member와 연관된 모든 값을 제거해야 함 -> Cascading 안됨
     * @param request
     * @param response
     * @return
     */
    @Operation(description = "탈퇴")
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
    @Operation(description = "유저 정보 가져오기")
    @GetMapping("/info")
    public ResponseEntity<MemberInfoNoIdDto> userInfo(HttpServletRequest request) {
        return ResponseEntity.ok(memberService.userInfo(request));
    }

    /**
     * 유저 정보 업데이트
     */
    @Operation(description = "유저 정보 업데이트")
    @PatchMapping("/info-change")
    public ResponseEntity<String> userInfoUpdate(@RequestBody MemberInfoNoIdDto updateMemberInfo, HttpServletRequest request) {
        memberService.updateUserInfo(updateMemberInfo, request);
        return ResponseEntity.ok("Ok");
    }

    /**
     * 채널 나가기
     */
    @Operation(description = "채널 나가기")
    @PatchMapping("/exit-channel")
    public ResponseEntity<String> userExitChannel(HttpServletRequest request, HttpServletResponse response) {
        memberService.exitChannel(request, response);
        return ResponseEntity.ok("Ok");
    }

    /**
     * 채널 유저 리스트
     */
    @Operation(description = "채널 유저 리스트")
    @GetMapping("")
    public ResponseEntity<List<MemberDto.MemberInfoIdDto>> channelUserList(HttpServletRequest request) {
        return ResponseEntity.ok(memberService.channelUserList(request));
    }
}
