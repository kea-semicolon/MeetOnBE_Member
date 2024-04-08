package semicolon.MeetOn.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.global.exception.BusinessLogicException;
import semicolon.MeetOn.global.exception.code.ExceptionCode;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberKafkaService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final static String MEMBER_DELETED_TOPIC = "member_deleted_topic";

    /**
     * 채널 추방 메시징
     * @param memberIdStr
     */
    @Transactional
    @KafkaListener(topics = "channel_member_kick_topic", groupId = "channel-group")
    public void kickedByChannel(String memberIdStr) {
        log.info("Channel Member 추방 memberId={}", memberIdStr);
        Long memberId = Long.valueOf(memberIdStr);
        Member member = findMember(memberId);
        member.exitChannel();
        kafkaTemplate.send(MEMBER_DELETED_TOPIC, memberIdStr);
    }

    /**
     * 채널 삭제 메시징
     * @return
     */
    @Transactional
    @KafkaListener(topics = "channel_deleted_topic", groupId = "channel_group")
    public void deletedByChannelDeleted(String channelIdStr) {
        log.info("Channel 삭제 channelId={}", channelIdStr);
        Long channelId = Long.valueOf(channelIdStr);
        List<Member> memberList = memberRepository.findByChannelId(channelId);
        for (Member member : memberList) {
            member.exitChannel();
            kafkaTemplate.send(MEMBER_DELETED_TOPIC, member.getId().toString());
        }
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
