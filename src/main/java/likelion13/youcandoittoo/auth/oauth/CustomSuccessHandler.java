package likelion13.youcandoittoo.auth.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.dto.social.CustomOAuth2User;
import likelion13.youcandoittoo.auth.dto.LoginType;
import likelion13.youcandoittoo.auth.util.CookieUtil;
import likelion13.youcandoittoo.auth.util.JwtHelper;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JwtHelper jwtHelper;
    private final long ACCESS_TOKEN_TTL;
    private final long REFRESH_TOKEN_TTL;

    public CustomSuccessHandler(
            JwtUtil jwtUtil,
            CookieUtil cookieUtil,
            JwtHelper jwtHelper,
            @Value("${spring.jwt.access.ttl}") long ACCESS_TOKEN_TTL,
            @Value("${spring.jwt.refresh.ttl}") long REFRESH_TOKEN_TTL
    ) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.jwtHelper = jwtHelper;
        this.ACCESS_TOKEN_TTL = ACCESS_TOKEN_TTL;
        this.REFRESH_TOKEN_TTL = REFRESH_TOKEN_TTL;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, SecurityException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();
        LoginType loginType = customUserDetails.getLoginType();

        String accessToken = jwtUtil.createJwt("access", username, loginType, ACCESS_TOKEN_TTL);
        String refreshToken = jwtUtil.createJwt("refresh", username, loginType, REFRESH_TOKEN_TTL);

        System.out.println("accessToken: Bearer " + accessToken);
        System.out.println("refreshToken: " + refreshToken);

        jwtHelper.saveRefreshToken(refreshToken);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(cookieUtil.createCookie("refresh", refreshToken, (int) REFRESH_TOKEN_TTL));
        response.sendRedirect("http://localhost:3000/");
    }
}
