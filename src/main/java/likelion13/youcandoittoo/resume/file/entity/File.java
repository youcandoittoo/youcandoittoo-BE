package likelion13.youcandoittoo.resume.file.entity;

import jakarta.persistence.*;
import likelion13.youcandoittoo.common.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(length = 500)
    private String fileUrl;

    private Integer sequence;

    @Enumerated(EnumType.STRING)
    private FileTargetType targetType;

    private Long targetId;
}
