package semicolon.MeetOn.domain.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import semicolon.MeetOn.domain.BaseTimeEntity;
import semicolon.MeetOn.domain.member.dto.MemberDto;
import semicolon.MeetOn.global.OAuth.OAuthInfoResponse;

import static semicolon.MeetOn.domain.member.dto.MemberDto.*;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    private String userImage;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private Long channelId;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "channel_id")
//    private Channel channel;

    @Builder
    public Member(Long id, String username, String email, String userImage, Authority authority, Long channelId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userImage = userImage;
        this.authority = authority;
        this.channelId = channelId;
    }

    public static Member toAdmin(OAuthInfoResponse oAuthInfoResponse) {
        return Member
                .builder()
                .username(oAuthInfoResponse.getNickname())
                .userImage(oAuthInfoResponse.getProfileImage())
                .email(oAuthInfoResponse.getEmail())
                .authority(Authority.ROLE_CLIENT)
                .channelId(1L)
                .build();
    }

    public void updateInfo(MemberInfoNoIdDto updateMemberInfo) {
        this.username = updateMemberInfo.getUserNickname();
        this.userImage = updateMemberInfo.getUserImage();
    }

    public void updateChannelCreate(MemberInfoNoIdDto updateMemberInfo, Long channelId) {
        if (updateMemberInfo.getUserNickname() != null) {
            this.username = updateMemberInfo.getUserNickname();
        }
        if(updateMemberInfo.getUserImage() != null){
            this.userImage = updateMemberInfo.getUserImage();
        }
        this.authority = updateMemberInfo.getAuthority();
        this.channelId = channelId;
    }

    public void exitChannel() {
        this.channelId = 1L;
    }
}
