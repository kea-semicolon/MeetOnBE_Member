package semicolon.MeetOn.domain.member.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import semicolon.MeetOn.domain.member.application.MemberCommonService;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberCommonController {

    private final MemberCommonService memberCommonService;

    @GetMapping("/find")
    public Boolean existMember(@RequestParam Long memberId) {
        return memberCommonService.findMember(memberId);
    }
}
