package semicolon.MeetOn.domain.admin.application;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semicolon.MeetOn.global.OAuth.OAuthInfoResponse;
import semicolon.MeetOn.global.OAuth.OAuthLoginParams;
import semicolon.MeetOn.global.OAuth.RequestOAuthInfoService;
import semicolon.MeetOn.domain.admin.dao.AdminRepository;
import semicolon.MeetOn.domain.admin.domain.Admin;
import semicolon.MeetOn.domain.admin.dto.AuthToken;
import semicolon.MeetOn.global.exception.BusinessLogicException;
import semicolon.MeetOn.global.exception.code.ExceptionCode;
import semicolon.MeetOn.global.jwt.AuthTokensGenerator;
import semicolon.MeetOn.global.jwt.JwtTokenProvider;
import semicolon.MeetOn.global.util.CookieUtil;

import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminRepository adminRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인
     * @param params
     * @return
     */
    @Transactional
    public AuthToken login(OAuthLoginParams params, HttpServletResponse response) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long adminId = findOrCreateMember(oAuthInfoResponse);
        AuthToken token = authTokensGenerator.generate(adminId);
        CookieUtil.createCookie("accessToken", token.getRefreshToken(), response);
        CookieUtil.createCookie("refreshToken", token.getAccessToken(), response);
        CookieUtil.createCookie("memberId", String.valueOf(adminId), response);
        return token;
    }

    /**
     * AccessToken 재발급
     * @return
     * 여기 지금 쿠키에 access랑 refresh를 담고 있으니 프론트에서 쿠키 값 꺼내서 쓰고 access만 갱신하면 됨(refresh x)
     */
    @Transactional
    public AuthToken refresh(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getCookieValue("accessToken", request);
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        if(!adminRepository.existsById(Long.valueOf(authentication.getName()))){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }

        String refreshToken = getCookieValue("refreshToken", request);
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new BusinessLogicException(ExceptionCode.LOGOUT_MEMBER);
        }
        AuthToken authToken = authTokensGenerator.generate(Long.valueOf(authentication.getName()));
        CookieUtil.createCookie("accessToken", authToken.getAccessToken(), response);
        return authToken;
    }

    /**
     * 로그아웃
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getCookieValue("accessToken", request);
        String refreshToken = getCookieValue("refreshToken", request);
        if(accessToken != null){
            CookieUtil.deleteCookie("accessToken", response);
        }
        if(refreshToken != null){
            CookieUtil.deleteCookie("refreshToken", response);
        }
        CookieUtil.deleteCookie("memberId", response);
        CookieUtil.deleteCookie("JSESSIONID", response);


//        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
//        if(!adminRepository.existsById(Long.valueOf(authentication.getName()))){
//            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
//        }
//        if (authentication.isAuthenticated()) {
//            CookieUtil.deleteCookie(accessToken, response);
//            CookieUtil.deleteCookie("memberId", response);
//            CookieUtil.deleteCookie("JSESSIONID", response);
//        }
//        String refreshToken = getCookieValue("refreshToken", request);
//        if(!jwtTokenProvider.validateToken(refreshToken)){
//            throw new BusinessLogicException(ExceptionCode.LOGOUT_MEMBER);
//        }
//        authentication = jwtTokenProvider.getAuthentication(accessToken);
//        if (authentication.isAuthenticated()) {
//            CookieUtil.deleteCookie(accessToken, response);
//        }
    }

    private String getCookieValue(String cookieName, HttpServletRequest request){
        Cookie cookie = CookieUtil.getCookie(request, cookieName);
        if(cookie == null){
            throw new BusinessLogicException(ExceptionCode.INVALID_REQUEST);
        }
        return cookie.getValue();
    }

    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        Optional<Admin> findAdmin = adminRepository.findByEmail(oAuthInfoResponse.getEmail());
        if(findAdmin.isEmpty()){
            Admin admin = Admin.toAdmin(oAuthInfoResponse);
            adminRepository.save(admin);
            return admin.getId();
        }
        return findAdmin.get().getId();
    }
}
