package likelion13.youcandoittoo.user.service;

import likelion13.youcandoittoo.user.dto.JoinDto;
import likelion13.youcandoittoo.user.entity.User;
import likelion13.youcandoittoo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<String> joinProcess(JoinDto joinDto) {

        String email = joinDto.getEmail();
        String password = joinDto.getPassword();
        String nickname = joinDto.getNickName();

        Optional<User> isExist = userRepository.getByEmail(email);

        if (isExist.isPresent()) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 아이디입니다.");
        }

        User data = new User();

        data.setEmail(email);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setNickName(nickname);

        userRepository.save(data);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("회원가입 성공");
    }
}
