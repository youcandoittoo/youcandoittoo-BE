package likelion13.youcandoittoo.resume.repo;

import likelion13.youcandoittoo.resume.entity.InputType;
import likelion13.youcandoittoo.resume.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Page<Resume> findAllByUser_UserId(Long userId, Pageable pageable);
    Page<Resume> findAllByUser_UserIdAndInputType(Long userId, InputType inputType, Pageable pageable);
    boolean existsByResumeIdAndUser_UserId(Long resumeId, Long userId);
}
