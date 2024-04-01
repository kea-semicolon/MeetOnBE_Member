package semicolon.MeetOn.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import semicolon.MeetOn.domain.member.domain.Authority;
import semicolon.MeetOn.domain.member.domain.Member;

public class MemberDto {

    @Getter
    @Builder
    public static class MemberInfoDto {
        private Long userId;
        private String userNickname;
        private String userImage;
        private Authority authority;

        public static MemberInfoDto toMemberInfoDto(Member member) {
            return MemberInfoDto
                    .builder()
                    .userNickname(member.getUsername())
                    .userImage(member.getUserImage())
                    .authority(member.getAuthority())
                    .build();
        }

        public static MemberInfoDto toMemberInfoIdDto(Member member) {
            return MemberInfoDto
                    .builder()
                    .userId(member.getId())
                    .userNickname(member.getUsername())
                    .userImage(member.getUserImage())
                    .authority(member.getAuthority())
                    .build();
        }
    }
}
