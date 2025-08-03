package likelion13.youcandoittoo.user.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.dto.LoginType;
import likelion13.youcandoittoo.auth.util.CookieUtil;
import likelion13.youcandoittoo.auth.util.JwtHelper;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import likelion13.youcandoittoo.user.userDetail.CustomUserDetails;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final CookieUtil cookieUtil;

    private final JwtHelper jwtHelper;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CookieUtil cookieUtil, JwtHelper jwtHelper) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {

        return request.getParameter("email");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        System.out.println("✅ successfulAuthentication 실행됨");

        String username = authentication.getName();
        LoginType loginType = LoginType.LOCAL;

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, loginType.name(), 600L);
        String refresh = jwtUtil.createJwt("refresh", username, loginType.name(), 604800L);

        Cookie cookie = cookieUtil.createCookie("refresh", refresh, 60800);

        jwtHelper.saveRefreshToken(refresh);
        //응답 설정
        response.setHeader("access", access);
        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }
}
