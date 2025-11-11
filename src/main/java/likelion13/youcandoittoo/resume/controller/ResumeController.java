package likelion13.youcandoittoo.resume.controller;

import likelion13.youcandoittoo.resume.dto.ResumeReqDto;
import likelion13.youcandoittoo.resume.dto.ResumeResDto;
import likelion13.youcandoittoo.resume.dto.ResumeUpdateDto;
import likelion13.youcandoittoo.resume.entity.InputType;
import likelion13.youcandoittoo.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeService resumeService;

    private Long getId(Object principal) {
        // 프로젝트의 UserPrincipal에서 id를 추출하도록 구현
        return (Long) principal;
    }

    @PostMapping("/save")
    public ResumeResDto createResume(@RequestHeader("X-USER-ID") Long id,
                                     @RequestBody ResumeReqDto resumeReqDto) {

        return resumeService.create(id, resumeReqDto);
    }

    @GetMapping("/{resumeId}")
    public ResumeResDto getOneResume(@RequestHeader("X-USER-ID") Long id,
                                     @PathVariable Long resumeId) {

        return resumeService.getOneResume(id, resumeId);
    }

    @GetMapping
    public Page<ResumeResDto> list(@RequestHeader("X-USER-ID") Long id,
                                   @RequestParam(required = false) InputType inputType,
                                   Pageable pageable) {

        return resumeService.resumeList(id, inputType, pageable);
    }

    @PutMapping("/{resumeId}")
    public ResumeResDto updateResume(@RequestHeader("X-USER-ID") Long id,
                                     @PathVariable Long resumeId,
                                     @RequestBody ResumeUpdateDto resumeUpdateDto) {

        return resumeService.updateResume(id, resumeId, resumeUpdateDto);
    }

    @DeleteMapping("/{resumeId}")
    public void delete(@RequestHeader("X-USER-ID") Long id,
                       @PathVariable Long resumeId) {

        resumeService.deleteResume(id, resumeId);
    }
}
