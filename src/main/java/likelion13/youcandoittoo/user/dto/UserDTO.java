package likelion13.youcandoittoo.user.dto;

import likelion13.youcandoittoo.user.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {

    private final String email;
    private final String name;
    private final UserRole role;
}