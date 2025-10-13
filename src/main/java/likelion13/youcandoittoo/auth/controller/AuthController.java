package likelion13.youcandoittoo.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13.youcandoittoo.auth.dto.local.SignUpRequest;
import likelion13.youcandoittoo.auth.service.AuthService;
import likelion13.youcandoittoo.global.response.SuccessResponse;
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
    public ResponseEntity<SuccessResponse<String>> reissue(HttpServletRequest request, HttpServletResponse response) {

        String responseMsg = authService.reissue(request, response);
        return ResponseEntity.ok(SuccessResponse.ok(responseMsg));
    }

    /**
     * 회원가입 API
     */
    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = "우리 서비스의 자체적인 계정 관리에 대한 회원가입 API"
    )
    public ResponseEntity<SuccessResponse<String>> signUp(@RequestBody SignUpRequest signUpRequest) {

        String responseMsg = authService.signUp(signUpRequest);
        return ResponseEntity.ok(SuccessResponse.ok(responseMsg));
    }
}

