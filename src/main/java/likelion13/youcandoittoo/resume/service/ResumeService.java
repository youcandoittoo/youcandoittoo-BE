package likelion13.youcandoittoo.resume.service;

import likelion13.youcandoittoo.resume.dto.ResumeResDto;
import likelion13.youcandoittoo.resume.dto.ResumeUpdateDto;
import likelion13.youcandoittoo.resume.entity.Resume;
import likelion13.youcandoittoo.resume.enums.InputType;
import likelion13.youcandoittoo.resume.file.entity.File;
import likelion13.youcandoittoo.resume.file.entity.FileTargetType;
import likelion13.youcandoittoo.resume.file.repo.FileRepository;
import likelion13.youcandoittoo.resume.file.service.FileService;
import likelion13.youcandoittoo.resume.repo.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final FileRepository uploadFileRepository;
    private final FileService s3Service;

    /** 공통 생성: TEXT / FILE 모두 처리 */
    public ResumeResDto createResume(String email,
                               InputType inputType,
                               String title,
                               String company,
                               String domain,
                               String textContent,
                               MultipartFile file) {

        validate(inputType, textContent, file);

        // 1) 우선 Resume 저장 (id 뽑기 위해)
        Resume resume = Resume.builder()
                .inputType(inputType)
                .title(title)
                .company(company)
                .domain(domain)
                .textContent(inputType == InputType.TEXT ? textContent : null)
                .email(email)
                .build();

        Resume saved = resumeRepository.save(resume);

        // 2) FILE 타입이면 S3 업로드 + file 테이블 저장
        if (inputType == InputType.FILE) {
            String url = s3Service.uploadPdf(file, "resumes/");

            File stored = File.builder()
                    .fileUrl(url)
                    .sequence(1)
                    .targetType(FileTargetType.RESUME)
                    .targetId(saved.getResumeId())
                    .build();

            uploadFileRepository.save(stored);
        }

        return toResponse(saved);
    }

    /** 공통 수정: TEXT / FILE 모두 처리 (타입 변경도 가능) */
    public ResumeResDto updateResume(String email,
                                     Long resumeId,
                                     InputType inputType,
                                     ResumeUpdateDto dto,
                                     MultipartFile file) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("resume not found"));
        assertOwner(email, resume);

        String title = dto.getTitle();
        String company = dto.getCompany();
        String domain = dto.getDomain();
        String textContent = dto.getTextContent();

        validate(inputType, textContent, file);

        // 공통 메타데이터 수정
        if (title != null)   resume.setTitle(title);
        if (company != null) resume.setCompany(company);
        if (domain != null)  resume.setDomain(domain);

        // 현재 연결된 파일 row 조회
        Optional<File> maybeFile =
                uploadFileRepository.findByTargetTypeAndTargetId(FileTargetType.RESUME, resumeId);

        if (inputType == InputType.TEXT) {
            // TEXT 로 바꾸는 경우: 기존 파일 있으면 S3 + DB 삭제
            maybeFile.ifPresent(f -> {
                s3Service.deleteByUrl(f.getFileUrl());
                uploadFileRepository.delete(f);
            });
            resume.setInputType(InputType.TEXT);
            resume.setTextContent(textContent);

        } else if (inputType == InputType.FILE) {
            // FILE 로 저장/변경하는 경우: 새 파일 필수
            String newUrl = s3Service.uploadPdf(file, "resumes/");

            if (maybeFile.isPresent()) {
                // 기존 파일 있으면 교체
                File existing = maybeFile.get();
                s3Service.deleteByUrl(existing.getFileUrl());
                existing.setFileUrl(newUrl);
            } else {
                // 기존 파일 없으면 새 row 생성
                uploadFileRepository.save(
                        File.builder()
                                .fileUrl(newUrl)
                                .sequence(1)
                                .targetType(FileTargetType.RESUME)
                                .targetId(resumeId)
                                .build()
                );
            }

            resume.setInputType(InputType.FILE);
            resume.setTextContent(null); // 필요하면 TEXT 유지해도 됨
        }

        return toResponse(resume);
    }

    // 자소서 세부사항
    @Transactional(readOnly = true)
    public ResumeResDto getOneResume(String email, Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("resume not found"));
        assertOwner(email, resume);
        return toResponse(resume);
    }

    // 자소서 전체 목록
    @Transactional(readOnly = true)
    public Page<ResumeResDto> resumeList(String email, InputType inputType, Pageable pageable) {

        Page<Resume> page = (inputType == null)
                ? resumeRepository.findAllByEmail(email, pageable)
                : resumeRepository.findAllByEmailAndInputType(email, inputType, pageable);
        return page.map(this::toResponse);
    }

    // 자소서 삭제 (파일까지 같이 삭제)
    public void deleteResume(String email, Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("resume not found"));
        assertOwner(email, resume);

        // FILE 이면 파일도 삭제
        uploadFileRepository.findByTargetTypeAndTargetId(FileTargetType.RESUME, resumeId)
                .ifPresent(f -> {
                    s3Service.deleteByUrl(f.getFileUrl());
                    uploadFileRepository.delete(f);
                });

        resumeRepository.delete(resume);
    }

    /** TEXT/FILE 공통 유효성 검사 */
    private void validate(InputType inputType, String textContent, MultipartFile file) {
        if (inputType == InputType.TEXT) {
            if (textContent == null || textContent.isBlank()) {
                throw new IllegalArgumentException("TEXT 유형은 textContent가 필수입니다.");
            }
        } else if (inputType == InputType.FILE) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("FILE 유형은 file이 필수입니다.");
            }
            if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
                throw new IllegalArgumentException("PDF 파일만 업로드 가능합니다.");
            }
        }
    }

    private void assertOwner(String email, Resume r) {
        if (!r.getEmail().equals(email)) {
            throw new SecurityException("권한이 없습니다.");
        }
    }

    private ResumeResDto toResponse(Resume r) {
        return ResumeResDto.builder()
                .resumeId(r.getResumeId())
                .inputType(r.getInputType().name())
                .title(r.getTitle())
                .company(r.getCompany())
                .domain(r.getDomain())
                .textContent(r.getTextContent())
                .email(r.getEmail())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
