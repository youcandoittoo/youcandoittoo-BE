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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = resolveOAuth2Response(registrationId, oAuth2User);

        CustomOAuth2User existingOAuthUser = getExistingSocialUserIfPresent(oAuth2Response);
        if (existingOAuthUser != null) {
            return existingOAuthUser;
        }

        User user = findOrCreateUser(oAuth2Response);

        ensureNoOtherProviderOrThrow(user, oAuth2Response);

        linkProviderIfAbsent(user, oAuth2Response);

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
        throw new AuthException(ErrorCode.UNSUPPORTED_OPERATION);
    }

    private String buildUsername(OAuth2Response resp) {
        return resp.getProvider() + "_" + resp.getProviderId();
    }

    private CustomOAuth2User getExistingSocialUserIfPresent(OAuth2Response oAuth2Response) {
        String username = buildUsername(oAuth2Response);
        Optional<AuthProvider> authProviderOpt = authProviderRepository.findByUsername(username);

        if (authProviderOpt.isPresent()) {
            User existingUser = authProviderOpt.get().getUser();
            return new CustomOAuth2User(UserDTO.builder()
                    .email(existingUser.getEmail())
                    .name(existingUser.getNickName())
                    .role(existingUser.getRole())
                    .build());
        }

        return null;
    }

    private User findOrCreateUser(OAuth2Response oAuth2Response) {
        return userRepository.findByEmail(oAuth2Response.getEmail())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(oAuth2Response.getEmail())
                                .password("")
                                .nickName(oAuth2Response.getName())
                                .role(UserRole.USER)
                                .build()
                ));
    }

    private void ensureNoOtherProviderOrThrow(User user, OAuth2Response oAuth2Response) {
        authProviderRepository.findByUser(user).ifPresent(existing -> {
            String existingProvider = existing.getProvider().name();
            if (!existingProvider.equalsIgnoreCase(oAuth2Response.getProvider())) {
                throw new AuthException(
                        ErrorCode.EMAIL_ALREADY_REGISTERED_WITH_OTHER_PROVIDER,
                        "이 이메일은 " + existingProvider + " 계정으로 이미 등록되어 있습니다. 해당 계정으로 로그인해 주세요."
                );
            }
        });
    }

    private void linkProviderIfAbsent(User user, OAuth2Response oAuth2Response) {
        String username = buildUsername(oAuth2Response);
        OauthProvider providerName = OauthProvider.toOauthProvider(oAuth2Response.getProvider());

        if (authProviderRepository.findByUsername(username).isPresent()) {
            return;
        }

        AuthProvider newProvider = AuthProvider.builder()
                .provider(providerName)
                .username(username)
                .user(user)
                .build();

        authProviderRepository.save(newProvider);
    }

}
