package likelion13.youcandoittoo.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.dto.CustomOAuth2User;
import likelion13.youcandoittoo.auth.dto.LoginType;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import likelion13.youcandoittoo.user.dto.UserDTO;
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

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = token.split(" ")[1];
        jwtUtil.validateToken(accessToken);

        String username = jwtUtil.getUserName(accessToken);
        // 로그인 타입 구별을 위해 추가
        LoginType loginType = jwtUtil.getLoginType(accessToken);

        Authentication authentication = null;

        // 소셜로그인 / 일반로그인에 따른 처리
        if (LoginType.SOCIAL.equals(loginType)) {
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                    UserDTO.builder()
                            .username(username)
                            .name(username)
                            .loginType(loginType)
                            .build());

            authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        } else if (LoginType.LOCAL.equals(loginType)) {
            // 일반 로그인 Authentication 로직 추가
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}