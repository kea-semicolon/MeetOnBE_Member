package semicolon.MeetOn.domain.member.application;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.domain.member.dto.JwtToken;
import semicolon.MeetOn.global.OAuth.kakao.KakaoLoginParams;
import semicolon.MeetOn.global.jwt.JwtTokenGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    JwtTokenGenerator jwtTokenGenerator;

    MockHttpServletResponse response = new MockHttpServletResponse();

    /**
     * 인가 코드는 알아서 받아야함
     */
    @Test
    void 로그인_성공() {
        KakaoLoginParams params = new KakaoLoginParams("BnvI_fasKTSa-r5uZkkwEeoHq00FQ60X7BD0gykuv6gespn4t4vE4M8oiMUKPXWaAAABjo7WYzuGtS2__sNdBQ");
        JwtToken login = authService.login(params, response).block();
        Long id = jwtTokenGenerator.extractMemberId(login.getAccessToken());

        assertThat(id).isNotNull();
    }
}
