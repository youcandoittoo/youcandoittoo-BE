package likelion13.youcandoittoo.resume.controller;

import jakarta.validation.constraints.NotNull;
import likelion13.youcandoittoo.resume.dto.*;
import likelion13.youcandoittoo.resume.enums.InputType;
import likelion13.youcandoittoo.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    private String getEmail(String principal) {

        return principal;
    }

    /**
     * TEXT / FILE 공통 생성
     * - TEXT  : textContent 필수, file 없음
     * - FILE  : file 필수(PDF), textContent는 무시
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeResDto create(
            @AuthenticationPrincipal String principal,
            @RequestParam @NotNull InputType inputType,   // "TEXT" or "FILE"
            @RequestParam String title,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String textContent,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        String email = getEmail(principal);
        return resumeService.createResume(email, inputType, title, company, domain, textContent, file);
    }

    @GetMapping("/{resumeId}")
    public ResumeResDto getOne(
            @AuthenticationPrincipal String principal,
            @PathVariable Long resumeId
    ) {
        String email = getEmail(principal);
        return resumeService.getOneResume(email, resumeId);
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

    // 2) 수정 (TEXT/FILE 공통)
    public ResumeResDto update(
            @AuthenticationPrincipal String principal,
            @PathVariable Long id,
            @RequestParam InputType inputType,
            @RequestParam String title,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String textContent,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        String email = principal;

        // 메타데이터용 DTO (서비스에서 재사용하기 쉽게)
        ResumeUpdateDto dto = ResumeUpdateDto.builder()
                .title(title)
                .company(company)
                .domain(domain)
                .textContent(textContent)
                .build();

        return resumeService.updateResume(email, id, inputType, dto, file);
    }

    @DeleteMapping("/{resumeId}")
    public void delete(
            @AuthenticationPrincipal String principal,
            @PathVariable Long resumeId
    ) {
        String email = getEmail(principal);
        resumeService.deleteResume(email, resumeId);
    }
}
