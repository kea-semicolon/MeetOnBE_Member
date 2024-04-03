package semicolon.MeetOn.domain.member.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.member.application.MemberChannelService;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.domain.member.dto.MemberDto;
import semicolon.MeetOn.global.exception.BusinessLogicException;
import semicolon.MeetOn.global.exception.code.ExceptionCode;

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
    @PatchMapping("/update")
    public ResponseEntity<String> memberUpdateByChannel(@RequestBody MemberInfoNoIdDto memberInfoDto,
                                                        @RequestParam Long memberId,
                                                        @RequestParam Long channelId) {
        memberChannelService.updateMember(memberInfoDto, memberId, channelId);
        return ResponseEntity.ok("Ok");
    }

    @PatchMapping("/delete/channel/{channelId}")
    public ResponseEntity<String> memberChannelDeleted(@PathVariable Long channelId) {
        memberChannelService.deleteChannel(channelId);
        return ResponseEntity.ok("Ok");
    }

    @PatchMapping("/delete/member/{memberId}")
    public ResponseEntity<String> memberDeletedInChannel(@PathVariable Long memberId) {
        memberChannelService.deleteMemberInChannel(memberId);
        return ResponseEntity.ok("Ok");
    }
}
