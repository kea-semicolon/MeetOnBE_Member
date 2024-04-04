package semicolon.MeetOn.domain.member.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import semicolon.MeetOn.domain.member.application.MemberBoardService;
import semicolon.MeetOn.domain.member.application.MemberService;

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
}
