package likelion13.youcandoittoo.auth.dto.social;

import likelion13.youcandoittoo.auth.dto.LoginType;
import likelion13.youcandoittoo.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // 팀 내에서 관리자 필요 없어서 유저에 대해서만 고려하기로 함
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return userDTO.getName();
    }

    public String getUsername() {
        return userDTO.getUsername();
    }

    public LoginType getLoginType() {
        return userDTO.getLoginType();
    }
}
