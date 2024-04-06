package semicolon.MeetOn.domain.member.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.member.application.MemberChannelService;

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
        log.info("userName={}", memberInfoDto.getUserNickname());
        memberChannelService.updateMember(memberInfoDto, memberId, channelId);
        return ResponseEntity.ok("Ok");
    }

    /**
     * 채널 삭제 전 채널 소속 모든 유저 default 채널로 변경
     * @param channelId
     * @return
     */
    @PatchMapping("/delete/channel")
    public ResponseEntity<String> memberChannelDeleted(@RequestParam Long channelId) {
        memberChannelService.deleteChannel(channelId);
        return ResponseEntity.ok("Ok");
    }

    /**
     * 특정 멤버 채널에서 추방 -> default 채널로 변경
     * @param memberId
     * @return
     */
    @PatchMapping("/delete/member")
    public ResponseEntity<String> memberDeletedInChannel(@RequestParam Long memberId) {
        memberChannelService.deleteMemberInChannel(memberId);
        return ResponseEntity.ok("Ok");
    }
}
