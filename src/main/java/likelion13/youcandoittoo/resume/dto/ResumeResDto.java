package likelion13.youcandoittoo.resume.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResumeResDto {

    private Long resumeId;
    private String inputType;
    private String title;
    private String company;
    private String domain;
    private String textContent;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
