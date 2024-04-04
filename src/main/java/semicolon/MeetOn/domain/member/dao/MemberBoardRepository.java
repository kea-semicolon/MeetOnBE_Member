package semicolon.MeetOn.domain.member.dao;

import semicolon.MeetOn.domain.member.domain.Member;

import java.util.List;

public interface MemberBoardRepository {

    List<Member> findAllByUsernameAndChannelId(String username, Long channelId);

}
