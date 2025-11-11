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

    private Long getUserId(Object principal) {
        // 프로젝트의 UserPrincipal에서 userId를 추출하도록 구현
        return (Long) principal;
    }

    @PostMapping("/save")
    public ResumeResDto createResume(@RequestHeader("X-USER-ID") Long userId,
                                     @RequestBody ResumeReqDto resumeReqDto) {

        return resumeService.create(userId, resumeReqDto);
    }

    @GetMapping("/{resumeId}")
    public ResumeResDto getOneResume(@RequestHeader("X-USER-ID") Long userId,
                                     @PathVariable Long resumeId) {

        return resumeService.getOneResume(userId, resumeId);
    }

    @GetMapping
    public Page<ResumeResDto> list(@RequestHeader("X-USER-ID") Long userId,
                                   @RequestParam(required = false) InputType inputType,
                                   Pageable pageable) {

        return resumeService.resumeList(userId, inputType, pageable);
    }

    @PutMapping("/{resumeId}")
    public ResumeResDto updateResume(@RequestHeader("X-USER-ID") Long userId,
                                     @PathVariable Long resumeId,
                                     @RequestBody ResumeUpdateDto resumeUpdateDto) {

        return resumeService.updateResume(userId, resumeId, resumeUpdateDto);
    }

    @DeleteMapping("/{resumeId}")
    public void delete(@RequestHeader("X-USER-ID") Long userId,
                       @PathVariable Long resumeId) {

        resumeService.deleteResume(userId, resumeId);
    }
}
