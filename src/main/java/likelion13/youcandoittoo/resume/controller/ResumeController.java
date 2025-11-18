package likelion13.youcandoittoo.resume.controller;

import likelion13.youcandoittoo.resume.dto.*;
import likelion13.youcandoittoo.resume.entity.InputType;
import jakarta.validation.Valid;
import likelion13.youcandoittoo.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    private String getEmail(String principal) {

        return principal;
    }

    // 공통 생성: inputType을 함께 보냄 (TEXT/FILE)
    @PostMapping
    public ResumeResDto create(
            @AuthenticationPrincipal String principal,
            @RequestBody @Valid ResumeReqDto req
    ) {
        String email = getEmail(principal);
        return resumeService.create(email, req);
    }

    @GetMapping("/{id}")
    public ResumeResDto getOne(
            @AuthenticationPrincipal String principal,
            @PathVariable Long id
    ) {
        String email = getEmail(principal);
        return resumeService.getOneResume(email, id);
    }

    // 목록: ?inputType=TEXT (없으면 전체)
    @GetMapping
    public Page<ResumeResDto> list(
            @AuthenticationPrincipal String principal,
            @RequestParam(required = false) InputType inputType,
            Pageable pageable
    ) {
        String email = getEmail(principal);
        return resumeService.resumeList(email, inputType, pageable);
    }

    @PutMapping("/{id}")
    public ResumeResDto update(
            @AuthenticationPrincipal String principal,
            @PathVariable Long id,
            @RequestBody @Valid ResumeUpdateDto req
    ) {
        String email = getEmail(principal);
        return resumeService.updateResume(email, id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @AuthenticationPrincipal String principal,
            @PathVariable Long id
    ) {
        String email = getEmail(principal);
        resumeService.deleteResume(email, id);
    }
}
