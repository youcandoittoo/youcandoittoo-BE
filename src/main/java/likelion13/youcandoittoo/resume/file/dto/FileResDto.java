package likelion13.youcandoittoo.resume.file.dto;

import likelion13.youcandoittoo.resume.file.entity.FileTargetType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResDto {

    private Long fileId;
    private String fileUrl;
    private Integer sequence;
    private FileTargetType targetType;
    private Long targetId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
