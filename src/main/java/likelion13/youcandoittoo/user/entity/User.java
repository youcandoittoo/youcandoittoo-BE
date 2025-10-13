package likelion13.youcandoittoo.user.entity;

import jakarta.persistence.*;
import likelion13.youcandoittoo.common.entity.BaseEntity;
import likelion13.youcandoittoo.user.UserRole;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 DB에 저장
    @Column(nullable = false)
    private UserRole role;
}
