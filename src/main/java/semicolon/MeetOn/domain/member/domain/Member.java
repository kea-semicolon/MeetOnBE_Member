package semicolon.MeetOn.domain.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import semicolon.MeetOn.domain.BaseTimeEntity;
import semicolon.MeetOn.domain.channel.domain.Channel;
import semicolon.MeetOn.global.OAuth.OAuthInfoResponse;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;

    private String userImage;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Builder
    public Member(Long id, String username, String email, String userImage, Authority authority, Channel channel) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userImage = userImage;
        this.authority = authority;
        this.channel = channel;
    }

    public static Member toAdmin(OAuthInfoResponse oAuthInfoResponse) {
        return Member
                .builder()
                .username(oAuthInfoResponse.getNickname())
                .email(oAuthInfoResponse.getEmail())
                .authority(Authority.ROLE_CLIENT)
                .build();
    }
}
