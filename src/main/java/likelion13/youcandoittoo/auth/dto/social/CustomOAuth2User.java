package likelion13.youcandoittoo.auth.dto.social;

import likelion13.youcandoittoo.user.UserRole;
import likelion13.youcandoittoo.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userDTO.getRole().name()));
    }

    @Override
    public String getName() {
        return userDTO.getName();
    }

    public String getEmail() {
        return userDTO.getEmail();
    }

    public UserRole getRole() {
        return userDTO.getRole();
    }
}
