package likelion13.youcandoittoo.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import likelion13.youcandoittoo.global.exception.custom.AuthException;
import likelion13.youcandoittoo.user.entity.User;
import likelion13.youcandoittoo.user.userDetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("Authorization");

        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }
        //모든 토큰 검증 코드
        try {
            jwtUtil.validateToken(accessToken);
        } catch (AuthException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage()); // 필요하면 에러 메시지 출력
            return;
        }


        String username = jwtUtil.getUserName(accessToken);
        // 로그인 타입 구별을 위해 추가
        String loginType = jwtUtil.getLoginType(accessToken);

        Authentication authentication = null;

        // 소셜로그인 / 일반로그인
        if ("social".equals(loginType)) {
            // 소셜 로그인 Authentication 로직 추가

        } else if ("local".equals(loginType)) {
            User user = new User();
            user.setEmail(username);

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}