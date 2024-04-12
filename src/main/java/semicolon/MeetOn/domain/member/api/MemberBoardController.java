package semicolon.MeetOn.domain.member.api;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(description = "Member-Board 내부 API, 유저이름, 채널Id로 유저 정보 가져오기")
    @GetMapping("/board/infoList")
    public List<MemberBoardDto> getMemberInfoForBoardList(@RequestParam String username, @RequestParam Long channelId) {
        return memberBoardService.findMemberInfoList(username, channelId);
    }

    @Operation(description = "Member-Board 내부 API, memberId로 유저 정보 가져오기")
    @GetMapping("/board/info")
    public MemberBoardDto getMemberInfoForBoard(@RequestParam Long memberId) {
        return memberBoardService.findMemberInfo(memberId);
    }
}
