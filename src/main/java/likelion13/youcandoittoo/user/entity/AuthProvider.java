package likelion13.youcandoittoo.user.entity;

import jakarta.persistence.*;
import likelion13.youcandoittoo.common.entity.BaseEntity;
import likelion13.youcandoittoo.user.enums.OauthProvider;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthProvider extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OauthProvider provider;

    @Column(nullable = false, unique = true)
    private String username; // provider_providerId 형식으로 저장

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
