package semicolon.MeetOn.domain.member.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.util.StringUtils;
import semicolon.MeetOn.domain.member.domain.Member;

import java.util.List;

import static semicolon.MeetOn.domain.member.domain.QMember.*;

public class MemberBoardRepositoryImpl implements MemberBoardRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public MemberBoardRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super();
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Member> findAllByUsernameAndChannelId(String username, Long channelId) {
        return
                jpaQueryFactory.selectFrom(member)
                        .where(usernameEq(username), channelIdEq(channelId))
                        .fetch();
    }

    BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username) ? member.username.eq(username) : null;
    }

    BooleanExpression channelIdEq(Long channelId) {
        return channelId != null ? member.channelId.eq(channelId) : null;
    }
}
