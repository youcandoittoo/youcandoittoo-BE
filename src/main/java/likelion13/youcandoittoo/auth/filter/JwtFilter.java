package likelion13.youcandoittoo.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
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
        String role = jwtUtil.getRole(accessToken);
        // 로그인 타입 구별을 위해 추가
        String loginType = jwtUtil.getLoginType(accessToken);

        Authentication authentication = null;

        // 소셜로그인 / 일반로그인
        if ("social".equals(loginType)) {
            // 소셜 로그인 Authentication 로직 추가

        } else if ("local".equals(loginType)) {
            // 일반 로그인 Authentication 로직 추가
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}