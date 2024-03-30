package semicolon.MeetOn.domain.channel.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import semicolon.MeetOn.domain.BaseTimeEntity;
import semicolon.MeetOn.domain.member.domain.Member;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Channel extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "channel")
    private List<Member> memberList = new ArrayList<>();

    @Builder
    public Channel(Long id, List<Member> memberList) {
        this.id = id;
        this.memberList = memberList;
    }

    public static Channel defaultChannel(){
        return Channel.builder().id(-1L).build();
    }
}
