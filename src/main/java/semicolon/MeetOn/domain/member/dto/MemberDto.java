package semicolon.MeetOn.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import semicolon.MeetOn.domain.member.domain.Authority;
import semicolon.MeetOn.domain.member.domain.Member;

import java.util.List;

public class MemberDto {

    @Getter
    @Builder
    public static class MemberInfoNoIdDto {
        private String userNickname;
        private String userImage;
        private Authority authority;

        public static MemberInfoNoIdDto memberInfoNoIdDto(Member member) {
            return MemberInfoNoIdDto
                    .builder()
                    .userNickname(member.getUsername())
                    .userImage(member.getUserImage())
                    .authority(member.getAuthority())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MemberInfoIdDtoList<T> {
        private List<MemberInfoIdDto> userList;
    }

    @Getter
    @Builder
    public static class MemberInfoIdDto {
        private Long userId;
        private String userNickname;
        private String userImage;
        private Authority authority;

        public static MemberInfoIdDto toMemberInfoIdDto(Member member) {
            return MemberInfoIdDto
                    .builder()
                    .userId(member.getId())
                    .userNickname(member.getUsername())
                    .userImage(member.getUserImage())
                    .authority(member.getAuthority())
                    .build();
        }
    }
}
