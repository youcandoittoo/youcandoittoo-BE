package likelion13.youcandoittoo.resume.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeUpdateDto {

    private String title;
    private String company;
    private String domain;
    private String textContent;
}
