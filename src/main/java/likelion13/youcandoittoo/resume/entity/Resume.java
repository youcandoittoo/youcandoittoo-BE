package likelion13.youcandoittoo.resume.entity;

import likelion13.youcandoittoo.common.entity.BaseEntity;
import likelion13.youcandoittoo.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InputType inputType;

    @Column(length = 500)
    private String title;

    @Column(length = 50)
    private String company;

    @Column(length = 50)
    private String domain;

    @Column(length = 1500)
    private String textContent;

    @Column(nullable = false)
    private String email;

}
