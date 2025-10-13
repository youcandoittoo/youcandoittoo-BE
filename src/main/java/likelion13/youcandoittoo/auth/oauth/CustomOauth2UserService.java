package likelion13.youcandoittoo.auth.oauth;

import likelion13.youcandoittoo.auth.dto.social.*;
import likelion13.youcandoittoo.auth.dto.social.provider.GoogleResponse;
import likelion13.youcandoittoo.auth.dto.social.provider.KakaoResponse;
import likelion13.youcandoittoo.auth.dto.social.provider.NaverResponse;
import likelion13.youcandoittoo.auth.dto.social.provider.OAuth2Response;
import likelion13.youcandoittoo.global.exception.custom.AuthException;
import likelion13.youcandoittoo.global.exception.error.ErrorCode;
import likelion13.youcandoittoo.user.UserRole;
import likelion13.youcandoittoo.user.dto.UserDTO;
import likelion13.youcandoittoo.user.entity.AuthProvider;
import likelion13.youcandoittoo.user.entity.User;
import likelion13.youcandoittoo.user.enums.OauthProvider;
import likelion13.youcandoittoo.user.repository.AuthProviderRepository;
import likelion13.youcandoittoo.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AuthProviderRepository authProviderRepository;

    /**
     * 소셜 로그인 유저 로딩 및 회원가입
     * 동일한 이메일에 대해
     * 다른 소셜 계정이 존재한다면 해당 계정으로 로그인하도록 유도
     * 일반계졍 존재한다면 계정 통합 처리
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = resolveOAuth2Response(registrationId, oAuth2User);
        if (oAuth2Response == null) {
            throw new AuthException(ErrorCode.UNSUPPORTED_OPERATION);
        }

        // 공급자 응답정보 파싱
        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();
        String nickName = oAuth2Response.getName();
        String username = provider + "_" + providerId;

        // 이미 해당 소셜 계정이 존재한다면 CustomOAuth2User 반환
        Optional<AuthProvider> authProviderOpt = authProviderRepository.findByUsername(username);
        if (authProviderOpt.isPresent()) {
            User existingUser = authProviderOpt.get().getUser();

            return new CustomOAuth2User(UserDTO.builder()
                    .email(existingUser.getEmail())
                    .name(existingUser.getNickName())
                    .role(existingUser.getRole())
                    .build());
        }

        // 동일 이메일이 기존 유저(일반/다른 공급자 계정)에 이미 존재하는 경우
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;

        if (userOpt.isPresent()) {
            user = userOpt.get();

            Optional<AuthProvider> existingAuthProvider = authProviderRepository.findByUser(user);
            // 다른 공급자 계정이 존재한다면
            if (existingAuthProvider.isPresent()) {
                String existingProvider = existingAuthProvider.get().getProvider().name();
                if (!existingProvider.equalsIgnoreCase(provider)) {
                    // 추가 소셜 회원가입 제한
                    throw new AuthException(
                            ErrorCode.EMAIL_ALREADY_REGISTERED_WITH_OTHER_PROVIDER,
                            "이 이메일은 " + existingProvider + " 계정으로 이미 등록되어 있습니다. 해당 계정으로 로그인해 주세요."
                    );
                }
            } // 일반 로그인 유저라면 통합 계정으로 처리 (이미 존재하는 user 사용하기에 처리 X)
        } else {
            // 유저가 존재하지 않는다면(소셜 회원가입) 처리 O
            user = User.builder()
                    .email(email)
                    //  (비밀번호는 소셜 로그인 유저이므로 빈값으로 설정)
                    .password("")
                    .nickName(nickName)
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        }

        // AuthProvider 새로 등록
        AuthProvider newProvider = AuthProvider.builder()
                .provider(OauthProvider.valueOf(provider.toUpperCase()))
                .username(username)
                .user(user)
                .build();

        authProviderRepository.save(newProvider);

        return new CustomOAuth2User(UserDTO.builder()
                .email(user.getEmail())
                .name(user.getNickName())
                .role(user.getRole())
                .build());
    }

    private OAuth2Response resolveOAuth2Response(String registrationId, OAuth2User oAuth2User) {

        if (registrationId.equals("naver")) {
            return new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {
            return new KakaoResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
            return new GoogleResponse(oAuth2User.getAttributes());
        }
        return null;
    }
}
