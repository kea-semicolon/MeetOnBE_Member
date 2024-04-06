package semicolon.MeetOn.domain.member.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.MeetOn.domain.member.application.MemberReplyService;
import semicolon.MeetOn.domain.member.dto.MemberReplyDto;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberReplyController {

    private final MemberReplyService memberReplyService;

    @PostMapping("/reply/infoList")
    public ResponseEntity<List<MemberReplyDto>> getUserInfoList(@RequestBody List<Long> userIdList) {
        return ResponseEntity.ok(memberReplyService.memberInfoList(userIdList));
    }
}
