package semicolon.MeetOn.domain.member.application;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dao.MemberRepository;
import semicolon.MeetOn.domain.member.domain.Member;
import semicolon.MeetOn.domain.member.dto.JwtToken;
import semicolon.MeetOn.domain.member.dto.MemberDto;
import semicolon.MeetOn.global.OAuth.kakao.KakaoLoginParams;
import semicolon.MeetOn.global.exception.BusinessLogicException;
import semicolon.MeetOn.global.jwt.JwtTokenGenerator;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static semicolon.MeetOn.domain.member.dto.MemberDto.*;

@Slf4j
@SpringBootTest
@Transactional
@RequiredArgsConstructor
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    JwtTokenGenerator jwtTokenGenerator;

    MockHttpServletResponse response;
    MockHttpServletRequest request;
    @BeforeEach
    void 쿠키_세팅() {
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
    }
    /**
     * 인가 코드는 알아서 받아야함
     */
    @Test
    void 로그인_성공() {
        KakaoLoginParams params = new KakaoLoginParams("BnvI_fasKTSa-r5uZkkwEeoHq00FQ60X7BD0gykuv6gespn4t4vE4M8oiMUKPXWaAAABjo7WYzuGtS2__sNdBQ");
        JwtToken login = memberService.login(params, response);
        Long id = jwtTokenGenerator.extractMemberId(login.getAccessToken());

        assertThat(id).isNotNull();
    }

    @Test
    void 멤버_탈퇴_성공() {
        Member member = Member.builder().username("test").build();
        Member save = memberRepository.save(member);
        long memberId = save.getId();
        log.info("delete memberId = {} ", memberId + "");
        MockCookie mockCookie = createCookie("memberId", String.valueOf(memberId));
        response.addCookie(mockCookie);

        Cookie[] cookies = response.getCookies();
        if (cookies != null) {
            request.setCookies(cookies);
        }

        log.info("test={}",response.getCookie("memberId"));
        memberService.deactivate(request, response);

        // 결과 검증
        Member foundMember = memberRepository.findById(memberId).orElse(null);
        assertNull(foundMember, "멤버가 여전히 존재함");

        // 응답에서 쿠키 검증
        assertNotNull(response.getCookie("memberId"), "쿠키가 응답에 없음");
    }

    @Test
    void 멤버_정보가져오기_성공() {
        Member member = Member.builder().username("test").build();
        Member save = memberRepository.save(member);
        long memberId = save.getId();
        log.info("save memberId = {} ", memberId + "");
        MockCookie mockCookie = createCookie("memberId", String.valueOf(memberId));
        response.addCookie(mockCookie);

        Cookie[] cookies = response.getCookies();
        if (cookies != null) {
            request.setCookies(cookies);
        }

        log.info("test={}",response.getCookie("memberId"));

        MemberInfoDto memberInfoDto = memberService.userInfo(request);
        assertThat(memberInfoDto.getUserNickname()).isEqualTo(member.getUsername());
    }

    private MockCookie createCookie(String name, String value) {
        MockCookie mockCookie = new MockCookie(name, value);
        mockCookie.setPath("/");
        mockCookie.setHttpOnly(true);
        return mockCookie;
    }
}