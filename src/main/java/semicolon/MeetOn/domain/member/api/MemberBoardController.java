package semicolon.MeetOn.domain.member.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.member.application.MemberBoardService;
import semicolon.MeetOn.domain.member.application.MemberService;
import semicolon.MeetOn.domain.member.dto.MemberBoardDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberBoardController {

    private final MemberBoardService memberBoardService;

    @GetMapping("/find")
    public Boolean existMember(@RequestParam Long memberId) {
        return memberBoardService.findMember(memberId);
    }

    @GetMapping("/board/info")
    public List<MemberBoardDto> getMemberInfoForBoardTest(@RequestParam String username, @RequestParam Long channelId) {
        return memberBoardService.findMemberInfo(username, channelId);
    }
}
