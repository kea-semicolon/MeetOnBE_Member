package semicolon.MeetOn.domain.member.application;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.global.jwt.JwtTokenGenerator;
import semicolon.MeetOn.global.jwt.JwtTokenProvider;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static semicolon.MeetOn.domain.member.dto.MemberDto.*;

@Slf4j
@SpringBootTest
@Transactional
@RequiredArgsConstructor
//@Rollback(value = false)
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    /**
     * 채널을 MSA 빼면 API 통신을 통해 Channel 정보하나 받아서 sample 데이터 저장
     */
//    @Autowired
//    ChannelRepository channelRepository;

    MockHttpServletResponse response;
    MockHttpServletRequest request;
    @BeforeEach
    void 쿠키_세팅() {
        //샘플 채널
//        Channel channel = new Channel();
//        channelRepository.save(channel);
//        Channel channel = channelRepository.findById(1L).get();
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        Member member = Member.builder().username("test3").email("test3@test.com").channelId(1L).build();
        Member save = memberRepository.save(member);
        long memberId = save.getId();
        log.info("save memberId = {} ", memberId + "");
        log.info("save memberChannelId = {}", member.getChannelId());
        MockCookie mockCookie = createCookie("memberId", String.valueOf(memberId));
        MockCookie mockCookie1 = createCookie("channelId", String.valueOf(member.getChannelId()));
        response.addCookie(mockCookie);
        response.addCookie(mockCookie1);

        Cookie[] cookies = response.getCookies();
        if (cookies != null) {
            request.setCookies(cookies);
        }
    }

//    @Test
//    void 멤버_추가(){
//        Member member = Member.builder().username("test2").email("test2@naver.com").build();
//        memberRepository.save(member);
//    }

    @Test
    void 멤버_탈퇴_성공() {
        long memberId = Long.parseLong(Objects.requireNonNull(response.getCookie("memberId")).getValue());
        memberService.deactivate(request, response);
        // 결과 검증
        Member foundMember = memberRepository.findById(memberId).orElse(null);
        assertNull(foundMember, "멤버가 여전히 존재함");

        // 응답에서 쿠키 검증
        assertNotNull(response.getCookie("memberId"), "쿠키가 응답에 없음");
    }

    @Test
    void 멤버_정보가져오기_성공() {
        Member member = findMember();
        MemberInfoDto memberInfoDto = memberService.userInfo(request);
        assertThat(memberInfoDto.getUserNickname()).isEqualTo(member.getUsername());
    }

    @Test
    void 멤버_정보_수정_성공() {
        Member member = findMember();
        String beforeName = member.getUsername();
        MemberInfoDto memberInfoDto = MemberInfoDto.builder().userImage("change").userNickname("change").build();
        memberService.updateUserInfo(memberInfoDto, request);
        assertThat(member.getUsername()).isNotEqualTo(beforeName);
    }

    @Test
    void 멤버_채널_나가기_성공() {
        Member member = findMember();
        memberService.exitChannel(request);
        assertThat(member.getChannelId()).isEqualTo(1L);
    }

    @Test
    void 채널_멤버_리스트() {
        List<MemberInfoDto> memberInfoDtos = memberService.channelUserList(request);
        assertThat(memberInfoDtos.size()).isEqualTo(4);
    }

    private MockCookie createCookie(String name, String value) {
        MockCookie mockCookie = new MockCookie(name, value);
        mockCookie.setPath("/");
        mockCookie.setHttpOnly(true);
        return mockCookie;
    }

    private Member findMember() {
        long memberId = Long.parseLong(Objects.requireNonNull(response.getCookie("memberId")).getValue());
        return memberRepository.findById(memberId).get();
    }
}