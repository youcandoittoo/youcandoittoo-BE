package likelion13.youcandoittoo.resume.repo;

import likelion13.youcandoittoo.resume.entity.InputType;
import likelion13.youcandoittoo.resume.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Page<Resume> findAllByUser_id(Long id, Pageable pageable);
    Page<Resume> findAllByUser_idAndInputType(Long id, InputType inputType, Pageable pageable);
    boolean existsByResumeIdAndUser_id(Long resumeId, Long id);
}
