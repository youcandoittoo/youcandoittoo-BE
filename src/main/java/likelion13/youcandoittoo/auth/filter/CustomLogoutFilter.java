package likelion13.youcandoittoo.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.util.CookieUtil;
import likelion13.youcandoittoo.auth.util.JwtHelper;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import likelion13.youcandoittoo.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtHelper jwtHelper;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (!isLogoutRequest(request)) {

            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = cookieUtil.getCookieValue(request, "refresh");

        jwtUtil.validateToken(refreshToken);
        jwtHelper.validateToken(refreshToken);

        jwtHelper.addBlacklistToken(refreshToken);
        jwtHelper.deleteRefreshToken(refreshToken);

        Cookie cookie = cookieUtil.createCookie("refresh", null, 0);
        response.addCookie(cookie);

        String responseBody = objectMapper.writeValueAsString(SuccessResponse.ok("logout success"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isLogoutRequest(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        return requestURI.matches("^/logout$") && requestMethod.equals("GET");
    }
}
