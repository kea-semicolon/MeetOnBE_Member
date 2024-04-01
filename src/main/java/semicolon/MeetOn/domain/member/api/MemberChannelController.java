package semicolon.MeetOn.domain.member.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.member.application.MemberChannelService;
import semicolon.MeetOn.domain.member.dto.MemberDto;

import static semicolon.MeetOn.domain.member.dto.MemberDto.*;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberChannelController {

    private final MemberChannelService memberChannelService;

    /**
     * 채널 API에서 멤버 정보가 들어오면 Post로 업데이트
     */
    @PatchMapping("/create/{memberId}")
    public ResponseEntity<String> memberUpdateByChannel(@RequestBody MemberInfoDto memberInfoDto,
                                                        @PathVariable Long memberId) {
        memberChannelService.updateMember(memberInfoDto, memberId);
        return ResponseEntity.ok("Ok");
    }
}
