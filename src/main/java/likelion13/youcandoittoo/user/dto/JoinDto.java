package likelion13.youcandoittoo.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class JoinDto {

    private String email;
    private String password;
    private String nickName;
}
