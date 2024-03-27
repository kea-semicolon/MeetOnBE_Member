package semicolon.MeetOn.domain.admin.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import semicolon.MeetOn.domain.BaseTimeEntity;
import semicolon.MeetOn.domain.channel.domain.Channel;
import semicolon.MeetOn.global.OAuth.OAuthInfoResponse;

@Getter
@Entity
@NoArgsConstructor
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Builder
    public Admin(Long id, String username, String email, Authority authority, Channel channel) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.authority = authority;
        this.channel = channel;
    }

    public static Admin toAdmin(OAuthInfoResponse oAuthInfoResponse) {
        return Admin
                .builder()
                .username(oAuthInfoResponse.getNickname())
                .email(oAuthInfoResponse.getEmail())
                .authority(Authority.ROLE_CLIENT)
                .build();
    }
}
