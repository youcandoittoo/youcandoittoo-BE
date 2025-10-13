package likelion13.youcandoittoo.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import likelion13.youcandoittoo.auth.filter.AuthExceptionFilter;
import likelion13.youcandoittoo.auth.filter.CustomLogoutFilter;
import likelion13.youcandoittoo.auth.filter.JwtFilter;
import likelion13.youcandoittoo.auth.oauth.CustomOauth2UserService;
import likelion13.youcandoittoo.auth.oauth.CustomSuccessHandler;
import likelion13.youcandoittoo.auth.util.CookieUtil;
import likelion13.youcandoittoo.auth.util.JwtHelper;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final ObjectMapper objectMapper;
    private final JwtHelper jwtHelper;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // cors 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 기본 보안 기능 비활성화
        http.csrf(csrf -> csrf.disable());
        http.logout(logout -> logout.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // 필터 등록 추가 예정
        http.addFilterBefore(new AuthExceptionFilter(objectMapper), LogoutFilter.class);
        http.addFilterAt(new CustomLogoutFilter(jwtHelper, cookieUtil, jwtUtil, objectMapper), LogoutFilter.class);
        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOauth2UserService))
                        .successHandler(customSuccessHandler)
                        // .failureHandler(customFailureHandler)
        );

        // 예외 처리 설정 추가 예정 (필요하다면)


        // 요청 경로별 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/reissue").permitAll()
                .anyRequest().permitAll()
        );

        // 세션 관리 정책
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
