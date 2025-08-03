package likelion13.youcandoittoo.global.config;

import likelion13.youcandoittoo.auth.RefreshTokenRedisRepository;
import likelion13.youcandoittoo.auth.RefreshTokenRedisRepositoryImpl;
import likelion13.youcandoittoo.auth.filter.CustomLogoutFilter;
import likelion13.youcandoittoo.auth.util.CookieUtil;
import likelion13.youcandoittoo.auth.util.JwtHelper;
import likelion13.youcandoittoo.auth.util.JwtUtil;
import likelion13.youcandoittoo.user.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtUtil jwtUtil;

    private final JwtHelper jwtHelper;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil, JwtHelper jwtHelper, RefreshTokenRedisRepository refreshTokenRedisRepository) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.jwtHelper = jwtHelper;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CookieUtil cookieUtil, RefreshTokenRedisRepositoryImpl refreshTokenRedisRepositoryImpl) throws Exception {

        // cors 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 기본 보안 기능 비활성화
        http.csrf(csrf -> csrf.disable());
        http.logout(logout -> logout.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // 필터 등록 추가 예정
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, cookieUtil, jwtHelper), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRedisRepository), LogoutFilter.class);
        // 예외 처리 설정 추가 예정 (필요하다면)


        // 요청 경로별 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/join","/login").permitAll()
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
