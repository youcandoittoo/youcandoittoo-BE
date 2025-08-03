package likelion13.youcandoittoo.user.userDetail;

import likelion13.youcandoittoo.user.entity.User;
import likelion13.youcandoittoo.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("✅ [로그인 요청] 들어온 이메일: " + username);  // 디버깅용 출력

        User userData = userRepository.findByEmail(username);

        if (userData == null) {
            System.out.println("❌ 해당 이메일로 가입된 사용자가 없음");  // 여기 찍히면 문제가 DB에 있음
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        System.out.println("✅ DB에서 찾은 유저: " + userData.getEmail());  // 잘 찾았을 때

        return new CustomUserDetails(userData);
    }

}
