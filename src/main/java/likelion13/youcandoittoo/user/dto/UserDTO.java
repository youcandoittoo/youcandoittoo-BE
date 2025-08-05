package likelion13.youcandoittoo.user.dto;

import likelion13.youcandoittoo.auth.dto.LoginType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {

    private final String username;
    private final String name;
    private final LoginType loginType;
}