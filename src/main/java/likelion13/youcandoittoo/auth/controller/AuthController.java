package likelion13.youcandoittoo.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "SocialLogin", description = "소셜로그인 API")
public class AuthController {

    private final AuthService authService;

    /**
     * token 재발급 API
     */
    @PostMapping("/reissue")
    @Operation(
            summary = "토큰 재발급 요청",
            description = "토큰의 유효기한이 끝나면 요청이 거부되기 때문에 토큰을 새롭게 재발급 받는 API"
    )
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        return authService.reissue(request, response);
    }
}

