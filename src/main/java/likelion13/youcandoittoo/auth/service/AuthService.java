package likelion13.youcandoittoo.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.dto.LoginType;
import likelion13.youcandoittoo.auth.util.CookieUtil;
import likelion13.youcandoittoo.auth.util.JwtHelper;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import likelion13.youcandoittoo.global.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JwtHelper jwtHelper;
    private final long ACCESS_TOKEN_TTL;
    private final long REFRESH_TOKEN_TTL;

    public AuthService(
            JwtUtil jwtUtil,
            CookieUtil cookieUtil,
            JwtHelper jwtHelper,
            @Value("{spring.jwt.access-ttl}") long ACCESS_TOKEN_TTL,
            @Value("{spring.jwt.refresh-ttl}") long REFRESH_TOKEN_TTL
    ) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.jwtHelper = jwtHelper;
        this.ACCESS_TOKEN_TTL = ACCESS_TOKEN_TTL;
        this.REFRESH_TOKEN_TTL = REFRESH_TOKEN_TTL;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 요청에서 refresh 토큰 값을 쿠키로부터 추출
        String refreshToken = cookieUtil.getCookieValue(request, "refresh");

        // 추출한 refresh 토큰이 유효한지 검증
        jwtHelper.validateToken(refreshToken);

        // refresh 토큰에서 사용자 정보 추출
        String username = jwtUtil.getUserName(refreshToken);
        LoginType loginType = jwtUtil.getLoginType(refreshToken);

        // 새 access 토큰과 refresh 토큰 생성
        String newAccessToken = jwtUtil.createJwt("access", username, loginType, ACCESS_TOKEN_TTL);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, loginType, REFRESH_TOKEN_TTL);

        System.out.println("newAccessToken: " + newAccessToken);
        System.out.println("newRefreshToken: " + newRefreshToken);

        // 기존 refresh 토큰을 블랙리스트에 추가 후 삭제
        jwtHelper.addBlacklistToken(refreshToken);
        jwtHelper.deleteRefreshToken(refreshToken);

        // 새 refresh 토큰을 저장
        jwtHelper.saveRefreshToken(newRefreshToken);

        // 새 access 토큰을 헤더에 설정하고, 새 refresh 토큰을 쿠키에 저장
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(cookieUtil.createCookie("refresh", newRefreshToken, (int) REFRESH_TOKEN_TTL));

        return ResponseEntity.ok(SuccessResponse.ok("Access token reissued"));
    }
}
