package likelion13.youcandoittoo.auth.security;

import likelion13.youcandoittoo.auth.dto.local.CustomUserDetails;
import likelion13.youcandoittoo.global.exception.custom.AuthException;
import likelion13.youcandoittoo.global.exception.error.ErrorCode;
import likelion13.youcandoittoo.user.entity.User;
import likelion13.youcandoittoo.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_CREDENTIALS, "email{" + username + "}로부터 유저를 찾을 수 없습니다."));

        return new CustomUserDetails(user);
    }
}
