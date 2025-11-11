package likelion13.youcandoittoo.resume.dto;

import likelion13.youcandoittoo.resume.entity.InputType;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeReqDto {

    private InputType inputType;
    private String title;
    private String company;
    private String domain;
    private String textContent;
}
