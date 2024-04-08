package semicolon.MeetOn.domain.member.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.member.application.MemberBoardService;
import semicolon.MeetOn.domain.member.dto.MemberBoardDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberBoardController {

    private final MemberBoardService memberBoardService;

    @GetMapping("/board/infoList")
    public List<MemberBoardDto> getMemberInfoForBoardList(@RequestParam String username, @RequestParam Long channelId) {
        return memberBoardService.findMemberInfoList(username, channelId);
    }

    @GetMapping("/board/info")
    public MemberBoardDto getMemberInfoForBoard(@RequestParam Long memberId) {
        return memberBoardService.findMemberInfo(memberId);
    }
}
