package semicolon.MeetOn.domain.member.api;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(description = "Member-외부 서버 Member 존재 여부 파악")
    @GetMapping("/find")
    public Boolean existMember(@RequestParam Long memberId) {
        return memberCommonService.findMember(memberId);
    }
}
