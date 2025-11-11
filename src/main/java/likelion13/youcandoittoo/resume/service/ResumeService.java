package likelion13.youcandoittoo.resume.service;


import likelion13.youcandoittoo.resume.dto.ResumeReqDto;
import likelion13.youcandoittoo.resume.dto.ResumeResDto;
import likelion13.youcandoittoo.resume.dto.ResumeUpdateDto;
import likelion13.youcandoittoo.resume.entity.InputType;
import likelion13.youcandoittoo.resume.entity.Resume;
import likelion13.youcandoittoo.resume.repo.ResumeRepository;
import likelion13.youcandoittoo.user.entity.User;
import likelion13.youcandoittoo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    //자기소개서 저장
    public ResumeResDto create(Long id, ResumeReqDto resumeReqDto) {

        validateCreate(resumeReqDto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        Resume resume = Resume.builder()
                .inputType(resumeReqDto.getInputType())
                .title(resumeReqDto.getTitle())
                .company(resumeReqDto.getCompany())
                .domain(resumeReqDto.getDomain())
                .textContent(resumeReqDto.getInputType() == InputType.TEXT ? resumeReqDto.getTextContent() : null)
                .user(user)
                .build();

        Resume saved = resumeRepository.save(resume);
        return toResponse(saved);
    }

    //자소서 세부사항
    @Transactional
    public ResumeResDto getOneResume(Long id, Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new IllegalArgumentException("resume not found"));

        assertOwner(id, resume);
        return toResponse(resume);
    }

    //자소서 전체 목록
    @Transactional
    public Page<ResumeResDto> resumeList(Long id, InputType inputType, Pageable pageable) {

        Page<Resume> page = (inputType == null)
                ? resumeRepository.findAllByUser_id(id, pageable)
                : resumeRepository.findAllByUser_idAndInputType(id, inputType, pageable);
        return page.map(this::toResponse);
    }

    //자소서 수정
    public ResumeResDto updateResume(Long id, Long resumeId, ResumeUpdateDto resumeUpdateDto) {

        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new IllegalArgumentException("resume not found"));
        assertOwner(id, resume);

        if (resumeUpdateDto.getTitle() != null)
            resume.setTitle(resumeUpdateDto.getTitle());
        if (resumeUpdateDto.getCompany() != null)
            resume.setCompany(resumeUpdateDto.getCompany());
        if (resumeUpdateDto.getTextContent() != null)
            resume.setTextContent(resumeUpdateDto.getTextContent());
        if (resume.getInputType() == InputType.TEXT && resumeUpdateDto.getTextContent() != null)
            resume.setTextContent(resumeUpdateDto.getTextContent());

        return toResponse(resume);
    }

    public void deleteResume(Long id, Long resumeId) {

        if(!resumeRepository.existsByResumeIdAndUser_id(resumeId, id)){
            throw new IllegalArgumentException("resume not found");
        }
        resumeRepository.deleteById(resumeId);
    }

    private void assertOwner(Long loginId, Resume r) {
        if (!r.getUser().getId().equals(loginId)) {
            throw new SecurityException("권한이 없습니다.");
        }
    }

    private void validateCreate(ResumeReqDto resumeReqDto) {
        if(resumeReqDto.getInputType() == InputType.TEXT) {
            if(resumeReqDto.getTextContent() == null || resumeReqDto.getTextContent().isBlank()) {
                throw new IllegalArgumentException("TEXT 유형은 textContent가 필수입니다.");
            }
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
                .id(r.getUser().getId())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}