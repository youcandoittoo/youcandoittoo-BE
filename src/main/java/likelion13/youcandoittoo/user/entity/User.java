package likelion13.youcandoittoo.user.entity;

import jakarta.persistence.*;
import likelion13.youcandoittoo.common.entity.BaseEntity;
import lombok.*;

@Entity
@Table(name = "userentity")
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

    @Column(nullable = false)
    private String password;

    @Column(name = "nick_name", nullable = false)
    private String nickName;
}
