package semicolon.MeetOn.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberBoardDto {

    private Long id;
    private String username;

    @Builder
    public MemberBoardDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
